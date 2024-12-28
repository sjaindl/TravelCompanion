package com.sjaindl.travelcompanion.baseui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sjaindl.travelcompanion.R
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme

@Composable
fun DisplayItem(
    modifier: Modifier = Modifier,
    title: String? = null,
    @DrawableRes icon: Int? = null,
    imageVector: ImageVector? = null,
    subTitle: String? = null,
    onClick: () -> Unit = { },
) {
    DisplayItem(icon = icon, imageVector = imageVector, onClick = onClick, modifier = modifier) {
        Column(modifier = Modifier.weight(1f)) {
            if (title != null) {
                Text(
                    text = title,
                    color = colorScheme.background,
                    fontSize = 20.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (subTitle != null) {
                Text(
                    text = subTitle,
                    color = colorScheme.background,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun DisplayItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int? = null,
    imageVector: ImageVector? = null,
    content: @Composable RowScope.() -> Unit,
) {
    TravelCompanionTheme {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .background(
                    color = colorScheme.onBackground,
                    shape = RoundedCornerShape(8.dp),
                )
                .clip(RoundedCornerShape(8.dp))
                .clickable(onClick = onClick)
                .padding(all = 4.dp)
        ) {
            if (icon != null) {
                Image(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                )
            } else if (imageVector != null) {
                Image(
                    imageVector = imageVector,
                    contentDescription = null,
                )
            }
            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DisplayItemPreview() {
    TravelCompanionTheme {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DisplayItem(
                title = "This is an item",
                subTitle = "with a subtitle",
                modifier = Modifier.fillMaxWidth(),
                onClick = {}
            )
            DisplayItem(
                modifier = Modifier.fillMaxWidth(),
                icon = R.drawable.bin,
                title = "This is an item with icon",
                subTitle = "and a subtitle",
                onClick = {}
            )
        }
    }
}
