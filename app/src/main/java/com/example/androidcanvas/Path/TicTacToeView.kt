package com.example.androidcanvas.Path

import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.os.Build.VERSION_CODES.O
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.annotation.Nullable


class TicTacToeView : View {
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

    var width = 0f
    var height = 0f
    var rect1 = RectF()
    var rect2 = RectF()
    var rect3 = RectF()
    var rect4 = RectF()
    var rect5 = RectF()
    var rect6 = RectF()
    var rect7 = RectF()
    var rect8 = RectF()
    var rect9 = RectF()

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            width = 100f
            height = 300f

            drawLines(it, width, height)

        }
    }

    enum class Type {
        CROSS, CIRCLE, NONE
    }

    private var previousType = Type.CIRCLE

    private fun drawLines(it: Canvas, w: Float, h: Float) {

        val paint = Paint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            color = Color.YELLOW
            style = Paint.Style.STROKE
            strokeWidth = 20f
        }
        it.drawRect(w, h, w + 900f, h + 900f, paint)
        it.drawLine(w + 300f, h, w + 300f, h + 900f, paint)
        it.drawLine(w + 600f, h, w + 600f, h + 900f, paint)
        it.drawLine(w, h + 300f, w + 900f, h + 300f, paint)
        it.drawLine(w, h + 600f, w + 900f, h + 600f, paint)

        rect1 = RectF(w, h, w + 300f, h + 300f)
        rect2 = RectF(w + 300f, h, w + 600f, h + 300f)
        rect3 = RectF(w + 600f, h, w + 900f, h + 300f)

        rect4 = RectF(w, h + 300f, w + 300f, h + 600f)
        rect5 = RectF(w + 300f, h + 300f, w + 600f, h + 600f)
        rect6 = RectF(w + 600f, h + 300f, w + 900f, h + 600f)

        rect7 = RectF(w, h + 600f, w + 300f, h + 900f)
        rect8 = RectF(w + 300f, h + 600f, w + 600f, h + 900f)
        rect9 = RectF(w + 600f, h + 600f, w + 900f, h + 900f)



        tappedBoxes?.forEach { boxProperty ->
            val selectedRectangle = boxProperty.rect
            if (boxProperty.isCircle) {
                it.drawCircle(
                    selectedRectangle.centerX(),
                    selectedRectangle.centerY(),
                    if (map[boxProperty.index] == Type.NONE) phase else 100f,
                    Paint().apply {
                        flags = Paint.ANTI_ALIAS_FLAG
                        color = Color.RED
                        style = Paint.Style.STROKE
                        strokeWidth = 20f
                    })
                if (phase == 100f && map[boxProperty.index] == Type.NONE)
                    map[boxProperty.index] = Type.CIRCLE
            } else {
                it.drawLine(
                    selectedRectangle.left + 60f,
                    selectedRectangle.top + 60f,
                    selectedRectangle.right - if (map[boxProperty.index] == Type.NONE) 300 - phase else 60f,
                    selectedRectangle.bottom - if (map[boxProperty.index] == Type.NONE) 300 - phase else 60f,
                    Paint().apply {
                        flags = Paint.ANTI_ALIAS_FLAG
                        color = Color.GREEN
                        style = Paint.Style.STROKE
                        strokeWidth = 20f
                    })
                it.drawLine(
                    selectedRectangle.right - 60f,
                    selectedRectangle.top + 60f,
                    selectedRectangle.left + if (map[boxProperty.index] == Type.NONE) 300 - phase else 60f,
                    selectedRectangle.bottom - if (map[boxProperty.index] == Type.NONE) 300 - phase else 60f,
                    Paint().apply {
                        flags = Paint.ANTI_ALIAS_FLAG
                        color = Color.GREEN
                        style = Paint.Style.STROKE
                        strokeWidth = 20f
                    })
                if (phase == 240f && map[boxProperty.index] == Type.NONE)
                    map[boxProperty.index] = Type.CROSS
            }
        }


        if (checkForBingo(Type.CIRCLE)) {
            android.os.Handler().postDelayed({
                status?.onStatusChange("CIRCLE WON")
            }, 50L)
            gameOver = true
            return
        }
        if (checkForBingo(Type.CROSS)) {
            android.os.Handler().postDelayed({
                status?.onStatusChange("CROSS WON")
            }, 50L)
            gameOver = true
            return
        }
        if (tappedBoxes?.size == 9) {
            gameOver = true
            android.os.Handler().postDelayed({
                status?.onStatusChange("Draw")
            }, 50L)
        }
    }

    private var gameOver = false

    fun interface Status {
        fun onStatusChange(status: String)
    }

    private var status: Status? = null
    fun setStatusListener(status: Status) {
        this.status = status
    }

    var phasePortion = PropertyValuesHolder.ofFloat(
        "percentage",
        if (previousType == Type.CROSS) 0f else 60f,
        if (previousType == Type.CROSS) 100f else 240f
    )
    var phase = 0f
    var enableAutoGame = true

    private val map = hashMapOf<Int, Type>()

    fun animateDrawing(type: Type) {
        phasePortion = PropertyValuesHolder.ofFloat(
            "percentage",
            if (type == Type.CIRCLE) 0f else 60f,
            if (type == Type.CIRCLE) 100f else 240f
        )
        ValueAnimator().apply {
            setValues(phasePortion)
            duration = 500
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener {
                phase = it.getAnimatedValue("percentage") as Float
                invalidate()
            }
        }.start()
    }

    private val arr = Array(3) { IntArray(3) }

    private fun checkForBingo(type: Type): Boolean {
        var isGameOver = false
        if (map[1] == type && map[2] == type && map[3] == type) {
            isGameOver = true
        } else if (map[4] == type && map[5] == type && map[6] == type) {
            isGameOver = true
        } else if (map[7] == type && map[8] == type && map[9] == type) {
            isGameOver = true
        } else if (map[1] == type && map[4] == type && map[7] == type) {
            isGameOver = true
        } else if (map[2] == type && map[5] == type && map[8] == type) {
            isGameOver = true
        } else if (map[3] == type && map[6] == type && map[9] == type) {
            isGameOver = true
        } else if (map[1] == type && map[5] == type && map[9] == type) {
            isGameOver = true
        } else if (map[3] == type && map[5] == type && map[7] == type) {
            isGameOver = true
        }
        return isGameOver
    }

    fun intializeBoxes() {
        for (i in 1..9) {
            map.put(i, Type.NONE)
        }
        for (i in 0..2) {
            for (j in 0..2) {
                arr[i][j] = -1
            }
        }
    }

    init {
        intializeBoxes()
    }

    private var selectedRectangle: RectF? = null

    private var tappedBoxes: HashSet<BoxProperty>? = HashSet()

    data class BoxProperty(val rect: RectF, val isCircle: Boolean, val index: Int)

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {

            val x = it.x
            val y = it.y

            if (gameOver) return false

            if (rect1.contains(x, y)) {
                drawImage(rect1, 1)
                arr[0][0] = 1
            } else if (rect2.contains(x, y)) {
                drawImage(rect2, 2)
                arr[0][1] = 1
            } else if (rect3.contains(x, y)) {
                drawImage(rect3, 3)
                arr[0][2] = 1
            } else if (rect4.contains(x, y)) {
                drawImage(rect4, 4)
                arr[1][0] = 1
            } else if (rect5.contains(x, y)) {
                drawImage(rect5, 5)
                arr[1][1] = 1
            } else if (rect6.contains(x, y)) {
                drawImage(rect6, 6)
                arr[1][2] = 1
            } else if (rect7.contains(x, y)) {
                drawImage(rect7, 7)
                arr[2][0] = 1
            } else if (rect8.contains(x, y)) {
                drawImage(rect8, 8)
                arr[2][1] = 1
            } else if (rect9.contains(x, y)) {
                drawImage(rect9, 9)
                arr[2][2] = 1
            }


        }
        return super.onTouchEvent(event)
    }

    private var drawCircle = false
    private fun drawImage(rect: RectF, index: Int = 0) {

        tappedBoxes?.forEach {
            if (it.rect == rect) return
        }

        selectedRectangle = rect
        if (previousType == Type.CIRCLE) {
            drawCircle = false
            previousType = Type.CROSS
            tappedBoxes?.add(BoxProperty(rect, false, index))
            animateDrawing(Type.CROSS)
            if (enableAutoGame) {
                android.os.Handler().postDelayed({ autoDrawCircle() }, 1000L)
            }
        } else {
            drawCircle = true
            previousType = Type.CIRCLE
            tappedBoxes?.add(BoxProperty(rect, true, index))
            animateDrawing(Type.CIRCLE)
        }

    }

    private fun autoDrawCircle() {
        val box = findBestMove(arr)!!
        val mx = box[0]
        val my = box[1]
        if (mx == -1 || my == -1) return
        var rect = RectF()
        var boxNumber = -1
        when {
            mx == 0 && my == 0 -> {
                rect = rect1
                arr[0][0] = 2
                boxNumber = 1
            }
            mx == 0 && my == 1 -> {
                rect = rect2
                arr[0][1] = 2
                boxNumber = 2
            }
            mx == 0 && my == 2 -> {
                rect = rect3
                arr[0][2] = 2
                boxNumber = 3
            }
            mx == 1 && my == 0 -> {
                rect = rect4
                arr[1][0] = 2
                boxNumber = 4
            }
            mx == 1 && my == 1 -> {
                rect = rect5
                arr[1][1] = 2
                boxNumber = 5
            }
            mx == 1 && my == 2 -> {
                rect = rect6
                arr[1][2] = 2
                boxNumber = 6
            }
            mx == 2 && my == 0 -> {
                rect = rect7
                arr[2][0] = 2
                boxNumber = 7
            }
            mx == 2 && my == 1 -> {
                rect = rect8
                arr[2][1] = 2
                boxNumber = 8
            }
            mx == 2 && my == 2 -> {
                rect = rect9
                arr[2][2] = 2
                boxNumber = 9
            }
        }
        drawCircle = true
        previousType = Type.CIRCLE
        tappedBoxes?.add(BoxProperty(rect, true, boxNumber))
        animateDrawing(Type.CIRCLE)
    }


    fun resetGame() {
        tappedBoxes?.clear()
        gameOver = false
        intializeBoxes()
        previousType = Type.CIRCLE
        invalidate()
    }


    fun findBestMove(board: Array<IntArray>): IntArray? {
        var bestScore = Int.MIN_VALUE
        val bestMove = intArrayOf(-1, -1)
        for (i in 0..2) {
            for (j in 0..2) {
                if (board[i][j] == -1) {
                    board[i][j] = 2
                    val score = minMax(board, Type.CROSS)
                    board[i][j] = -1
                    if (score > bestScore) {
                        bestScore = score
                        bestMove[0] = i
                        bestMove[1] = j
                    }
                }
            }
        }
        return bestMove
    }

    // The MinMax algorithm implementation.
    private fun minMax(board: Array<IntArray>, player: Type): Int {
        if (checkWin(Type.CIRCLE)) return 10
        if (checkWin(Type.CROSS)) return -10
        if (availableCellsSize() == 0) return 0
        var bestScore = if (player == Type.CIRCLE) Int.MIN_VALUE else Int.MAX_VALUE
        for (i in 0..2) {
            for (j in 0..2) {
                if (board[i][j] == -1) {
                    if (player == Type.CIRCLE) {
                        board[i][j] = 2
                        val score = minMax(board, Type.CROSS)
                        bestScore =
                            Math.max(score, bestScore)
                    } else if (player == Type.CROSS) {
                        board[i][j] = 1
                        val score = minMax(board, Type.CIRCLE)
                        bestScore =
                            Math.min(score, bestScore)
                    }
                    board[i][j] = -1
                }
            }
        }
        return bestScore
    }


    private fun availableCellsSize(): Int {
        var count = 0
        arr.forEach {
            it.forEach {
                if (it == -1) count++
            }
        }
        return count
    }

    private fun checkWin(type: Type): Boolean {
        val x = if (type == Type.CIRCLE) 2 else 1
        if (arr[0][0] == x && arr[0][1] == x && arr[0][2] == x) {
            return true
        } else if (arr[1][0] == x && arr[1][1] == x && arr[1][2] == x) {
            return true
        } else if (arr[2][0] == x && arr[2][1] == x && arr[2][2] == x) {
            return true
        } else if (arr[0][0] == x && arr[1][0] == x && arr[2][0] == x) {
            return true
        } else if (arr[0][1] == x && arr[1][1] == x && arr[2][1] == x) {
            return true
        } else if (arr[0][2] == x && arr[1][2] == x && arr[2][2] == x) {
            return true
        } else if (arr[0][0] == x && arr[1][1] == x && arr[2][2] == x) {
            return true
        } else if (arr[0][2] == x && arr[1][1] == x && arr[2][0] == x) {
            return true
        }
        return false
    }
}