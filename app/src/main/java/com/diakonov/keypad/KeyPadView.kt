package com.diakonov.keypad

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import kotlinx.coroutines.*


class KeyPadView(context: Context?, attrs: AttributeSet?) :
    View(context, attrs) {
    private val black: Paint
    private val purple: Paint
    private val white: Paint
    private val whites: ArrayList<Key> = ArrayList()
    private val blacks: ArrayList<Key> = ArrayList()
    private var keyWidth = 0
    private var keyHeight = 0
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        keyWidth = w / NB
        keyHeight = h
        var count = 0
        for (i in 0 until NB) {
            val left = i * keyWidth
            var right = left + keyWidth
            if (i == NB - 1) {
                right = w
            }
            var rect = RectF(left.toFloat(), 0F, right.toFloat(), h.toFloat())
            whites.add(Key(rect, count))
            if (i != 0 && i != 3 && i != 7) {
                rect = RectF(
                    (i - 1).toFloat() * keyWidth + 0.5f * keyWidth + 0.25f * keyWidth, 0F,
                    i.toFloat() * keyWidth + 0.25f * keyWidth, 0.67f * height
                )
                blacks.add(Key(rect, count))
                count++
            }
        }
    }


    override fun onDraw(canvas: Canvas) {
        val corners = floatArrayOf(
            0f, 0f,
            0f, 0f,
            17f, 17f,
            17f, 17f
        )
        for (k in whites) {
            canvas.drawRoundRect(k.rect, 40F, 40F, if (k.down) purple else white)
        }
        for (i in 0 until NB) {
            canvas.drawLine((i * keyWidth).toFloat(), 0F, (i * keyWidth).toFloat(),
                height.toFloat(), black )
        }
        for (k in blacks) {
            val path = Path()
            path.addRoundRect(k.rect, corners, Path.Direction.CW)
            canvas.drawPath(path,  if (k.down) purple else black)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val action = event.action
        val isDownAction = action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE
        for (touchIndex in 0 until event.pointerCount) {
            val x = event.getX(touchIndex)
            val y = event.getY(touchIndex)
            val k = keyForCoords(x, y)
            if (k != null) {
                k.down = isDownAction
            }
        }
        val tmp = ArrayList(whites)
        tmp.addAll(blacks)


        for (k in tmp) {
            if (k.down) {
                invalidate()

            } else {
                releaseKey(k)
                invalidate()
            }
        }

        return true
    }
    private fun releaseKey(k: Key) {
        val loop = true
        val mCoroutineScope = CoroutineScope(Dispatchers.IO)
        mCoroutineScope.launch(Dispatchers.IO) {
            while(loop) {
                delay(500L)
                withContext(Dispatchers.Main) {
                    k.down = false
                }
            }
        }
        k.down = false
    }


    private fun keyForCoords(x: Float, y: Float): Key? {
        for (k in blacks) {
            if (k.rect.contains(x, y)) {
                return k
            }
        }
        for (k in whites) {
            if (k.rect.contains(x, y)) {
                return k
            }
        }
        return null
    }

    companion object {
        const val NB = 7
    }

    init {
        black = Paint()
        black.color = Color.BLACK
        black.strokeWidth = 10F
        white = Paint()
        white.color = Color.WHITE
        white.style = Paint.Style.FILL
        purple = Paint()
        purple.color = ContextCompat.getColor(context?.applicationContext!!, R.color.purple)
        purple.style = Paint.Style.FILL
    }
}
