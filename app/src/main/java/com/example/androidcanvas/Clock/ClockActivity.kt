package com.example.androidcanvas.Clock

import android.icu.text.NumberFormat
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.androidcanvas.R
import java.util.*
import kotlin.concurrent.timer


class ClockActivity : AppCompatActivity() {
    private lateinit var clockView: ClockView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*for customview use below line and comment others
        setContentView(CustomView(this))*/
        setContentView(R.layout.activity_clock)
        clockView = findViewById(R.id.clockView)
        val format = java.text.NumberFormat.getInstance()
        format.setMinimumIntegerDigits(2)
        timer(initialDelay = 0L, period = 1000L) {
            val time = System.currentTimeMillis()
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = time
            val sec = calendar.get(Calendar.SECOND)
            val min = calendar.get(Calendar.MINUTE)
            val hrs = calendar.get(Calendar.HOUR)
            runOnUiThread {
                findViewById<TextView>(R.id.time).setText(
                    format.format(hrs)
                        .toString() + " : " + format.format(min) + " : " + format.format(sec)
                )
            }
            clockView.updateView(sec.toFloat(), min.toFloat(), hrs.toFloat())
        }
    }
}