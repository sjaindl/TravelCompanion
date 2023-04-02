package com.sjaindl.travelcompanion.baseui

import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp

@Composable
fun TCLink(link: String) {
    val annotatedString = buildAnnotatedString {
        append(link)

        addStyle(
            style = SpanStyle(
                color = Color(0xff64B5F6),
                fontSize = 18.sp,
                textDecoration = TextDecoration.Underline
            ),
            start = 0,
            end = link.length
        )

        addStringAnnotation(
            tag = "URL",
            annotation = link,
            start = 0,
            end = link.length
        )
    }

    val uriHandler = LocalUriHandler.current
    ClickableText(text = annotatedString) {
        annotatedString.getStringAnnotations(tag = "URL", start = it, end = it).firstOrNull()?.let { annotation ->
            uriHandler.openUri(annotation.item)
        }
    }
}
