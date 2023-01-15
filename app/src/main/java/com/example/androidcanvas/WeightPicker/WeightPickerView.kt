package com.example.androidcanvas.WeightPicker

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.Nullable
import androidx.core.graphics.withRotation
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin


class WeightPickerView : View {

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
    val centerVisibleWeight = 60
    val minWeight = 30
    val maxWeight = 100
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            cx = width.toFloat() / 2
            cy = height.toFloat()
            val radius = 1000f
            val scalewidth = 400f
            val outerRadius = radius + scalewidth / 2
            val innerRadius = radius - scalewidth / 2

            it.drawCircle(cx, cy, radius, Paint().apply {
                flags = Paint.ANTI_ALIAS_FLAG
                strokeWidth = scalewidth
                color = Color.WHITE
                style = Paint.Style.STROKE
                setShadowLayer(60f, 0f, 0f, Color.rgb(50, 0, 0))
            })

            // draw lines


            for (currentWeight in minWeight..maxWeight) {
                val angleForEachLineInRad =
                    (currentWeight - centerVisibleWeight + angle - 90) * (Math.PI / 180).toFloat()
                var lineColor = Color.WHITE
                var lineLength = 20f
                when {
                    currentWeight % 10 == 0 -> {
                        lineColor = Color.BLACK
                        lineLength = 60f
                        showWeightNumber(
                            currentWeight,
                            it,
                            angleForEachLineInRad,
                            outerRadius,
                            cx,
                            cy,
                            lineLength
                        )
                    }
                    currentWeight % 5 == 0 -> {
                        lineColor = Color.GREEN
                        lineLength = 40f
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

            showWeightIndicator(it, innerRadius, cx, cy)

        }
    }

    private fun showWeightIndicator(it: Canvas, innerRadius: Float, cx: Float, cy: Float) {

        val middlePoints = Point(cx, cy - innerRadius - 150f)
        val bottomLeftPoints = Point(cx - 4f, cy - innerRadius)
        val bottomRightPoint = Point(cx + 4f, cy - innerRadius)

        it.drawPath(Path().apply {
            moveTo(middlePoints.x, middlePoints.y)
            lineTo(bottomLeftPoints.x, bottomLeftPoints.y)
            lineTo(bottomRightPoint.x, bottomRightPoint.y)
            moveTo(middlePoints.x, middlePoints.y)
        }, Paint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            color = Color.GREEN
        })

    }

    data class Point(val x: Float, val y: Float)


    private fun showWeightNumber(
        currentWeight: Int,
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
            it.drawText(currentWeight.toString(),
                x, y,
                Paint().apply {
                    flags = Paint.ANTI_ALIAS_FLAG
                    textSize = 30f
                    textAlign = Paint.Align.CENTER

                })
        }
    }

    var x1 = 0f
    var y1 = 0f
    var x2 = 0f
    var y2 = 0f
    var isDragStarted = false
    var dragStartedAngle = 0f
    var oldAngle = 0f
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                x1 = event.x
                y1 = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                x2 = event.x
                y2 = event.y
                if (Math.abs(x1 - x2) > 0f || Math.abs(y1 - y2) > 0f) {
                    if (!isDragStarted) {
                        isDragStarted = true
                        onDragStart(event)
                    }
                }
            }
            MotionEvent.ACTION_UP -> if (isDragStarted) {
                isDragStarted = false
                onDragEnd(event)
            }
        }
        val touchAngle = -atan2(cx - event.x, cy - event.y) * (180f / Math.PI).toFloat()
        val newAngle = oldAngle + (touchAngle - dragStartedAngle)
        angle = newAngle.coerceIn(
            (centerVisibleWeight - maxWeight).toFloat(),
            (centerVisibleWeight - minWeight).toFloat()
        )
        invalidate()
        change?.onWeightChange((centerVisibleWeight - angle).roundToInt())
        return super.onTouchEvent(event)
    }


    private fun onDragStart(it: MotionEvent) {
        println("dcba on drag start")
        dragStartedAngle = -atan2(cx - it.x, cy - it.y) * (180f / Math.PI).toFloat()
    }

    private fun onDragEnd(it: MotionEvent) {
        println("dcba on drag end...")
        oldAngle = angle

    }

    fun interface WeightChange {
        fun onWeightChange(weight: Int)
    }

    private var change: WeightChange? = null
    fun setOnWeightChangeListener(change: WeightChange) {
        this.change = change
    }


//    override fun dispatchDragEvent(event: DragEvent?): Boolean {
//        println("dcba on drag dispatch called..........")
//        return super.dispatchDragEvent(event)
//    }

//    override fun onDrag(p0: View?, event: DragEvent?): Boolean {
//        println("dcba draggedd..")
//        event?.let {
//            if (it.action == DragEvent.ACTION_DRAG_STARTED) {
//                dragStartedAngle = -atan2(cx - it.x, cy - it.y) * (180f / Math.PI).toFloat()
//
//            } else if (it.action == DragEvent.ACTION_DRAG_ENDED) {
//                oldAngle = angle
//            }
//            val touchAngle = -atan2(cx - it.x, cy - it.y) * (180f / Math.PI).toFloat()
//            val newAngle = oldAngle + (touchAngle - dragStartedAngle)
//            angle = newAngle.coerceIn(
//                (centerVisibleWeight - maxWeight).toFloat(),
//                (centerVisibleWeight - minWeight).toFloat()
//            )
//            invalidate()
//        }
//        return true
//    }

}