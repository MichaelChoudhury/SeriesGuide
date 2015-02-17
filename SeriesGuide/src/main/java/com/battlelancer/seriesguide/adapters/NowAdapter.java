/*
 * Copyright 2014 Uwe Trottmann
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.battlelancer.seriesguide.adapters;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.battlelancer.seriesguide.R;
import com.battlelancer.seriesguide.adapters.model.HeaderData;
import com.battlelancer.seriesguide.util.ServiceUtils;
import com.battlelancer.seriesguide.util.TimeTools;
import com.battlelancer.seriesguide.util.Utils;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Sectioned adapter displaying recently watched episodes, episodes released today and episodes
 * recently watched by trakt friends.
 */
public class NowAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    static class DefaultViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView description;
        TextView timestamp;
        ImageView poster;
        ImageView type;

        public DefaultViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.textViewHistoryTitle);
            description = (TextView) itemView.findViewById(R.id.textViewHistoryDescription);
            timestamp = (TextView) itemView.findViewById(R.id.textViewHistoryTimestamp);
            poster = (ImageView) itemView.findViewById(R.id.imageViewHistoryPoster);
            type = (ImageView) itemView.findViewById(R.id.imageViewHistoryType);
        }
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public TextView title;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.textViewNowHeader);
        }
    }

    static class MoreViewHolder extends RecyclerView.ViewHolder {
        public TextView title;

        public MoreViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.textViewNowMoreText);
        }
    }

    static class FriendViewHolder extends RecyclerView.ViewHolder {
        public TextView show;
        public TextView episode;
        public TextView timestamp;
        public ImageView poster;
        public TextView username;
        public ImageView avatar;
        public ImageView type;

        public FriendViewHolder(View itemView) {
            super(itemView);
            show = (TextView) itemView.findViewById(R.id.textViewFriendShow);
            episode = (TextView) itemView.findViewById(R.id.textViewFriendEpisode);
            timestamp = (TextView) itemView.findViewById(R.id.textViewFriendTimestamp);
            poster = (ImageView) itemView.findViewById(R.id.imageViewFriendPoster);
            username = (TextView) itemView.findViewById(R.id.textViewFriendUsername);
            avatar = (ImageView) itemView.findViewById(R.id.imageViewFriendAvatar);
            type = (ImageView) itemView.findViewById(R.id.imageViewFriendActionType);
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ ViewType.DEFAULT, ViewType.HEADER, ViewType.MORE_LINK, ViewType.FRIEND })
    public @interface ViewType {
        static final int DEFAULT = 0;
        static final int HEADER = 1;
        static final int MORE_LINK = 2;
        static final int FRIEND = 3;
    }

    private final int resIdDrawableCheckin;
    private final int resIdDrawableWatched;
    private final Context context;

    private List<NowItem> dataset;
    private List<HeaderData> headers;
    private List<NowItem> recentlyWatched;
    private List<NowItem> releasedToday;
    private List<NowItem> friendsRecently;

    /**
     * Lock used to modify the content of {@link #dataset}. Any write operation performed on the
     * array should be synchronized on this lock.
     */
    private final Object lock = new Object();

    public enum NowType {
        RELEASED_TODAY(0),
        RECENTLY_WATCHED(1),
        FRIENDS(2),
        RECENTLY_MORE_LINK(1);

        private final int headerId;

        private NowType(int headerId) {
            this.headerId = headerId;
        }
    }

    public static class NowItem {
        public Integer episodeTvdbId;
        public Integer showTvdbId;
        public long timestamp;
        public String title;
        public String description;
        public String poster;
        public String username;
        public String avatar;
        public String action;
        @ViewType public int type;

        public NowItem recentlyWatched(int episodeTvdbId, long timestamp, String show,
                String episode, String poster) {
            setCommonValues(timestamp, show, episode, poster);
            this.episodeTvdbId = episodeTvdbId;
            this.type = ViewType.DEFAULT;
            return this;
        }

        public NowItem recentlyWatchedTrakt(Integer episodeTvdbId, Integer showTvdbId,
                long timestamp, String show, String episode, String poster) {
            setCommonValues(timestamp, show, episode, poster);
            this.episodeTvdbId = episodeTvdbId;
            this.showTvdbId = showTvdbId;
            this.type = ViewType.DEFAULT;
            return this;
        }

        public NowItem releasedToday(int episodeTvdbId, long timestamp, String show,
                String episode, String poster) {
            setCommonValues(timestamp, show, episode, poster);
            this.episodeTvdbId = episodeTvdbId;
            this.type = ViewType.DEFAULT;
            return this;
        }

        public NowItem friend(Integer episodeTvdbId, Integer showTvdbId, long timestamp,
                String show, String episode, String poster, String username, String avatar,
                String action) {
            setCommonValues(timestamp, show, episode, poster);
            this.episodeTvdbId = episodeTvdbId;
            this.showTvdbId = showTvdbId;
            this.username = username;
            this.avatar = avatar;
            this.action = action;
            this.type = ViewType.FRIEND;
            return this;
        }

        public NowItem header(String title) {
            this.type = ViewType.HEADER;
            this.title = title;
            return this;
        }

        public NowItem moreLink(String title) {
            this.type = ViewType.MORE_LINK;
            this.title = title;
            return this;
        }

        private void setCommonValues(long timestamp, String show, String episode, String poster) {
            this.timestamp = timestamp;
            this.title = show;
            this.description = episode;
            this.poster = poster;
        }
    }

    public NowAdapter(Context context) {
        this.context = context;
        this.dataset = new ArrayList<>();
        this.resIdDrawableCheckin = Utils.resolveAttributeToResourceId(context.getTheme(),
                R.attr.drawableCheckin);
        this.resIdDrawableWatched = Utils.resolveAttributeToResourceId(context.getTheme(),
                R.attr.drawableWatch);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == ViewType.DEFAULT) {
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_history, viewGroup, false);
            return new DefaultViewHolder(v);
        } else if (viewType == ViewType.HEADER) {
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_now_header, viewGroup, false);
            return new HeaderViewHolder(v);
        } else if (viewType == ViewType.MORE_LINK) {
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_now_more, viewGroup, false);
            return new MoreViewHolder(v);
        } else if (viewType == ViewType.FRIEND) {
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_friend, viewGroup, false);
            return new FriendViewHolder(v);
        } else {
            throw new IllegalArgumentException("Using unrecognized view type.");
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        NowItem item = getItem(position);

        if (viewHolder instanceof DefaultViewHolder) {
            DefaultViewHolder holder = (DefaultViewHolder) viewHolder;

            holder.title.setText(item.title);
            holder.description.setText(item.description);
            holder.timestamp.setText(
                    TimeTools.formatToLocalRelativeTime(getContext(), new Date(item.timestamp)));

            if (item.poster != null && item.poster.startsWith("http")) {
                // is a trakt poster
                Utils.loadSmallPoster(getContext(), holder.poster, item.poster);
            } else {
                // is a TVDb (only path then, so build URL) or no poster
                Utils.loadSmallTvdbShowPoster(getContext(), holder.poster, item.poster);
            }
        } else if (viewHolder instanceof HeaderViewHolder) {
            HeaderViewHolder holder = (HeaderViewHolder) viewHolder;

            holder.title.setText(item.title);
        } else if (viewHolder instanceof MoreViewHolder) {
            MoreViewHolder holder = (MoreViewHolder) viewHolder;

            holder.title.setText(item.title);
        } else if (viewHolder instanceof FriendViewHolder) {
            FriendViewHolder holder = (FriendViewHolder) viewHolder;

            holder.show.setText(item.title);
            holder.episode.setText(item.description);
            holder.timestamp.setText(
                    TimeTools.formatToLocalRelativeTime(getContext(), new Date(item.timestamp)));
            holder.username.setText(item.username);
            // trakt poster urls
            ServiceUtils.loadWithPicasso(getContext(), item.poster).into(holder.poster);
            ServiceUtils.loadWithPicasso(getContext(), item.avatar).into(holder.avatar);

            // action type indicator
            if ("watch".equals(item.action)) {
                // marked watched
                holder.type.setImageResource(resIdDrawableWatched);
            } else {
                // check-in, scrobble
                holder.type.setImageResource(resIdDrawableCheckin);
            }
        }
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).type;
    }

    private Context getContext() {
        return context;
    }

    public NowItem getItem(int position) {
        return dataset.get(position);
    }

    public synchronized void setRecentlyWatched(List<NowItem> items) {
        recentlyWatched = items;
        reloadData();
    }

    public synchronized void setReleasedTodayData(List<NowItem> items) {
        releasedToday = items;
        reloadData();
    }

    public synchronized void setFriendsRecentlyWatched(List<NowItem> items) {
        friendsRecently = items;
        reloadData();
    }

    private void reloadData() {
        synchronized (lock) {
            dataset.clear();
            if (releasedToday != null) {
                dataset.addAll(releasedToday);
            }
            if (recentlyWatched != null) {
                dataset.addAll(recentlyWatched);
            }
            if (friendsRecently != null) {
                dataset.addAll(friendsRecently);
            }
        }
        notifyDataSetChanged();
    }
}
