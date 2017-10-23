package com.shuhart.moneyedittext.span

import android.graphics.Canvas
import android.graphics.Paint
import android.text.style.ReplacementSpan

class DecoratorSpan(private val before: String = "", private val after: String = ""): ReplacementSpan() {

    override fun getSize(paint: Paint, text: CharSequence, start: Int, end: Int, fm: Paint.FontMetricsInt?): Int {
        val newCharSequence = before + text.subSequence(start, end) + after
        return Math.round(paint.measureText(newCharSequence, 0, newCharSequence.length))
    }

    override fun draw(canvas: Canvas, text: CharSequence, start: Int, end: Int, x: Float, top: Int, y: Int, bottom: Int, paint: Paint?) {
        val newCharSequence = before + text.subSequence(start, end) + after
        canvas.drawText(newCharSequence, x, y.toFloat(), paint)
    }

}