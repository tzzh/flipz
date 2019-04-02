package com.example.flipz

import android.app.Activity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import SensorRecord
import android.widget.TextView

class MainActivity : Activity() {

    private lateinit var sensorRecord: SensorRecord

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(GameView(applicationContext))
        sensorRecord = SensorRecord(applicationContext)
        //sensorRecord.startRecording()

    }

    override fun onPause() {
        super.onPause()
        sensorRecord.stopRecording()
    }
}