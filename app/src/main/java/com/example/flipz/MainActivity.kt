package com.example.flipz

import android.app.Activity
import android.graphics.Point
import android.os.Bundle
import android.view.Window
import android.view.WindowManager

class MainActivity : Activity() {

    private lateinit var gameView : GameView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        val screenSize = Point()
        windowManager?.defaultDisplay?.getSize(screenSize)
        gameView = GameView(applicationContext, screenSize.x, screenSize.y)
        setContentView(gameView)
    }

    override fun onPause() {
        super.onPause()
        gameView.pause()
    }
}