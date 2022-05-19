package com.battlelancer.seriesguide.preferences

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.battlelancer.seriesguide.R
import com.battlelancer.seriesguide.ui.SeriesGuidePreferences
import com.battlelancer.seriesguide.util.Utils
import com.google.android.material.composethemeadapter3.Mdc3Theme

/**
 * Displays details about the app version, links to credits and terms.
 */
class AboutActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(SeriesGuidePreferences.THEME)
        super.onCreate(savedInstanceState)

        setContent {
            Mdc3Theme {
                About(
                    versionString = Utils.getVersionString(this),
                    onBackPressed = { onBackPressed() },
                    onOpenWebsite = { viewUrl(R.string.url_website) },
                    onOpenPrivacyPolicy = { viewUrl(R.string.url_privacy) },
                    onOpenCredits = { viewUrl(R.string.url_credits) },
                    onOpenTmdbTerms = { viewUrl(R.string.url_terms_tmdb) },
                    onOpenTmdbApiTerms = { viewUrl(R.string.url_terms_tmdb_api) },
                    onOpenTraktTerms = { viewUrl(R.string.url_terms_trakt) }
                )
            }
        }
    }

    @Composable
    fun About(
        versionString: String,
        onBackPressed: () -> Unit,
        onOpenWebsite: () -> Unit,
        onOpenPrivacyPolicy: () -> Unit,
        onOpenCredits: () -> Unit,
        onOpenTmdbTerms: () -> Unit,
        onOpenTmdbApiTerms: () -> Unit,
        onOpenTraktTerms: () -> Unit
    ) {
        Scaffold(
            topBar = {
                SmallTopAppBar(
                    title = { Text(text = stringResource(id = R.string.prefs_category_about)) },
                    navigationIcon = {
                        IconButton(onClick = onBackPressed) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = stringResource(id = R.string.navigate_back)
                            )
                        }
                    }
                )
            },
            content = { scaffoldPadding ->
                Box(modifier = Modifier.padding(scaffoldPadding)) {
                    Column() {
                        Text(
                            text = stringResource(id = R.string.app_name),
                            style = MaterialTheme.typography.headlineLarge
                        )
                        Text(
                            text = versionString,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        FilledTonalButton(onClick = onOpenWebsite) {
                            Text(text = stringResource(id = R.string.website))
                        }
                        FilledTonalButton(onClick = onOpenPrivacyPolicy) {
                            Text(text = stringResource(id = R.string.privacy_policy))
                        }
                        Text(
                            text = stringResource(id = R.string.about_open_source),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                        FilledTonalButton(onClick = onOpenCredits) {
                            Text(text = stringResource(id = R.string.licences_and_credits))
                        }
                        Text(
                            text = stringResource(id = R.string.licence_themoviedb),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                        FilledTonalButton(onClick = onOpenTmdbTerms) {
                            Text(text = stringResource(id = R.string.tmdb_terms))
                        }
                        FilledTonalButton(onClick = onOpenTmdbApiTerms) {
                            Text(text = stringResource(id = R.string.tmdb_api_terms))
                        }
                        Text(
                            text = stringResource(id = R.string.licence_trakt),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                        FilledTonalButton(onClick = onOpenTraktTerms) {
                            Text(text = stringResource(id = R.string.trakt_terms))
                        }
                    }
                }
            }
        )
    }

    @Preview
    @Composable
    fun AboutPreview() {
        About(
            "v42 (Database v42)",
            {},
            {},
            {},
            {},
            {},
            {},
            {}
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun viewUrl(@StringRes urlResId: Int) {
        Utils.launchWebsite(this, getString(urlResId))
    }

}