package com.example.androidcanvas.Clock


import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.annotation.Nullable
import androidx.core.graphics.withRotation
import kotlin.math.cos
import kotlin.math.sin


class ClockView : View {

    constructor(context: Context?) : super(context) {

    }

    constructor(context: Context?, @Nullable attrs: AttributeSet?) : super(context, attrs) {

    }

    constructor(context: Context?, @Nullable attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
    }


    constructor(
        context: Context?,
        @Nullable attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
    }

    var cx = width.toFloat() / 2
    var cy = height.toFloat()
    var angle = 0f
    val centerVisibleWeight = 0
    val minTime = 0
    val maxTime = 60
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            cx = width.toFloat() / 2
            cy = height.toFloat() / 2
            val radius = 350f
            val scalewidth = 50f
            val outerRadius = radius + scalewidth / 2
            radius - scalewidth / 2

            it.drawCircle(cx, cy, radius, Paint().apply {
                flags = Paint.ANTI_ALIAS_FLAG
                strokeWidth = scalewidth
                color = Color.WHITE
                style = Paint.Style.FILL_AND_STROKE
                setShadowLayer(60f, 0f, 0f, Color.rgb(50, 0, 0))
            })

            // draw lines

            angle = 0f
            for (currentTime in minTime..maxTime) {
                val angleForEachLineInRad =
                    (angle - 90) * (Math.PI / 180).toFloat()
                var lineColor = Color.WHITE
                var lineLength = 20f
                angle += 6
                when {
                    currentTime % 5 == 0 -> {
                        lineColor = Color.GREEN
                        lineLength = 60f
                        if (currentTime != 0)
                            showTimeNumber(
                                currentTime / 5,
                                it,
                                angleForEachLineInRad,
                                outerRadius,
                                cx,
                                cy,
                                lineLength
                            )
                    }
                    else -> {
                        lineColor = Color.GRAY
                        lineLength = 20f
                    }
                }
                val lineStartX = (outerRadius - lineLength) * cos(angleForEachLineInRad) + cx
                val lineStartY = (outerRadius - lineLength) * sin(angleForEachLineInRad) + cy
                val lineEndX = (outerRadius) * cos(angleForEachLineInRad) + cx
                val lineEndY = (outerRadius) * sin(angleForEachLineInRad) + cy

                it.drawLine(lineStartX, lineStartY, lineEndX, lineEndY, Paint().apply {
                    flags = Paint.ANTI_ALIAS_FLAG
                    color = lineColor
                    strokeWidth = 5f
                })

            }

            // show arrows from middle.

            // seconds
            it.withRotation(degrees = seconds * (360f / 60f), cx, cy) {
                this.drawLine(cx, cy, cx, cy - radius + 60f + 20f, Paint().apply {
                    flags = Paint.ANTI_ALIAS_FLAG
                    color = Color.RED
                    strokeWidth = 5f
                    strokeCap = Paint.Cap.ROUND
                })
            }

            // minutes
            it.withRotation(degrees = minutes * (360f / 60f), cx, cy) {
                this.drawLine(cx, cy, cx, cy - radius + 120f, Paint().apply {
                    flags = Paint.ANTI_ALIAS_FLAG
                    color = Color.BLUE
                    strokeWidth = 10f
                    strokeCap = Paint.Cap.ROUND
                })
            }

            // hours
            it.withRotation((hours + 1) * (360f / 12f), cx, cy) {
                this.drawLine(cx, cy, cx, cy - radius + 140f, Paint().apply {
                    flags = Paint.ANTI_ALIAS_FLAG
                    color = Color.MAGENTA
                    strokeWidth = 12f
                    strokeCap = Paint.Cap.ROUND
                })
            }


        }
    }


    private fun showTimeNumber(
        currentTime: Int,
        it: Canvas,
        angleForEachLineInRad: Float,
        outerRadius: Float,
        cx: Float,
        cy: Float,
        lineLength: Float
    ) {
        val x = (outerRadius - lineLength - 30f) * cos(angleForEachLineInRad) + cx
        val y = (outerRadius - lineLength - 30f) * sin(angleForEachLineInRad) + cy
        it.withRotation(
            angleForEachLineInRad * (180f / Math.PI.toFloat()) + 90f,
            pivotX = x, pivotY = y
        ) {
            it.drawText(currentTime.toString(),
                x, y,
                Paint().apply {
                    flags = Paint.ANTI_ALIAS_FLAG
                    textSize = 30f
                    textAlign = Paint.Align.CENTER
                    textAlign=Paint.Align.CENTER
                    isFakeBoldText=true
                })
        }
    }

    private var seconds = 0f
    private var minutes = 0f
    private var hours = 0f
    fun updateView(sec: Float, min: Float, hrs: Float) {
        seconds = sec
        minutes = min
        hours = hrs
        invalidate()
    }

}