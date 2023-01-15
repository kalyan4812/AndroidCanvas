package com.example.androidcanvas.Shapes

import android.content.Context
import android.graphics.*
import android.graphics.Shader.TileMode
import android.os.Handler
import android.view.View

class CustomView(context: Context) : View(context) {

    val rect = Rect()
    var paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            canvasRectangle(it)
            canvasCircle(it)
            canvasArc(it)
            canvasOval(it)
            canvasLine(it)
            canvasText(it)
        }
    }

    private fun canvasText(it: Canvas) {
        it.drawText("Canvas Text...", 100f, 1600f, Paint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            color = Color.RED
            textSize = 50f
        })
    }

    private fun canvasLine(it: Canvas) {
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.setColor(Color.MAGENTA)
        paint.strokeWidth = 20f
        it.drawLine(100f, 1300f, 300f, 1500f, paint)


        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.setColor(Color.LTGRAY)
        paint.strokeWidth = 20f
        paint.pathEffect = DiscretePathEffect(
            50f,
            90f
        ) // Chop the path into lines of segmentLength, randomly deviating from the original path by deviation.
        it.drawLine(
            400f,
            1300f,
            600f,
            1300f,
            paint
        )// length of line=200px,each segment=50px,so there will four segments.
    }

    private fun canvasOval(it: Canvas) {
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.setColor(Color.CYAN)
        it.drawOval(50f, 900f, 200f, 1250f, paint)
    }

    private fun canvasArc(it: Canvas) {
        // connect to centre (0-270 clockwise) ,0 degree is along x axis.
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.setColor(Color.RED)
        it.drawArc(50f, 550f, 200f, 850f, 0f, 270f, true, paint)

        // no connection to center.
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.setColor(Color.RED)
        it.drawArc(250f, 550f, 550f, 850f, 0f, 270f, false, paint)

        // stroke with connection to center
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.setColor(Color.GREEN)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 20f
        it.drawArc(600f, 550f, 750f, 700f, 270f, 270f, true, paint)


        // progress bar
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.setColor(Color.LTGRAY)
        paint.style = Paint.Style.STROKE
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeWidth = 20f
        it.drawArc(800f, 550f, 950f, 700f, 0f, 360f, false, paint)
        paint.setColor(Color.GREEN)
        it.drawArc(800f, 550f, 950f, 700f, 270f, progress, false, paint)
    }

    init {
        fillProgress()
    }

    var progress = 0f
    fun fillProgress() {
        Handler().postDelayed({
            progress += 10f
            invalidate() // view invalidate() method is used when a redraw is needed.
            if (progress <= 360f) {
                fillProgress()
            }
        }, 50L)
    }

    private fun canvasCircle(it: Canvas) {

        //solid circle--(here cx,cy are calculated from centre top of the circle)
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.style = Paint.Style.FILL
        paint.setColor(Color.BLUE)
        it.drawCircle(100f, 200f, 100f, paint)


        // circle with stroke
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 10f
        paint.setColor(Color.GREEN)
        it.drawCircle(350f, 200f, 100f, paint)


        // circle with dotted stroke
        paint.style = Paint.Style.STROKE
        paint.setColor(Color.YELLOW)
        paint.strokeWidth = 10f
        paint.pathEffect = DashPathEffect(floatArrayOf(100f, 20f, 20f, 20f), 90f)
        it.drawCircle(600f, 200f, 100f, paint)

        // gradient circle
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.shader = RadialGradient(
            850f,
            200f,
            100f,
            Color.YELLOW,
            Color.RED,
            TileMode.MIRROR
        ) // note here radius is like width
        // of each yellow stroke.
        it.drawCircle(850f, 200f, 100f, paint)

        // spiral circle
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.shader = RadialGradient(
            100f,
            400f,
            5f,
            Color.YELLOW,
            Color.RED,
            TileMode.MIRROR
        ) // yellow -centre color,red-edge color
        it.drawCircle(100f, 400f, 100f, paint)

        // multi color circle.
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.shader = RadialGradient(
            400f, 400f, 100f, intArrayOf(
                Color.YELLOW, Color.RED, Color.GREEN, Color.BLUE, Color.BLACK
            ),
            floatArrayOf(0.2f, 0.4f, 0.6f, 0.8f, 1f), TileMode.MIRROR
        ) // float array is stopping point/area %  of mentioned colors inside circle.
        it.drawCircle(400f, 400f, 100f, paint)
    }

    private fun canvasRectangle(it: Canvas) {
        // all values here are pixel values.
        // drawing rectangle ,here (0,0) will be top left corner of our screen.--1
        rect.left = 0
        rect.top = 0
        rect.bottom = rect.top + 80
        rect.right = rect.left + 80
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.setColor(Color.RED)// -- paint by default will fill the area.
        it.drawRect(rect, paint)

        // rectangle with stroke--2
        rect.left = 100
        rect.top = 10
        rect.bottom = rect.top + 80
        rect.right = rect.left + 80
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.setColor(Color.RED)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 10f
        it.drawRect(rect, paint)

        // rectangle inside 2nd rectangle
        rect.left = 110
        rect.top = 20
        rect.bottom = rect.top + 20
        rect.right = rect.left + 20
        paint.style =
            Paint.Style.FILL // u need to again change paint style,if u want ti have filled style ,since it was set ti stroke above.
        paint.setColor(Color.GREEN)
        it.drawRect(rect, paint)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
    }


}