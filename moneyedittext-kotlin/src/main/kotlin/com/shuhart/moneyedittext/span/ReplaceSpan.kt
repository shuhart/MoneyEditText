package com.shuhart.moneyedittext.span

import android.graphics.Canvas
import android.graphics.Paint
import android.text.style.ReplacementSpan

open class ReplaceSpan(private val replaceText: String = "") : ReplacementSpan() {
    override fun getSize(paint: Paint, text: CharSequence?, start: Int, end: Int, fm: Paint.FontMetricsInt?): Int =
            Math.round(paint.measureText(replaceText, 0, replaceText.length))

    override fun draw(canvas: Canvas, text: CharSequence?, start: Int, end: Int, x: Float, top: Int, y: Int, bottom: Int, paint: Paint?) {
        canvas.drawText(replaceText, x, y.toFloat(), paint)
    }

}