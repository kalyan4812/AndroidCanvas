package com.example.androidcanvas.Path

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import com.example.androidcanvas.R

class BingoActivity : AppCompatActivity() {

    private lateinit var bingoView: TicTacToeView
    private lateinit var reset: Button
    private lateinit var status: TextView
    private lateinit var autoGameSwitch: Switch
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bingo)
        bingoView = findViewById(R.id.bingoView)
        reset = findViewById(R.id.btn)
        status = findViewById(R.id.statustext)
        autoGameSwitch = findViewById(R.id.autogame)
        autoGameSwitch.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                bingoView.enableAutoGame(true)
                autoGameSwitch.isChecked = true
            } else {
                bingoView.enableAutoGame(false)
                autoGameSwitch.isChecked = false
            }
            status.visibility = View.GONE
        }
        reset.setOnClickListener {
            autoGameSwitch.isChecked = false
            bingoView.resetGame()
            status.visibility = View.GONE
        }
        bingoView.setStatusListener({
            status.visibility = View.VISIBLE
            status.setText(it)
        })
    }
}