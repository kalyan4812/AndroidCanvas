package com.example.androidcanvas.Shapes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class CustomShapeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(CustomView(this))
    }
}