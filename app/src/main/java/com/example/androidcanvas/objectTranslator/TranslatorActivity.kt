package com.example.androidcanvas.objectTranslator

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnLayout
import com.example.androidcanvas.databinding.ActivityTranslatorBinding
import java.util.LinkedList
import java.util.Queue
import kotlin.math.hypot


class TranslatorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTranslatorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTranslatorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.doOnLayout {
            //  startAnimation(speed = 1, view = binding.translatorView)
            collisionAnimateView(binding.translatorView, binding.anotherTranslatorView)
        }
    }

    // --------------------------------- anim around border of screen-----------------------------------
    private fun startAnimation(repeatCount: Int = 1, speed: Int, view: View) {
        val rect = Rect()
        window.decorView.getWindowVisibleDisplayFrame(rect)
        val screenEndX = resources.displayMetrics.widthPixels
        val screenEndY = rect.height()
        val animQueue = getAnimations(
            mutableListOf(
                DIRECTIONS.RIGHT_BOTTOM, DIRECTIONS.RIGHT_TOP, DIRECTIONS.LEFT_BOTTOM,
                DIRECTIONS.LEFT_TOP
            ),
            screenEndX.toFloat(),
            screenEndY.toFloat(),
            view.width.toFloat(),
            view.height.toFloat()
        )
        val duration = (screenEndX * 2 + screenEndY * 2) / speed
        playAnimations(animQueue, duration.toLong(), repeatCount, LinkedList(animQueue), view)
    }

    private fun playAnimations(
        queue: Queue<Pair<Float, Float>>,
        eachDuration: Long,
        repeatCount: Int = 1, originalQueue: Queue<Pair<Float, Float>>, view: View
    ) {
        if (queue.isEmpty() || repeatCount == 0) {
            if (repeatCount > 0) {
                playAnimations(
                    LinkedList(originalQueue),
                    eachDuration,
                    repeatCount - 1,
                    originalQueue, view
                )
            } else if (repeatCount < 0) {
                playAnimations(
                    LinkedList(originalQueue),
                    eachDuration,
                    repeatCount,
                    originalQueue, view
                )
            }
            return
        }
        val entry = queue.poll() ?: return
        view.animate().x(entry.first).y(entry.second).apply {
            duration = eachDuration
            interpolator = LinearInterpolator()
        }.withEndAction {
            playAnimations(queue, eachDuration, repeatCount, originalQueue, view)
        }.start()
    }

    private fun getAnimations(
        directions: MutableList<DIRECTIONS>,
        screenEndX: Float,
        screenEndY: Float, viewWidth: Float, viewHeight: Float
    ): Queue<Pair<Float, Float>> {
        val queue: Queue<Pair<Float, Float>> = LinkedList()
        directions.forEach {
            when (it) {
                DIRECTIONS.RIGHT_TOP -> {
                    queue.add(Pair(screenEndX - viewWidth, 0f))
                }

                DIRECTIONS.LEFT_BOTTOM -> {
                    queue.add(Pair(0f, screenEndY - viewHeight))
                }

                DIRECTIONS.RIGHT_BOTTOM -> {
                    queue.add(
                        Pair(
                            screenEndX - viewWidth,
                            screenEndY - viewHeight
                        )
                    )
                }

                else -> {
                    queue.add(Pair(0f, 0f))
                }
            }
        }
        return queue
    }


    enum class DIRECTIONS {
        LEFT_TOP, RIGHT_TOP, LEFT_BOTTOM, RIGHT_BOTTOM
    }

    // -------------------------- collision with screen boundaries
    private var velocityX: Float = 5f
    private var velocityY: Float = 50f
    private fun animateView(view: View) {
        val screenWidth = resources.displayMetrics.widthPixels.toFloat()
        val screenHeight = resources.displayMetrics.heightPixels.toFloat()
        val entry = moveView(binding.translatorView, velocityX, velocityY)
        if (entry.first < 0 || entry.first + view.width > screenWidth) {
            velocityX = -velocityX
        }
        if (entry.second < 0 || entry.second + view.height > screenHeight) {
            velocityY = -velocityY
        }
        view.animate().apply {
            x(entry.first)
            y(entry.second)
            duration = calculateDuration(
                view.x,
                view.y,
                entry.first,
                entry.second,
                velocityX,
                velocityY
            )
            interpolator = LinearInterpolator()
            withEndAction { animateView(view) }
        }.start()
    }

    //------------------------------  collision with screen boundaries and in between of views----------------
    private var vx1: Float = 5f
    private var vy1: Float = 7f
    private var vx2: Float = 7f
    private var vy2: Float = 15f
    private fun collisionAnimateView(view1: View, view2: View) {
        val screenWidth = resources.displayMetrics.widthPixels.toFloat()
        val screenHeight = resources.displayMetrics.heightPixels.toFloat()
        val entry1 = moveView(binding.translatorView, vx1, vy1)
        val entry2 = moveView(binding.anotherTranslatorView, vx2, vy2)

        if (entry1.first < 0 || entry1.first + view1.width > screenWidth) {
            vx1 = -vx1
        }
        if (entry1.second < 0 || entry1.second + view1.height > screenHeight) {
            vy1 = -vy1
        }
        if (entry2.first < 0 || entry2.first + view2.width > screenWidth) {
            vx2 = -vx2
        }
        if (entry2.second < 0 || entry2.second + view2.height > screenHeight) {
            vy2 = -vy2
        }
        if (checkCollision(
                entry1.first,
                entry1.second,
                view1.width,
                view1.height,
                entry2.first,
                entry2.second,
                view2.width,
                view2.height
            )
        ) {
            vx1 = -vx1
            vy1 = -vy1
            vx2 = -vx2
            vy2 = -vy2
        }
        view1.animate().apply {
            x(entry1.first)
            y(entry1.second)
            duration = calculateDuration(
                view1.x,
                view1.y,
                entry1.first,
                entry1.second,
                vx1,
                vy1
            )

            interpolator = LinearInterpolator()
        }.start()

        view2.animate().apply {
            x(entry2.first)
            y(entry2.second)
            duration = calculateDuration(
                view2.x,
                view2.y,
                entry2.first,
                entry2.second,
                vx2,
                vy2
            )
            interpolator = LinearInterpolator()
            withEndAction { collisionAnimateView(view1, view2) }
        }.start()
    }

    private fun calculateDuration(
        startX: Float,
        startY: Float,
        endX: Float,
        endY: Float,
        velocityX: Float,
        velocityY: Float
    ): Long {
        val distance = hypot((endX - startX).toDouble(), (endY - startY).toDouble()).toFloat()
        return (distance / hypot(velocityX.toDouble(), velocityY.toDouble())).toLong()
    }

    private fun moveView(view: View, velocityX: Float, velocityY: Float): Pair<Float, Float> {
        val newX = view.x + velocityX
        val newY = view.y + velocityY
        return Pair(newX, newY)
    }

    private fun checkCollision(
        x1: Float, y1: Float, width1: Int, height1: Int,
        x2: Float, y2: Float, width2: Int, height2: Int
    ): Boolean {
        return x1 < x2 + width2 &&
                x1 + width1 > x2 &&
                y1 < y2 + height2 &&
                y1 + height1 > y2
    }
}