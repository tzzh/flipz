package com.example.flipz

import android.content.Context
import android.graphics.*
import android.view.SurfaceHolder
import android.view.SurfaceView
import SensorRecord
import getTrickDistances
import Rotation


class GameView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {
    private val thread: GameThread
    private val sensorRecord: SensorRecord

    private var lastRotation = Rotation(0.0, 0.0, 0.0)

    init {

        holder.addCallback(this)
        thread = GameThread(holder, this)

        sensorRecord = SensorRecord(context)
    }

    fun pause(){
        sensorRecord.stopRecording()
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
        val (x,y,z) = sensorRecord.detectTrick()
        if( x != 0.0 && y != 0.0 && z != 0.0) {
            lastRotation = Rotation(x,y,z)
        }
    }


    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        val paint = Paint()
        paint.color = Color.WHITE
        paint.style = Paint.Style.FILL
        canvas.drawPaint(paint)

        paint.color = Color.BLACK
        paint.textSize = 40f
        canvas.drawText("${lastRotation.x}", 40f, 40f, paint)
        canvas.drawText("${lastRotation.y}", 40f, 80f, paint)
        canvas.drawText("${lastRotation.z}", 40f, 120f, paint)

        var y = 160F
        for ((name, distance) in getTrickDistances(lastRotation)) {
            canvas.drawText("$name: $distance", 40f, y, paint)
            y += 40
        }

    }
}