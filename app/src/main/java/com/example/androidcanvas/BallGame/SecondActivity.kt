package com.example.androidcanvas.BallGame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.TextView
import com.example.androidcanvas.R

class SecondActivity : AppCompatActivity() {
    private lateinit var point: TextView
    private lateinit var timer: TextView
    private lateinit var button: Button
    var time = 30
    var points = 0
    private lateinit var ballView: BallView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        point = findViewById(R.id.points)
        timer = findViewById(R.id.timer)
        button = findViewById(R.id.startBtn)
        ballView = findViewById(R.id.ballView)
        ballView.setBallClickListener {
            ctimer?.let {
                points++
                point.setText("Points : $points")
            }
        }
        button.setOnClickListener {
            if (button.text.equals("Reset")) {
                time = 30
                timer.setText(time.toString())
                ballView.enableOrDisableClick(false)
                points = 0
                point.setText("Points : $points")
                button.setText("Start")
                ctimer?.cancel()
                ctimer = null
            } else {
                points = 0
                point.setText("Points : $points")
                button.setText("Reset")
                startTimer()
            }
        }
    }

    private var ctimer: CountDownTimer? = null
    private fun startTimer() {
        ballView.enableOrDisableClick(true)
        timer.setText(time.toString())
        point.setText("Points : $points")
        ctimer = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timer.setText((millisUntilFinished / 1000).toString())
            }

            override fun onFinish() {
                time = 30
                timer.setText(time.toString())
                point.setText("Points : $points")
                button.setText("ReStart")
                ctimer?.cancel()
                ballView.enableOrDisableClick(false)
                ctimer = null
            }
        }
        ctimer?.start()
    }
}