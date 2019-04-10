package com.example.flipz
import Rotation
import getTrick
import Trick
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface

class TrickView {
    private val displayTime = 3000 // time to display trick in ms
    private var trickTime : Long = 0L
    private var trick : Trick? = null
    private val paint = Paint()

    init {
        paint.color = Color.RED
        paint.textSize = 200f
        paint.typeface = Typeface.DEFAULT_BOLD
    }

    fun update(r: Rotation){
        val t= getTrick(r)
        if(t != null){
            this.trick = t
            trickTime = System.currentTimeMillis()
        }
        else if(trick != null){
            if(System.currentTimeMillis() - trickTime > displayTime){
                trick = null
            }
        }
    }

    fun draw(canvas: Canvas){
        if(trick != null){
            canvas.drawText(trick?.name, 60F, 1000F, paint)
        }

    }
}