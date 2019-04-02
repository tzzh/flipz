package com.example.flipz

import android.content.Context
import android.graphics.*
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.WindowManager



class GameView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {
    private val thread: GameThread
    private val maxX: Int
    private val maxY: Int

    init {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val size = Point()
        wm.defaultDisplay.getSize(size)
        maxX = size.x
        maxY = size.y


        holder.addCallback(this)
        thread = GameThread(holder, this)
    }


    override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
        // start the game thread
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

    }


    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        val paint = Paint()
        paint.color = Color.WHITE
        paint.style = Paint.Style.FILL
        canvas.drawPaint(paint)

        paint.color = Color.BLACK
        paint.textSize = 40f
        canvas.drawText("Kickflip", maxX/2f, maxY/2f, paint)
    }
}