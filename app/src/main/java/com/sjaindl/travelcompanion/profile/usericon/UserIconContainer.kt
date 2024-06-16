package com.sjaindl.travelcompanion.profile.usericon

import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Typeface
import android.text.TextPaint
import androidx.activity.compose.ReportDrawnWhen
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sjaindl.travelcompanion.R
import com.sjaindl.travelcompanion.util.BitmapUtils
import com.sjaindl.travelcompanion.util.LoadingAnimation
import kotlinx.coroutines.launch

@Composable
fun UserIconContainer(
    viewModel: UserIconViewModel = viewModel(),
    onClickedProfile: () -> Unit = { },
) = trace(sectionName = "UserIconContainer") {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current

    val screenHeight = configuration.screenHeightDp
    val screenWidth = configuration.screenWidthDp

    val backgroundPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = ContextCompat.getColor(context, R.color.primaryFill)
        }
    }

    val textPaint by lazy {
        TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            textSize = 40f
            typeface = Typeface.DEFAULT_BOLD
            color = ContextCompat.getColor(context, R.color.textLight)
        }
    }

    var profileIsDrawn by remember {
        mutableStateOf(value = false)
    }

    val scope = rememberCoroutineScope()

    val viewState by viewModel.userIconViewState.collectAsState()

    ReportDrawnWhen {
        profileIsDrawn
    }

    when (val state = viewState) {
        is UserIconViewModel.UserIconViewState.Initial -> {
            LoadingAnimation()
        }

        is UserIconViewModel.UserIconViewState.Content -> {
            Box(
                modifier = if (profileIsDrawn) {
                    Modifier.testTag("fullyDrawn")
                } else {
                    Modifier
                },
            ) {
                AndroidView(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .wrapContentSize()
                        .clickable {
                            onClickedProfile()
                        },
                    factory = {
                        UserIconView(context)
                    },
                    update = { userIconView ->
                        scope.launch {
                            trace(sectionName = "LoadProfileBitmap") {
                                val initials = state.initials ?: "Anonymous".first().toString()
                                userIconView.initials = initials

                                val bitmap = BitmapUtils.bitmapFromText(
                                    text = initials,
                                    width = screenWidth,
                                    height = screenHeight,
                                    backgroundPaint = backgroundPaint,
                                    textPaint = textPaint,
                                )

                                userIconView.updateView(initialsBitmap = bitmap)
                                profileIsDrawn = true
                            }
                        }
                    },
                )
            }
        }
    }
}
