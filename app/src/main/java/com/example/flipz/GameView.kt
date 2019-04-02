package com.example.flipz

import android.content.Context
import android.graphics.*
import android.view.SurfaceHolder
import android.view.SurfaceView
import SensorRecord



class GameView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {
    private val thread: GameThread
    private val sensorRecord: SensorRecord

    private var lastTrick = "No Trick"
    private var lastTs = 0L

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
        lastTs = System.nanoTime()
        val (x, y, z) = sensorRecord.detectTrick(lastTs)
        if( x != 0F && y != 0F && z != 0F) {
            lastTrick = "x: $x, y: $y, z: $z"
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
        canvas.drawText("$lastTrick", 40f, 40f, paint)
    }
}