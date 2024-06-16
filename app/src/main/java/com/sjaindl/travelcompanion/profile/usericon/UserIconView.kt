package com.sjaindl.travelcompanion.profile.usericon

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.Typeface
import android.text.TextPaint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.sjaindl.travelcompanion.R

class UserIconView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private val backgroundPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = ContextCompat.getColor(context, R.color.primaryFill)
        }
    }

    private val textPaint by lazy {
        TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            textSize = 40f
            typeface = Typeface.DEFAULT_BOLD
            color = ContextCompat.getColor(context, R.color.textLight)
        }
    }

    var initials: String? = null

    fun updateView(initialsBitmap: Bitmap?) {
        if (initialsBitmap != null) {
            val resizedBitmap = Bitmap.createScaledBitmap(initialsBitmap, 100, 180, true)
            setImageBitmap(resizedBitmap)
        } else {
            setImageResource(R.drawable.ic_user_placeholder)
        }
    }

    fun updateView(initials: String?) {
        val initialsBitmap = initials?.takeIf { it.isNotEmpty() }?.let { bitmapFromText(it.uppercase()) }
        if (initialsBitmap != null) {
            // setImageBitmap(initialsBitmap)
        } else {
            setImageResource(R.drawable.ic_user_placeholder)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        updateView(initials = initials)
    }

    private fun bitmapFromText(text: String): Bitmap? {
        if (measuredWidth == 0 || measuredHeight == 0) return null
        val bitmapConfig = Bitmap.Config.ARGB_8888
        val bitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, bitmapConfig)
        val canvas = Canvas(bitmap)
        val radius = if (measuredHeight > measuredWidth) {
            measuredWidth / 2f
        } else {
            measuredHeight / 2f
        }
        canvas.drawCircle(measuredWidth / 2f, measuredHeight / 2f, radius, backgroundPaint)
        val bounds = Rect()

        textPaint.textSize = getFitTextSize(textPaint, (radius * 1.5f), text)
        textPaint.getTextBounds(text, 0, text.length, bounds)
        val x = (bitmap.width - bounds.width()) / 2f
        val y = (bitmap.height + bounds.height()) / 2f
        canvas.drawText(text, x, y, textPaint)
        return bitmap
    }

    private fun getFitTextSize(paint: TextPaint, width: Float, text: String): Float {
        val nowWidth = paint.measureText(text)
        return width / nowWidth * paint.textSize
    }
}
