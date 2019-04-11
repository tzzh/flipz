package com.example.flipz

import android.content.Context
import android.graphics.*
import android.view.SurfaceHolder
import android.view.SurfaceView
import SensorRecord


class GameView(context: Context, private val screenWidth : Int, private val screenHeight : Int) : SurfaceView(context), SurfaceHolder.Callback {
    private val thread: GameThread
    private val sensorRecord: SensorRecord

    private val backgroundBoard = BitmapFactory.decodeResource(resources, R.drawable.skateboard)

    private val trickView = TrickView()

    init {
        holder.addCallback(this)
        thread = GameThread(holder, this)
        sensorRecord = SensorRecord(context)

    }

    fun pause(){
        sensorRecord.stopRecording()
    }

    fun resume(){
        sensorRecord.startRecording()
    }


    override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
        sensorRecord.startRecording()

        thread.setRunning(true)
        thread.start()
    }

    override fun surfaceChanged(surfaceHolder: SurfaceHolder, i: Int, i1: Int, i2: Int) {

    }

    override fun surfaceDestroyed(surfaceHolder: SurfaceHolder) {
        var retry = true
        while (retry) {
            try {
                thread.setRunning(false)
                thread.join()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            retry = false
        }
    }


    fun update() {
        val rotation = sensorRecord.detectRotation()
        trickView.update(rotation)
    }


    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        canvas.drawBitmap(backgroundBoard, null, Rect(0,0,screenWidth, screenHeight), null)

        trickView.draw(canvas)

    }
}