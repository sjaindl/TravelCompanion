package com.sjaindl.travelcompanion.plan.detail.expandable

import android.annotation.SuppressLint
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.sjaindl.travelcompanion.R
import com.sjaindl.travelcompanion.plan.detail.PlanDetailItem
import com.sjaindl.travelcompanion.plan.detail.PlanDetailItemType
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun ExpandableCard(
    card: ExpandableCardModel,
    planDetailItems: List<PlanDetailItem>,
    onCardArrowClick: () -> Unit,
    onAdd: () -> Unit,
    onClick: (plannableId: String) -> Unit,
    expanded: Boolean,
) {
    val transitionState = remember {
        MutableTransitionState(expanded).apply {
            targetState = !expanded
        }
    }
    val transition = rememberTransition(transitionState, label = "transition")

    val cardBgColor by transition.animateColor({
        tween(durationMillis = EXPAND_ANIMATION_DURATION)
    }, label = "bgColorTransition") {
        if (expanded) colorScheme.onPrimary else colorScheme.secondaryContainer
    }

    val cardContentColor by transition.animateColor({
        tween(durationMillis = EXPAND_ANIMATION_DURATION)
    }, label = "bgColorTransition") {
        if (expanded) colorScheme.secondaryContainer else colorScheme.onPrimary
    }

    val cardPaddingHorizontal by transition.animateDp({
        tween(durationMillis = EXPAND_ANIMATION_DURATION)
    }, label = "paddingTransition") {
        if (expanded) 24.dp else 48.dp
    }

    val cardElevation by transition.animateDp({
        tween(durationMillis = EXPAND_ANIMATION_DURATION)
    }, label = "elevationTransition") {
        if (expanded) 24.dp else 4.dp
    }

    val cardRoundedCorners by transition.animateDp({
        tween(
            durationMillis = EXPAND_ANIMATION_DURATION,
            easing = FastOutSlowInEasing
        )
    }, label = "cornersTransition") {
        if (expanded) 8.dp else 16.dp
    }

    val arrowRotationDegree by transition.animateFloat({
        tween(durationMillis = EXPAND_ANIMATION_DURATION)
    }, label = "rotationDegreeTransition") {
        if (expanded) 0f else -90f
    }

    val interactionSource = remember { MutableInteractionSource() }

    Card(
        colors = CardColors(
            containerColor = cardBgColor,
            contentColor = cardContentColor,
            disabledContainerColor = cardBgColor.copy(alpha = 0.4f),
            disabledContentColor = cardContentColor.copy(alpha = 0.4f),
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = cardElevation,
        ),
        shape = RoundedCornerShape(cardRoundedCorners),
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = cardPaddingHorizontal,
                vertical = 8.dp
            )
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) {
                        onCardArrowClick()
                    },
            ) {
                ConstraintLayout(modifier = Modifier.fillMaxSize()) {
                    val (arrowRef, titleRef, addRef) = createRefs()

                    /*
                    val chainRef = createHorizontalChain(arrowRef, titleRef, addRef, chainStyle = ChainStyle.SpreadInside)
                    constrain(chainRef) {
                        start.linkTo(arrowRef.start)
                        end.linkTo(addRef.end)
                    }
                     */

                    CardArrow(
                        modifier = Modifier.constrainAs(arrowRef) {
                            start.linkTo(parent.start)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                        },
                        degrees = arrowRotationDegree,
                        onClick = onCardArrowClick,
                    )

                    val title = when (card.type) {
                        PlanDetailItemType.HOTEL -> stringResource(id = R.string.hotels)
                        PlanDetailItemType.RESTAURANT -> stringResource(id = R.string.restaurants)
                        PlanDetailItemType.ATTRACTION -> stringResource(id = R.string.attractions)
                    }
                    CardTitle(
                        modifier = Modifier.constrainAs(titleRef) {
                            start.linkTo(arrowRef.end)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            //end.linkTo(addRef.start)
                        },
                        title = "$title (${planDetailItems.size})",
                    )

                    CardIcon(
                        modifier = Modifier.constrainAs(addRef) {
                            //start.linkTo(titleRef.end)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            end.linkTo(parent.end)
                        },
                        image = Icons.Default.Add,
                        onClick = onAdd,
                    )
                }

            }
            ExpandableContent(
                planDetailItems = planDetailItems,
                visible = expanded,
                onClick = onClick
            )
        }
    }
}

@Preview
@Composable
fun ExpandableCardPreview() {
    val model = ExpandableCardModel(
        id = 1,
        type = PlanDetailItemType.HOTEL,
    )

    val expanded by remember {
        mutableStateOf(true)
    }

    TravelCompanionTheme {
        ExpandableCard(
            card = model,
            planDetailItems = listOf(
                PlanDetailItem(title = "title1", details = "detail1", attributionWithText = null, imagePath = null, id = "1"),
                PlanDetailItem(title = "title2", details = "detail2", attributionWithText = null, imagePath = null, id = "2"),
            ),
            onCardArrowClick = { },
            onAdd = { },
            expanded = expanded,
            onClick = { },
        )
    }
}
