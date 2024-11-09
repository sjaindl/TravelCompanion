package com.sjaindl.travelcompanion.baseui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
fun TCLink(url: String, title: String, modifier: Modifier = Modifier, fontSize: TextUnit = 18.sp) {
    val annotatedString = buildAnnotatedString {
        append(title)

        addStyle(
            style = SpanStyle(
                color = Color(0xff64B5F6),
                fontSize = fontSize,
                textDecoration = TextDecoration.Underline
            ),
            start = 0,
            end = title.length
        )

        withLink(LinkAnnotation.Url(url = url)) {
            append(url)
        }
    }

    Text(
        text = annotatedString,
        modifier = modifier,
    )
}
