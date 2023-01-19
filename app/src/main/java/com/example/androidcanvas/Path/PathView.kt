package com.example.androidcanvas.Path

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Animatable
import android.graphics.drawable.Animatable2
import android.graphics.drawable.shapes.RectShape
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import androidx.annotation.Nullable
import androidx.core.graphics.withClip
import androidx.core.graphics.withRotation
import androidx.core.graphics.withScale
import androidx.core.graphics.withTranslation
import com.google.android.material.shape.ShapePath.PathOperation
import kotlin.math.atan2

class PathView : View {
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


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            drawSquareUsingPath(it)
            drawCurveOnOneSideOfSquareUsingQudraticBieze(it)
            drawCurveOnOneSideOfSquareUsingCubicBieze(it)
            drawCircleAndRectUsingPath(it)
            drawShapeUsingPathOperation(it)
            animatePath(it)
            transformPath(it)
            clippigPath(it)
            drawTextOnPath(it)
            pathEffect(it)
            
        }
    }

    private fun pathEffect(it: Canvas) {
            it.drawPath(Path().apply {
                 moveTo(100f,400f)
                 cubicTo(200f,200f,400f,400f,1400f,600f)
            }, Paint().apply {
                flags = Paint.ANTI_ALIAS_FLAG
                color = Color.RED
                style=Paint.Style.STROKE
                strokeWidth=20f
                pathEffect=DashPathEffect(floatArrayOf(50f,30f),phase)
            })


        it.drawPath(Path().apply {
            moveTo(100f,500f)
            lineTo(400f,900f)
            lineTo(900f,500f)

        }, Paint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            color = Color.RED
            style=Paint.Style.STROKE
            strokeWidth=20f
            pathEffect=CornerPathEffect(1000f)
        })

        it.drawPath(Path().apply {
            moveTo(100f,700f)
            lineTo(400f,900f)
            lineTo(900f,700f)

        }, Paint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            color = Color.RED
            style=Paint.Style.STROKE
            strokeWidth=20f
            pathEffect=PathDashPathEffect(Path().apply {
                moveTo(0f,0f)
                lineTo(0f,100f)
                lineTo(100f,100f)
                lineTo(100f,0f)
                close()
            },200f,phase,PathDashPathEffect.Style.MORPH)
        })


    }

    private fun drawTextOnPath(it: Canvas) {
        it.drawTextOnPath("Hell0",Path().apply {
            moveTo(100f,1000f)
            quadTo(100f,1700f,800f,1600f)
        },0f,0f, Paint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            color = Color.RED
            textSize=100f
            textAlign=Paint.Align.CENTER
        })
    }

    private fun clippigPath(it: Canvas) {
        val circlepath = Path().apply {
            addOval(RectF(800f, 1200f, 1050f, 1450f), Path.Direction.CW)
        }
        it.drawPath(circlepath, Paint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            style = Paint.Style.STROKE
            color = Color.RED
            strokeWidth = 20f
        })
        // only rectangle inside circle will be rendered now.
        it.withClip(circlepath) {
            drawRect(RectF(925f, 1325f, 500f, 1700f), Paint().apply {
                flags = Paint.ANTI_ALIAS_FLAG
                style = Paint.Style.FILL
                color = Color.BLUE
                strokeWidth = 20f
            })
        }
    }

    private fun transformPath(it: Canvas) {
        // rotates square by 45 w.r.t point 100f,1800f
        it.withRotation(45f, 300f, 1500f) {
            drawRect(RectF(300f, 1500f, 500f, 1700f), Paint().apply {
                flags = Paint.ANTI_ALIAS_FLAG
                style = Paint.Style.STROKE
                color = Color.GREEN
                strokeWidth = 20f
            })
        }
        // rotates square by 100f right and 100f down and u can use another transformation function inside it.
        it.withTranslation(200f, 100f) {
            withRotation(45f, 400f, 1600f) {
                drawRect(RectF(300f, 1500f, 500f, 1700f), Paint().apply {
                    flags = Paint.ANTI_ALIAS_FLAG
                    style = Paint.Style.STROKE
                    color = Color.GREEN
                    strokeWidth = 20f
                })
            }
        }

        // half the size of square,using center as pivot.
        it.withScale(0.5f, 0.5f, 400f, 1600f) {
            drawRect(RectF(300f, 1500f, 500f, 1700f), Paint().apply {
                flags = Paint.ANTI_ALIAS_FLAG
                style = Paint.Style.STROKE
                color = Color.RED
                strokeWidth = 20f
            })
        }
    }

    var pathPortion = PropertyValuesHolder.ofFloat("percentage", 0f, 100f)
    var percentage = 0f

    var phasePortion = PropertyValuesHolder.ofFloat("percentage", 0f, 100000f)
    var phase=0f
    init {
        ValueAnimator().apply {
            setValues(pathPortion)
            duration = 15000
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener {
                percentage = it.getAnimatedValue("percentage") as Float
                invalidate()
            }
        }.start()

        ValueAnimator().apply {
            setValues(phasePortion)
            repeatCount=-1
            duration=20000L
            interpolator = LinearInterpolator()
            addUpdateListener {
                phase = it.getAnimatedValue("percentage") as Float
                invalidate()
            }
        }.start()

    }

    private fun animatePath(it: Canvas) {
        val path = Path().apply {
            moveTo(100f, 1300f)
            quadTo(100f, 1700f, 800f, 1600f)
        }
        val outComePath = Path()
        val pos = FloatArray(2) // to store position of each point of animation.
        val tan = FloatArray(2) // to store slope of each point of animation.
        PathMeasure().apply {
            setPath(path, false)
            getSegment(0f, percentage * length, outComePath, true)
            getPosTan(
                percentage * length,
                pos,
                tan
            ) // gives x,y coordinate and slope at each point of path and fills in pos,tan array.
        }
        // now we can use ,pos and tan array to draw triangle at end point of curve
        // to look it like a arrow.
        it.drawPath(outComePath, Paint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            style = Paint.Style.STROKE
            color = Color.GREEN
            strokeWidth = 20f
        })
        val angle = -atan2(tan[0], tan[1]) * (180f / Math.PI.toFloat()) - 180f
        val x = pos[0]
        val y = pos[1]
        it.withRotation(degrees = angle, pivotX = x, pivotY = y) {
            // draw triangle
            val trianglepath = Path().apply {
                moveTo(x, y - 30f)
                lineTo(x - 30f, y + 30f)
                lineTo(x + 30f, y + 30f)
                close()
            }
            it.drawPath(trianglepath, Paint().apply {
                flags = Paint.ANTI_ALIAS_FLAG
                style = Paint.Style.FILL
                strokeCap = Paint.Cap.ROUND
                color = Color.RED
            })
        }

    }

    private fun drawShapeUsingPathOperation(it: Canvas) {
        val path1 = Path().apply {
            addRect(RectF(500f, 1100f, 700f, 1300f), Path.Direction.CW)
        }
        val path2 = Path().apply {
            addOval(RectF(650f, 1200f, 850f, 1400f), Path.Direction.CW)
        }
        val pathWithOperation = Path().apply {
            op(path1, path2, Path.Op.UNION)
        }
        // Path.op.diff --> gives path1-path2 shape.(removing 2nd shape from 1st shape).
        // Path.op.reverse_diff --> gives path2-path1 shape.(removing 1st shape from 2nd shape).
        // intersection --> gives path1,path2 intersection.
        // xor --> gives path1+path2- intersection.
        // union --> gives path1+path2(with invisible intersection point)
        it.drawPath(pathWithOperation, Paint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            style = Paint.Style.FILL
            color = Color.RED
        })
    }

    private fun drawCircleAndRectUsingPath(it: Canvas) {
        val path = Path().apply {
            addRect(RectF(100f, 1100f, 300f, 1300f), Path.Direction.CW)
            addOval(RectF(150f, 1200f, 350f, 1400f), Path.Direction.CW)
            // you can any shape or path here.
        }
        it.drawPath(path, Paint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            style = Paint.Style.STROKE
            color = Color.GREEN
            strokeWidth = 20f
        })
    }

    private fun drawCurveOnOneSideOfSquareUsingCubicBieze(it: Canvas) {
        val path = Path().apply {
            moveTo(100f, 600f)
            lineTo(100f, 1000f)
            lineTo(500f, 1000f)
            cubicTo(
                800f,
                1000f,
                800f,
                600f,
                500f,
                600f
            ) // here for x1,y1,x2,y2 are control points of cubic bezier.
            // and we provided y1,y2 as end points of square side where we want to draw the curve,since it uses two control points
            // curve is smooth.

            //   cubicTo(800f,600f,800f,1000f,500f,600f) --- to get inside folding.
            close()
        }
        it.drawPath(path, Paint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            style = Paint.Style.STROKE
            color = Color.GREEN
            strokeWidth = 20f
        })
    }

    private fun drawCurveOnOneSideOfSquareUsingQudraticBieze(it: Canvas) {
        val path = Path().apply {
            moveTo(500f, 50f)
            lineTo(500f, 450f)
            lineTo(900f, 450f)
            quadTo(
                1200f,
                250f,
                900f,
                50f
            ) // x1,y1 are control point( for quad bezier there is only one control point)--curve is not smoother.
            // x2,y2 are end points from 900f,450f.
            close()
        }
        it.drawPath(path, Paint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            style = Paint.Style.STROKE
            color = Color.GREEN
            strokeWidth = 20f
        })
    }

    private fun drawSquareUsingPath(it: Canvas) {
        val path = Path().apply {
            moveTo(100f, 50f)
            lineTo(100f, 350f)
            lineTo(400f, 350f)
            lineTo(400f, 50f)
            lineTo(100f, 50f) // or using close()
        }
        it.drawPath(path, Paint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            style = Paint.Style.STROKE
            color = Color.GREEN
            strokeWidth = 20f
        })
    }
}