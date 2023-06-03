package com.sjaindl.travelcompanion.plan.detail.expandable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sjaindl.travelcompanion.plan.detail.PlanDetailItem
import com.sjaindl.travelcompanion.plan.detail.PlanDetailItemScreen

const val EXPAND_ANIMATION_DURATION = 500

private const val FADE_IN_ANIMATION_DURATION = 500
private const val FADE_OUT_ANIMATION_DURATION = 500
private const val COLLAPSE_ANIMATION_DURATION = 500

@Composable
fun ExpandableContent(
    planDetailItems: List<PlanDetailItem>,
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    onClick: (plannableId: String) -> Unit,
) {
    val enterFadeIn = remember {
        fadeIn(
            animationSpec = TweenSpec(
                durationMillis = FADE_IN_ANIMATION_DURATION,
                easing = FastOutLinearInEasing,
            )
        )
    }

    val enterExpand = remember {
        expandVertically(animationSpec = tween(EXPAND_ANIMATION_DURATION))
    }

    val exitFadeOut = remember {
        fadeOut(
            animationSpec = TweenSpec(
                durationMillis = FADE_OUT_ANIMATION_DURATION,
                easing = LinearOutSlowInEasing,
            )
        )
    }

    val exitCollapse = remember {
        shrinkVertically(animationSpec = tween(COLLAPSE_ANIMATION_DURATION))
    }

    AnimatedVisibility(
        visible = visible,
        enter = enterExpand + enterFadeIn,
        exit = exitCollapse + exitFadeOut
    ) {
        Column(modifier = modifier.padding(8.dp)) {
            Column(
                modifier = Modifier
                //.height((planDetailItems.size * 115).dp)
                //.verticalScroll(rememberScrollState())

            ) {
                //items(planDetailItems) {
                planDetailItems.forEach {
                    PlanDetailItemScreen(
                        modifier = Modifier,
                        planDetailItem = it,
                        onClick = onClick
                    )
                }
            }
        }
    }
}
