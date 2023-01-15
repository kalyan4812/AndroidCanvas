package com.example.androidcanvas.WeightPicker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.androidcanvas.R

class WeightPickerActivity : AppCompatActivity() {
    private lateinit var weightPickerView: WeightPickerView
    private lateinit var weightText: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*for customview use below line and comment others
        setContentView(CustomView(this))*/
        setContentView(R.layout.activity_weight_picker)
        weightText = findViewById(R.id.weight)
        weightPickerView = findViewById(R.id.wieghtPicker)
        weightText.setText("60 KG")
        weightPickerView.setOnWeightChangeListener({
            weightText.setText(it.toString() + " KG")
        })
    }
}