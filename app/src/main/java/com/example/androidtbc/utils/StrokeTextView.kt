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

    private var strokeWidth = 4f
    private var strokeColor = Color.parseColor("#0296E5")
    private var fillColor = Color.parseColor("#242A32")

    init {
        setTextColor(fillColor)
    }

    override fun onDraw(canvas: Canvas) {
        val paint = paint
        val textPaintStyle = paint.style
        val textPaintStrokeWidth = paint.strokeWidth
        val textColor = currentTextColor

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = strokeWidth
        setTextColor(strokeColor)
        super.onDraw(canvas)

        paint.style = Paint.Style.FILL
        setTextColor(fillColor)
        super.onDraw(canvas)

       paint.style = textPaintStyle
        paint.strokeWidth = textPaintStrokeWidth
        setTextColor(textColor)
    }

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