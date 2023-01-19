package com.example.androidcanvas

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.androidcanvas.BallGame.SecondActivity
import com.example.androidcanvas.Clock.ClockActivity
import com.example.androidcanvas.Path.PathActivity
import com.example.androidcanvas.Path.VectorGraphicActivity
import com.example.androidcanvas.Shapes.CustomShapeActivity
import com.example.androidcanvas.WeightPicker.WeightPickerActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun launchBallGame(view: View) {
        startActivity(SecondActivity::class.java)
    }


    fun launchWeightPicker(view: View) {
        startActivity(WeightPickerActivity::class.java)
    }

    fun launchShapeActivity(view: View) {
        startActivity(CustomShapeActivity::class.java)
    }


    fun <T : AppCompatActivity> startActivity(activity: Class<T>) {
        startActivity(Intent(this, activity).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK))
    }

    fun launchClockView(view: View) {
        startActivity(ClockActivity::class.java)
    }

    fun launchPath(view: View) {
        startActivity(PathActivity::class.java)
    }

    fun vectorPath(view: View) {
        startActivity(VectorGraphicActivity::class.java)
    }

}