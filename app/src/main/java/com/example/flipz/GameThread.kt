package com.example.flipz

import android.graphics.Canvas
import android.view.SurfaceHolder


class GameThread(private val surfaceHolder: SurfaceHolder, private val gameView: GameView) : Thread() {

    private var running: Boolean = false
    private val updatePeriod = 500L // how often to check for new tricks in ms

    fun setRunning(isRunning: Boolean) {
        this.running = isRunning
    }

    override fun run() {
        while (running) {
            canvas = null
            try {
                // locking the canvas allows us to draw on to it
                canvas = this.surfaceHolder.lockCanvas()
                synchronized(surfaceHolder) {
                    this.gameView.update()
                    this.gameView.draw(canvas!!)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (canvas != null) {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            try {
                sleep(updatePeriod)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        private var canvas: Canvas? = null
    }

}