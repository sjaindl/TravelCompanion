package com.sjaindl.travelcompanion.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextPaint

object BitmapUtils {
    fun bitmapFromText(
        text: String,
        width: Int,
        height: Int,
        backgroundPaint: Paint,
        textPaint: TextPaint,
    ): Bitmap? {
        if (width == 0 || height == 0) return null

        val bitmapConfig = Bitmap.Config.ARGB_8888
        val bitmap = Bitmap.createBitmap(width, height, bitmapConfig)
        val canvas = Canvas(bitmap)
        val radius = if (height > width) {
            width / 2f
        } else {
            height / 2f
        }
        canvas.drawCircle(width / 2f, height / 2f, radius, backgroundPaint)
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
