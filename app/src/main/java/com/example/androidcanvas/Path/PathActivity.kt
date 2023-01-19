package com.example.androidcanvas.Path

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.androidcanvas.R

class PathActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(PathView(this))
    }
}