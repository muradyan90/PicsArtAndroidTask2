package com.aram.paandroidtask2

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View


class CustomProgressBar @JvmOverloads constructor(
    ctx: Context,
    attrs: AttributeSet? = null,
    defStyleAtrr: Int = 0
) : View(ctx, attrs, defStyleAtrr) {

    private val paint = Paint().apply {
        isAntiAlias = true
        color = DEF_COLOR
        style = Paint.Style.FILL
        strokeWidth = DEF_STROKE_WIDTH
        strokeCap = Paint.Cap.ROUND
    }

    private val paintProgress = Paint().apply {
        isAntiAlias = true
        color = DEF_PROGRESS_COLOR
        style = Paint.Style.FILL
        strokeWidth = DEF_STROKE_WIDTH
        strokeCap = Paint.Cap.ROUND
    }

    var progress = DEF_PROGRESS
        set(value) {
            field = when {
                value < 0f -> DEF_PROGRESS
                value > 1f -> MAX_PROGRESS
                else -> value
            }
        }
    private var startColor = 0
    private var endColor = 0
    private var touchX = 0f
    private var moveX = 0f
    private var startX = 0f
    private var stopX = 0f
    private var startStopY = 0f

    init {
        context.takeIf {
            attrs != null
        }?.theme?.obtainStyledAttributes(
            attrs,
            R.styleable.CustomProgressBar,
            defStyleAtrr,
            0
        )?.apply {
            try {
                paint.strokeWidth = getFloat(R.styleable.CustomProgressBar_stroke_width, DEF_STROKE_WIDTH)
                paint.color = getColor(R.styleable.CustomProgressBar_background_color, DEF_COLOR)
                paintProgress.color = getColor(R.styleable.CustomProgressBar_progress_color, DEF_PROGRESS_COLOR)
                paintProgress.strokeWidth = getFloat(R.styleable.CustomProgressBar_stroke_width, DEF_STROKE_WIDTH)
                progress = getFloat(R.styleable.CustomProgressBar_progress, DEF_PROGRESS)
                startColor = getColor(R.styleable.CustomProgressBar_start_color, DEF_COLOR)
                endColor = getColor(R.styleable.CustomProgressBar_end_color, DEF_PROGRESS_COLOR)
            } finally {
                recycle()
            }
        }
    }

 var mShader = LinearGradient(0f,
                                 paint.strokeWidth,
                             paint.strokeWidth*10,
                                 paint.strokeWidth,
                                 startColor,
                                 endColor,
                                 Shader.TileMode.MIRROR)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val requestedWidth = MeasureSpec.getSize(widthMeasureSpec)
        val requestedWidthMode = MeasureSpec.getMode(widthMeasureSpec)

        val requestedHeight = MeasureSpec.getSize(heightMeasureSpec)
        val requestedHeightMode = MeasureSpec.getMode(heightMeasureSpec)

        val desiredWidth: Int = 10 * paint.strokeWidth.toInt()
        val desiredHeight: Int = paint.strokeWidth.toInt()

        val width = when (requestedWidthMode) {
            MeasureSpec.EXACTLY -> requestedWidth
            MeasureSpec.UNSPECIFIED -> desiredWidth
            else -> Math.min(requestedWidth, desiredWidth)
        }

        val height = when (requestedHeightMode) {
            MeasureSpec.EXACTLY -> requestedHeight
            MeasureSpec.UNSPECIFIED -> desiredHeight
            else -> Math.min(requestedHeight, desiredHeight)
        }
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        startX = width.toFloat() / 100 * 10
        stopX = width.toFloat() / 100 * 90
        startStopY = height.toFloat() / 2

        if (paint.strokeWidth > startStopY) {
            paint.strokeWidth = startStopY
            paintProgress.strokeWidth = startStopY
        }

        canvas?.drawLine(startX, startStopY, stopX, startStopY, paint)

        paintProgress.shader = mShader
        if (progress != 0f && touchX == 0f) {
            canvas?.drawLine(startX, startStopY, stopX * progress, startStopY, paintProgress)
        }

        if (touchX != 0f) {
            canvas?.drawLine(startX, startStopY, moveX, startStopY, paintProgress)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                touchX = when {
                    event.x < startX -> startX
                    event.x > stopX -> stopX
                    else -> event.x
                }
                moveX = touchX
                invalidate()
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                moveX = when {
                    event.x < startX -> startX
                    event.x > stopX -> stopX
                    else -> event.x
                }
                invalidate()
            }
        }
        return super.onTouchEvent(event)
    }

    companion object {
        private const val DEF_STROKE_WIDTH = 20f
        private const val DEF_COLOR = Color.BLACK
        private const val DEF_PROGRESS_COLOR = Color.YELLOW
        private const val DEF_PROGRESS = 0f
        private const val MAX_PROGRESS = 1f

    }
}