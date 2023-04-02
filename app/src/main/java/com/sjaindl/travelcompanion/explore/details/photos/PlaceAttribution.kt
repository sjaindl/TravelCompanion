package com.sjaindl.travelcompanion.explore.details.photos

import android.text.Html
import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView

// https://medium.com/@theAndroidDeveloper/displaying-html-text-in-jetpack-compose-7b801bb028c6
@Composable
fun PlaceAttribution(link: String) {
    val spannedText = Html.fromHtml(link, Html.FROM_HTML_MODE_LEGACY)
    /*
    HTML Link is something like:
     <a href="https://maps.google.com/maps/contrib/108933587345666100977">Jassim Anwar</a>
     */
    AndroidView(
        modifier = Modifier,
        factory = {
            TextView(it).apply {
                //autoLinkMask = Linkify.WEB_URLS
                linksClickable = true
                movementMethod = LinkMovementMethod.getInstance()
                setLinkTextColor(Color.White.toArgb())
            }
        },
        update = {
            it.text = spannedText
        }
    )
}
