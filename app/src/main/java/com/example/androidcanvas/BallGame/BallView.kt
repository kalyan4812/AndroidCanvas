package com.example.androidcanvas.BallGame

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.Nullable
import com.example.androidcanvas.R
import kotlin.random.Random


class BallView : View {

    constructor(context: Context?) : super(context) {
        init(null)
    }

    constructor(context: Context?, @Nullable attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context?, @Nullable attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(attrs)
    }


    constructor(
        context: Context?,
        @Nullable attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(attrs)
    }

    private lateinit var rect: Rect
    private lateinit var paint: Paint
    private var color: Int = 0
    private var size: Int = 0
    private fun init(attrs: AttributeSet?) {
        rect = Rect()
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(
                attrs,
                R.styleable.BallView
            )
            color = typedArray.getColor(
                R.styleable.BallView_circle_color,
                Color.BLACK
            )
            size = typedArray.getDimensionPixelSize(
                R.styleable.BallView_circle_radius,
                100
            )
            paint.color = color
            typedArray.recycle()
        }
    }

    fun changeColor() {
        paint.color = if (paint.color == color) Color.RED else color
        // invalidate(); synchrounous.
        postInvalidate() //aysnchrounous.
    }

    private var circleX: Float = 0f
    private var circleY: Float = 0f

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            if (circleX == 0f || circleY == 0f) {
                circleX = width.toFloat() / 2;
                circleY = height.toFloat() / 2;
            }
            canvas.drawCircle(
                circleX, circleY,
                size.toFloat(),
                paint
            )
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            if (it.action == MotionEvent.ACTION_DOWN && isBallClickable) {
                val x = it.x
                val y = it.y
                // checking whether user touched coordinate is on/inside the circle or not.
                // checking whether user touched coordinate is on/inside the circle or not.
                val dx = Math.pow(x - circleX.toDouble(), 2.0)
                val dy = Math.pow(y - circleY.toDouble(), 2.0)
                if (dx + dy < Math.pow(size.toDouble(), 2.0)) {
                    circleX = Random.nextInt(size, width - size).toFloat()
                    circleY = Random.nextInt(size, height - size).toFloat()
                    invalidate()
                    ballClick?.onBallClick()
                    return true
                }
            }
        }
        return super.onTouchEvent(event)
    }

    private var isBallClickable = false
    fun enableOrDisableClick(isenable: Boolean) {
        isBallClickable = isenable
    }

    fun interface BallClick {
        fun onBallClick()
    }

    private var ballClick: BallClick? = null
    fun setBallClickListener(ballClick: BallClick) {
        this.ballClick = ballClick
    }


}

