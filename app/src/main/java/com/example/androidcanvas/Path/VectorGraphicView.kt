package com.example.androidcanvas.Path

import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.core.graphics.PathParser
import androidx.core.graphics.withClip
import androidx.core.graphics.withScale
import androidx.core.graphics.withTranslation
import com.example.androidcanvas.R

class VectorGraphicView : View {
    var currentSelection: SELECTION = SELECTION.SUN

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

    var sunvalueholder = PropertyValuesHolder.ofFloat(
        "percentage",
        0f,
        if (currentSelection == SELECTION.SUN) 80f else 0f
    )
    var smilevalueholder = PropertyValuesHolder.ofFloat(
        "percentage",
        0f,
        if (currentSelection == SELECTION.SMILE) 80f else 0f
    )
    var sunpercentage = 0f
    var smilepercentage = 0f

    fun animatePic() {
        sunvalueholder = PropertyValuesHolder.ofFloat(
            "percentage",
            if (currentSelection == SELECTION.SUN) 0f else 80f,
            if (currentSelection == SELECTION.SUN) 80f else 0f
        )
        smilevalueholder = PropertyValuesHolder.ofFloat(
            "percentage",
            if (currentSelection == SELECTION.SMILE) 0f else 80f,
            if (currentSelection == SELECTION.SMILE) 80f else 0f
        )
        ValueAnimator().apply {
            setValues(sunvalueholder)
            duration = 500
            interpolator = LinearInterpolator()
            addUpdateListener {
                sunpercentage = it.getAnimatedValue("percentage") as Float
                invalidate()
            }
        }.start()
        ValueAnimator().apply {
            setValues(smilevalueholder)
            duration = 500
            interpolator = LinearInterpolator()
            addUpdateListener {
                smilepercentage = it.getAnimatedValue("percentage") as Float
                println("percen : " + smilepercentage + " " + sunpercentage)
                invalidate()
            }
        }.start()
    }


    var sunBounds = RectF()
    var smileBounds = RectF()
    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {

            val center = Point(width.toFloat() / 2, height.toFloat() / 2)
            val scaleFactor = 15f
            val sunPath = context.resources.getString(R.string.sun_path)
            val smilePath = context.resources.getString(R.string.smile_path)

            val parseSunPath = PathParser.createPathFromPathData(sunPath)
            val parseSmilePath = PathParser.createPathFromPathData(smilePath)



            parseSunPath.computeBounds(sunBounds, false)
            parseSmilePath.computeBounds(smileBounds, false)

            val sunTranslationOffset = Point(
                center.x - scaleFactor * sunBounds.width() - 20f,
                center.y - scaleFactor * sunBounds.height() / 2f
            )
            updatedSunBounds = RectF(
                sunTranslationOffset.x,
                sunTranslationOffset.y,
                sunTranslationOffset.x + scaleFactor * sunBounds.width(),
                sunTranslationOffset.y + sunBounds.height() * scaleFactor
            )
            it.withTranslation(
                sunTranslationOffset.x, sunTranslationOffset.y

            ) {
                it.withScale(
                    scaleFactor,
                    scaleFactor,
                    pivotX = sunBounds.left,
                    pivotY = sunBounds.top
                ) {
                    it.drawPath(parseSunPath, Paint().apply {
                        color = Color.LTGRAY
                    })

                    // de scale for clicks.
                    val clipx = if (clickedPoint.x == 0f && clickedPoint.y == 0f)
                        sunBounds.centerX() else (clickedPoint.x - sunTranslationOffset.x) / scaleFactor
                    val clipy = if (clickedPoint.x == 0f && clickedPoint.y == 0f)
                        sunBounds.centerY() else (clickedPoint.y - sunTranslationOffset.y) / scaleFactor
                    it.withClip(parseSunPath) {
                        drawCircle(clipx,
                            clipy, sunpercentage + 1f, Paint().apply {
                                shader = RadialGradient(
                                    clipx,
                                    clipy,
                                    sunpercentage + 1f,
                                    if (currentSelection == SELECTION.SUN || sunpercentage > 0f) Color.RED else Color.LTGRAY,
                                    if (currentSelection == SELECTION.SUN || sunpercentage > 0f) Color.RED else Color.LTGRAY,
                                    Shader.TileMode.CLAMP
                                )
                            })
                    }
                }
            }

            val smileTranslationOffset =
                Point(center.x + 20f, center.y - scaleFactor * sunBounds.height() / 2f)
            updatedSmileBounds = RectF(
                smileTranslationOffset.x,
                smileTranslationOffset.y,
                smileTranslationOffset.x + scaleFactor * smileBounds.width(),
                smileTranslationOffset.y + smileBounds.height() * scaleFactor
            )
            it.withTranslation(center.x + 20f, center.y - scaleFactor * sunBounds.height() / 2f) {
                withScale(
                    scaleFactor,
                    scaleFactor,
                    pivotX = smileBounds.left,
                    pivotY = smileBounds.top
                ) {
                    it.drawPath(parseSmilePath, Paint().apply {
                        color = Color.LTGRAY
                    })

                    // de scale for clicks.
                    val clipx = if (clickedPoint.x == 0f && clickedPoint.y == 0f)
                        smileBounds.centerX() else (clickedPoint.x - smileTranslationOffset.x) / scaleFactor
                    val clipy = if (clickedPoint.x == 0f && clickedPoint.y == 0f)
                        smileBounds.centerY() else (clickedPoint.y - smileTranslationOffset.y) / scaleFactor
                    it.withClip(parseSmilePath) {
                        drawCircle(
                            clipx,
                            clipy,
                            smilepercentage + 1f,
                            Paint().apply {
                                shader = RadialGradient(
                                    clipx,
                                    clipy,
                                    smilepercentage + 1f,
                                    if (currentSelection == SELECTION.SMILE || smilepercentage > 0f) Color.RED else Color.LTGRAY,
                                    if (currentSelection == SELECTION.SMILE || smilepercentage > 0f) Color.RED else Color.LTGRAY,
                                    Shader.TileMode.CLAMP
                                )
                            })
                    }

                }

            }
        }
    }

    var updatedSunBounds = RectF()
    var updatedSmileBounds = RectF()
    var clickedPoint: Point = Point(0f, 0f)


    init {
        sunpercentage = 80f
    }

    enum class SELECTION {
        SUN, SMILE
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {

            // now note for clicking u cant use sunBounds becuase they are scaled up after.so you need to transform it and perform click.

            val x = it.x
            val y = it.y
            clickedPoint = Point(x, y)

            println("bounds : " + updatedSunBounds.centerX() + " " + sunBounds.centerX() + " " + x + " " + y)
            if (updatedSunBounds.contains(x, y)) {
                currentSelection = SELECTION.SUN
                Toast.makeText(context, "sun clicked...", Toast.LENGTH_SHORT).show()
            } else if (updatedSmileBounds.contains(x, y)) {
                currentSelection = SELECTION.SMILE
                Toast.makeText(context, "smile clicked...", Toast.LENGTH_SHORT).show()
            }
            animatePic()
        }
        return super.onTouchEvent(event)
    }

    data class Point(val x: Float, val y: Float)

}