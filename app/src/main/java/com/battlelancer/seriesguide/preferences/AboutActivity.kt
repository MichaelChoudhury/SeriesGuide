package com.battlelancer.seriesguide.preferences

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.battlelancer.seriesguide.ui.SeriesGuidePreferences
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
                About()
            }
        }
    }

    @Composable
    fun About() {
        Text(text = "About SeriesGuide")
    }

    @Preview
    @Composable
    fun AboutPreview() {
        About()
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

}