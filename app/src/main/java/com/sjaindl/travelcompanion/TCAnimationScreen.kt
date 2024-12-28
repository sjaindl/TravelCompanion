package com.sjaindl.travelcompanion

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme

@Composable
fun TCAnimationScreen(
    onDismiss: () -> Unit,
) {
    var reverse by remember {
        mutableStateOf(value = false)
    }

    BackHandler(enabled = true) {
        onDismiss()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "TCInfiniteTransition")

    val color by infiniteTransition.animateColor(
        initialValue = colorScheme.primary,
        targetValue = colorScheme.background,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "AnimatedColor"
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = if (reverse) 0f else 360f,
        targetValue = if (reverse) 360f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "AnimatedRotation"
    )

    val alphaValue by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "AnimatedAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = color),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(id = R.mipmap.ic_launcher_foreground),
            contentDescription = "TC Animation",
            modifier = Modifier
                .size(size = 400.dp)
                .clip(shape = CircleShape)
                .border(width = 2.dp, color = Color.Gray, shape = CircleShape)
                .rotate(degrees = rotation)
                .clickable {
                    reverse = !reverse
                },
            alpha = alphaValue,
            contentScale = ContentScale.FillBounds,
        )
    }
}

@Composable
fun TCAnimationScreenWithoutRecompositions(
    onDismiss: () -> Unit,
) {
    var reverse by remember {
        mutableStateOf(value = false)
    }

    BackHandler(enabled = true) {
        onDismiss()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "TCInfiniteTransition")

    val color by infiniteTransition.animateColor(
        initialValue = colorScheme.primary,
        targetValue = colorScheme.background,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "AnimatedColor"
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = if (reverse) 0f else 360f,
        targetValue = if (reverse) 360f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "AnimatedRotation"
    )

    val alphaValue by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "AnimatedAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                drawRect(color)
            },
        contentAlignment = Alignment.Center,
    ) {

        Image(
            painter = painterResource(id = R.mipmap.ic_launcher_foreground),
            contentDescription = "TC Animation",
            modifier = Modifier
                .size(size = 400.dp)
                .clip(shape = CircleShape)
                .border(width = 2.dp, color = Color.Gray, shape = CircleShape)
                .graphicsLayer {
                    rotationZ = rotation
                    alpha = alphaValue
                }
                .clickable {
                    reverse = !reverse
                },
            contentScale = ContentScale.FillBounds,
        )
    }
}

@Preview
@Composable
fun TCAnimationScreenPreview() {
    TravelCompanionTheme {
        TCAnimationScreenWithoutRecompositions(
            onDismiss = { },
        )
    }
}
