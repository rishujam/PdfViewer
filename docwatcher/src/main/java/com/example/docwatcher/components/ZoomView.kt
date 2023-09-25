package com.example.docwatcher.components

import android.content.Context
import android.graphics.Matrix
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt


/*
 * Created by Sudhanshu Kumar on 08/09/23.
 */

internal class ZoomView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    companion object {
        private const val MAX_SCALE = 3.0f
        private const val MIN_SCALE = 1.0f
        private const val ZOOM_DURATION = 200
        private const val BASE_ROTATION = 0.0f
    }

    private var mScaleFactor = 1f
    private var mScaleDetector: ScaleGestureDetector? = null

    private val mBaseMatrix = Matrix()
    private val mDrawMatrix = Matrix()
    private val mSuppMatrix = Matrix()
    private val mMatrixValues = FloatArray(9)
    private val mDisplayRect = RectF()
    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var isDragging = false

    private val mScaleGestureListener = object : ScaleGestureDetector.OnScaleGestureListener {

        private var lastFocusX = 0f
        private var lastFocusY = 0f

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            mScaleFactor = detector.scaleFactor
            if (java.lang.Float.isNaN(mScaleFactor) || java.lang.Float.isInfinite(mScaleFactor)) return false
            if (mScaleFactor >= 0) {
                val focusX = detector.focusX
                val focusY = detector.focusY
                val dx = detector.focusX - lastFocusX
                val dy = detector.focusY - lastFocusY
                temp(mScaleFactor, focusX, focusY, dx, dy)
                lastFocusX = detector.focusX
                lastFocusY = detector.focusY
            }
            return true
        }

        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            this@ZoomView.parent?.requestDisallowInterceptTouchEvent(true)
            lastFocusX = detector.focusX
            lastFocusY = detector.focusY
            return true
        }

        override fun onScaleEnd(detector: ScaleGestureDetector) {
            this@ZoomView.parent?.requestDisallowInterceptTouchEvent(false)
        }
    }

    init {
        setOnTouchListener()
        mScaleDetector = ScaleGestureDetector(context, mScaleGestureListener)
        scaleType = ScaleType.MATRIX
        addOnLayoutChangeListener { _, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            if (left != oldLeft || top != oldTop || right != oldRight || bottom != oldBottom) {
                updateBaseMatrix()
            }
        }
    }

    private fun setOnTouchListener() {
        setOnTouchListener { v, ev ->
            when (ev.action) {
                MotionEvent.ACTION_DOWN -> {
                    lastTouchX = ev.x
                    lastTouchY = ev.y
                    isDragging = false
                }

                MotionEvent.ACTION_MOVE -> {
                    if (getScale() > MIN_SCALE) {
                        this@ZoomView.parent?.requestDisallowInterceptTouchEvent(true)
                        val dx = ev.x - lastTouchX
                        val dy = ev.y - lastTouchY

                        if (!isDragging) {
                            isDragging = sqrt(dx * dx + dy * dy) >= 10f
                        }
                        if (isDragging) {
                            mSuppMatrix.postTranslate(dx, dy)
                            if (checkMatrixBounds()) {
                                imageMatrix = getDrawMatrix()
                            }
                        }
                        lastTouchX = ev.x
                        lastTouchY = ev.y
                    }
                }

                MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                    this@ZoomView.parent?.requestDisallowInterceptTouchEvent(false)
                    if (getScale() < MIN_SCALE) {
                        val rect = getDisplayRect()
                        if (rect != null) {
                            v.post(
                                AnimatedZoom(
                                    getScale(), MIN_SCALE,
                                    rect.centerX(), rect.centerY()
                                )
                            )
                        }
                    } else if (getScale() > MAX_SCALE) {
                        val rect = getDisplayRect()
                        if (rect != null) {
                            v.post(
                                AnimatedZoom(
                                    getScale(), MAX_SCALE,
                                    rect.centerX(), rect.centerY()
                                )
                            )
                        }
                    }
                    isDragging = false
                    v.performClick()
                }
            }
            return@setOnTouchListener mScaleDetector?.onTouchEvent(ev) ?: false
        }
    }

    private fun temp(scaleFactor: Float, focusX: Float, focusY: Float, dx: Float, dy: Float) {
        if (getScale() < MAX_SCALE || scaleFactor < 1f) {
            mSuppMatrix.postScale(scaleFactor, scaleFactor, focusX, focusY)
            mSuppMatrix.postTranslate(dx, dy)
            if (checkMatrixBounds()) {
                imageMatrix = getDrawMatrix()
            }
        }
    }

    private fun getScale(): Float {
        val x = getValue(mSuppMatrix, Matrix.MSCALE_X).toDouble().pow(2.0).toFloat()
        val y = getValue(mSuppMatrix, Matrix.MSKEW_Y).toDouble().pow(2.0).toFloat()
        val square = sqrt((x + y).toDouble())
        return square.toFloat()
    }

    private fun getDrawMatrix(): Matrix {
        mDrawMatrix.set(mBaseMatrix)
        mDrawMatrix.postConcat(mSuppMatrix)
        return mDrawMatrix
    }

    private fun getDisplayRect(): RectF? {
        checkMatrixBounds()
        return getDisplayRect(getDrawMatrix())
    }

    private fun getValue(matrix: Matrix, whichValue: Int): Float {
        matrix.getValues(mMatrixValues)
        return mMatrixValues[whichValue]
    }

    private fun getDisplayRect(matrix: Matrix): RectF? {
        drawable?.let {
            mDisplayRect[0f, 0f, drawable.intrinsicWidth.toFloat()] =
                drawable.intrinsicHeight.toFloat()
            matrix.mapRect(mDisplayRect)
            return mDisplayRect
        }
        return null
    }

    private fun updateBaseMatrix() {
        if (drawable == null) {
            return
        }
        val viewWidth = getImageViewWidth().toFloat()
        val viewHeight = getImageViewHeight().toFloat()
        val drawableWidth = drawable.intrinsicWidth
        val drawableHeight = drawable.intrinsicHeight
        mBaseMatrix.reset()
        var mTempSrc = RectF(0f, 0f, drawableWidth.toFloat(), drawableHeight.toFloat())
        val mTempDst = RectF(0f, 0f, viewWidth, viewHeight)
        if (BASE_ROTATION.toInt() % 180 != 0) {
            mTempSrc = RectF(0f, 0f, drawableHeight.toFloat(), drawableWidth.toFloat())
        }
        mBaseMatrix.setRectToRect(
            mTempSrc,
            mTempDst,
            Matrix.ScaleToFit.CENTER
        )
        mSuppMatrix.reset()
        mSuppMatrix.postRotate(BASE_ROTATION % 360)
        imageMatrix = getDrawMatrix()
    }

    private fun checkMatrixBounds(): Boolean {
        val rect = getDisplayRect(getDrawMatrix()) ?: return false
        val height = rect.height()
        val width = rect.width()
        var deltaX = 0f
        var deltaY = 0f
        val viewHeight = getImageViewHeight()
        if (height <= viewHeight) {
            deltaY = (viewHeight - height) / 2 - rect.top
        } else if (rect.top > 0) {
            deltaY = -rect.top
        } else if (rect.bottom < viewHeight) {
            deltaY = viewHeight - rect.bottom
        }
        val viewWidth = getImageViewWidth()
        if (width <= viewWidth) {
            deltaX = (viewWidth - width) / 2 - rect.left
        } else if (rect.left > 0) {
            deltaX = -rect.left
        } else if (rect.right < viewWidth) {
            deltaX = viewWidth - rect.right
        }
        mSuppMatrix.postTranslate(deltaX, deltaY)
        return true
    }

    private fun getImageViewWidth() = width - paddingLeft - paddingRight

    private fun getImageViewHeight() = height - paddingTop - paddingBottom

    inner class AnimatedZoom(
        currentZoom: Float, targetZoom: Float,
        private val mFocalX: Float, private val mFocalY: Float
    ) : Runnable {
        private var mInterpolator: Interpolator = AccelerateDecelerateInterpolator()
        private val mStartTime: Long = System.currentTimeMillis()
        private val mZoomStart: Float
        private val mZoomEnd: Float

        init {
            mZoomStart = currentZoom
            mZoomEnd = targetZoom
        }

        override fun run() {
            val t = interpolate()
            val scale = mZoomStart + t * (mZoomEnd - mZoomStart)
            val deltaScale: Float = scale / getScale()
            temp(deltaScale, mFocalX, mFocalY, 0f, 0f)
            if (t < 1f) {
                postOnAnimation(this)
            }
        }

        private fun interpolate(): Float {
            var t: Float = 1f * (System.currentTimeMillis() - mStartTime) / ZOOM_DURATION
            t = min(1f, t)
            t = mInterpolator.getInterpolation(t)
            return t
        }
    }

}