package com.example.androidtbc.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class StrokeTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    // Default stroke width and colors
    private var strokeWidth = 4f
    private var strokeColor = Color.parseColor("#0296E5") // Dark color from your UI
    private var fillColor = Color.parseColor("#242A32")   // Blue fill color from your UI

    init {
        // Set the initial text color to the fill color
        setTextColor(fillColor)
    }

    override fun onDraw(canvas: Canvas) {
        // Save the text paint values
        val paint = paint
        val textPaintStyle = paint.style
        val textPaintStrokeWidth = paint.strokeWidth
        val textColor = currentTextColor

        // First draw the stroke
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = strokeWidth
        setTextColor(strokeColor)
        super.onDraw(canvas)

        // Then draw the fill
        paint.style = Paint.Style.FILL
        setTextColor(fillColor)
        super.onDraw(canvas)

        // Restore the original paint values
        paint.style = textPaintStyle
        paint.strokeWidth = textPaintStrokeWidth
        setTextColor(textColor)
    }

    // Setter methods for customization
    fun setStrokeWidth(width: Float) {
        this.strokeWidth = width
        invalidate()
    }

    fun setStrokeColor(color: Int) {
        this.strokeColor = color
        invalidate()
    }

    fun setFillColor(color: Int) {
        this.fillColor = color
        invalidate()
    }
}