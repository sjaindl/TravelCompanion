package com.sjaindl.travelcompanion.profile.usericon

import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Typeface
import android.text.TextPaint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
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
) {
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

    val scope = rememberCoroutineScope()

    val viewState by viewModel.userIconViewState.collectAsState()

    when (val state = viewState) {
        is UserIconViewModel.UserIconViewState.Initial -> {
            LoadingAnimation()
        }

        is UserIconViewModel.UserIconViewState.Content -> {
            Box {
                AndroidView(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .clickable {
                            onClickedProfile()
                        },
                    factory = {
                        UserIconView(context)
                    },
                    update = { userIconView ->
                        scope.launch {
                            val initials = state.initials ?: "Anonymous".first().toString()
                            userIconView.initials = initials

                            val bitmap = BitmapUtils.bitmapFromText(
                                text = state.initials ?: "Anonymous".first().toString(),
                                width = screenWidth * 25,
                                height = screenHeight * 25,
                                backgroundPaint = backgroundPaint,
                                textPaint = textPaint,
                            )

                            userIconView.updateView(initialsBitmap = bitmap)
                        }
                    },
                )
            }
        }
    }
}
