package com.example.docwatcher.components

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.widget.AppCompatImageView

/*
 * Created by Sudhanshu Kumar on 08/09/23.
 */

class ZoomView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr), ScaleGestureDetector.OnScaleGestureListener{

    private val scaleGestureDetector: ScaleGestureDetector = ScaleGestureDetector(context, this)
    private var currentScaleFactor = 1.0f
    private var targetScaleFactor = 1.0f
    private var pinchStarted = false

    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleGestureDetector.onTouchEvent(event)
        if (event.action == MotionEvent.ACTION_DOWN) {
            pinchStarted = false
        }
        return true
    }

    override fun onScale(detector: ScaleGestureDetector): Boolean {
        val scaleFactor = detector.scaleFactor
        if (!pinchStarted && scaleFactor!= 1.0f) {
            pinchStarted = true
            applyZoomStartAnimation()
        }
        targetScaleFactor *= scaleFactor
        this.scaleX *= scaleFactor
        this.scaleY *= scaleFactor
        return true
    }

    override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
        targetScaleFactor = currentScaleFactor
        pinchStarted = false
        return true
    }

    override fun onScaleEnd(detector: ScaleGestureDetector) {
        val animator = ValueAnimator.ofFloat(scaleX, 1.0f)
        animator.duration = 300 // Animation duration in milliseconds
        animator.interpolator = DecelerateInterpolator()
        animator.addUpdateListener { animation ->
            val animatedValue = animation.animatedValue as Float
            scaleX = animatedValue
            scaleY = animatedValue
        }
        animator.start()
        currentScaleFactor = 1.0f
    }

    private fun applyZoomStartAnimation() {
        val animator = ValueAnimator.ofFloat(scaleX, 2.5f)
        animator.duration = 200 // Animation duration in milliseconds
        animator.interpolator = AccelerateDecelerateInterpolator() // Apply the interpolator
        animator.addUpdateListener { animation ->
            val animatedValue = animation.animatedValue as Float
            scaleX = animatedValue
            scaleY = animatedValue
        }
        animator.start()
    }

}