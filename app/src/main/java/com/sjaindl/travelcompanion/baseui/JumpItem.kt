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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sjaindl.travelcompanion.R
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme

@Composable
fun JumpItem(
    title: String,
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int? = null,
    subTitle: String? = null,
    onClick: () -> Unit,
) {
    JumpItem(icon = icon, onClick = onClick, modifier = modifier) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                modifier = Modifier.fillMaxWidth(),
                color = colorScheme.background,
                fontSize = 20.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
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
fun JumpItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int? = null,
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
            }
            content()
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun JumpItemPreview() {
    TravelCompanionTheme {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            JumpItem(
                title = "This is an item",
                subTitle = "with a subtitle",
                modifier = Modifier.fillMaxWidth(),
                onClick = {}
            )
            JumpItem(
                icon = R.drawable.bin,
                title = "This is an item with icon",
                subTitle = "and a subtitle",
                modifier = Modifier.fillMaxWidth(),
                onClick = {}
            )
        }
    }
}
