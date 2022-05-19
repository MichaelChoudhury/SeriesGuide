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
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
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
                    onBackPressed = { onBackPressed() },
                    onOpenWebsite = { viewUrl(R.string.url_website) },
                    onOpenPrivacyPolicy = { viewUrl(R.string.url_privacy) }
                )
            }
        }
    }

    @Composable
    fun About(
        onBackPressed: () -> Unit,
        onOpenWebsite: () -> Unit,
        onOpenPrivacyPolicy: () -> Unit
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
                        Button(onClick = onOpenWebsite) {
                            Text(text = stringResource(id = R.string.website))
                        }
                        Button(onClick = onOpenPrivacyPolicy) {
                            Text(text = stringResource(id = R.string.privacy_policy))
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
            {},
            {},
            {}
        )
    }

//    private fun setupActionBar() {
//        val toolbar = findViewById<Toolbar>(R.id.sgToolbar)
//        setSupportActionBar(toolbar)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//    }

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