package com.example.docwatcher.components

import android.content.Context
import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import com.example.docwatcher.components.zoomimage.OnMatrixChangedListener
import com.example.docwatcher.components.zoomimage.OnOutsidePhotoTapListener
import com.example.docwatcher.components.zoomimage.OnPhotoTapListener
import com.example.docwatcher.components.zoomimage.OnScaleChangedListener
import com.example.docwatcher.components.zoomimage.OnSingleFlingListener
import com.example.docwatcher.components.zoomimage.OnViewDragListener
import com.example.docwatcher.components.zoomimage.OnViewTapListener
import com.example.docwatcher.components.zoomimage.PhotoViewerAttacher

/*
 * Created by Sudhanshu Kumar on 07/09/23.
 */

internal class ZoomImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatImageView(context, attrs) {

    private var attacher: PhotoViewerAttacher? = null
    private var pendingScaleType: ImageView.ScaleType? = null

    init {
        attacher = PhotoViewerAttacher(this)
        //We always pose as a Matrix scale type, though we can change to another scale type
        //via the attacher
        super.setScaleType(ImageView.ScaleType.MATRIX)
        //apply the previously applied scale type
        if (pendingScaleType != null) {
            scaleType = pendingScaleType
            pendingScaleType = null
        }
    }

    /**
     * Get the current [PhotoViewAttacher] for this view. Be wary of holding on to references
     * to this attacher, as it has a reference to this view, which, if a reference is held in the
     * wrong place, can cause memory leaks.
     *
     * @return the attacher.
     */
    fun getAttacher(): PhotoViewerAttacher? {
        return attacher
    }

    override fun getScaleType(): ImageView.ScaleType? {
        return attacher!!.getScaleType()
    }

    override fun getImageMatrix(): Matrix? {
        return attacher!!.getImageMatrix()
    }

    override fun setOnLongClickListener(l: View.OnLongClickListener?) {
        attacher!!.setOnLongClickListener(l)
    }

    override fun setOnClickListener(l: View.OnClickListener?) {
        attacher!!.setOnClickListener(l)
    }

    override fun setScaleType(scaleType: ImageView.ScaleType?) {
        if (attacher == null) {
            pendingScaleType = scaleType
        } else {
            attacher!!.setScaleType(scaleType!!)
        }
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        // setImageBitmap calls through to this method
        attacher?.update()
    }

    override fun setImageResource(resId: Int) {
        super.setImageResource(resId)
        if (attacher != null) {
            attacher!!.update()
        }
    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        if (attacher != null) {
            attacher!!.update()
        }
    }

    override fun setFrame(l: Int, t: Int, r: Int, b: Int): Boolean {
        val changed = super.setFrame(l, t, r, b)
        if (changed) {
            attacher!!.update()
        }
        return changed
    }

    fun setRotationTo(rotationDegree: Float) {
        attacher!!.setRotationTo(rotationDegree)
    }

    fun setRotationBy(rotationDegree: Float) {
        attacher!!.setRotationBy(rotationDegree)
    }

    fun isZoomable(): Boolean {
        return attacher!!.isZoomable()
    }

    fun setZoomable(zoomable: Boolean) {
        attacher!!.setZoomable(zoomable)
    }

    fun getDisplayRect(): RectF? {
        return attacher!!.getDisplayRect()
    }

    fun getDisplayMatrix(matrix: Matrix?) {
        attacher!!.getDisplayMatrix(matrix!!)
    }

    fun setDisplayMatrix(finalRectangle: Matrix?): Boolean {
        return attacher!!.setDisplayMatrix(finalRectangle)
    }

    fun getSuppMatrix(matrix: Matrix?) {
        attacher!!.getSuppMatrix(matrix!!)
    }

    fun setSuppMatrix(matrix: Matrix?): Boolean {
        return attacher!!.setDisplayMatrix(matrix)
    }

    fun getMinimumScale(): Float {
        return attacher!!.getMinimumScale()
    }

    fun getMediumScale(): Float {
        return attacher!!.getMediumScale()
    }

    fun getMaximumScale(): Float {
        return attacher!!.getMaximumScale()
    }

    fun getScale(): Float {
        return attacher!!.getScale()
    }

    fun setAllowParentInterceptOnEdge(allow: Boolean) {
        attacher!!.setAllowParentInterceptOnEdge(allow)
    }

    fun setMinimumScale(minimumScale: Float) {
        attacher!!.setMinimumScale(minimumScale)
    }

    fun setMediumScale(mediumScale: Float) {
        attacher!!.setMediumScale(mediumScale)
    }

    fun setMaximumScale(maximumScale: Float) {
        attacher!!.setMaximumScale(maximumScale)
    }

    fun setScaleLevels(minimumScale: Float, mediumScale: Float, maximumScale: Float) {
        attacher!!.setScaleLevels(minimumScale, mediumScale, maximumScale)
    }

    fun setOnMatrixChangeListener(listener: OnMatrixChangedListener?) {
        attacher!!.setOnMatrixChangeListener(listener)
    }

    fun setOnPhotoTapListener(listener: OnPhotoTapListener?) {
        attacher!!.setOnPhotoTapListener(listener)
    }

    fun setOnOutsidePhotoTapListener(listener: OnOutsidePhotoTapListener?) {
        attacher!!.setOnOutsidePhotoTapListener(listener)
    }

    fun setOnViewTapListener(listener: OnViewTapListener?) {
        attacher!!.setOnViewTapListener(listener)
    }

    fun setOnViewDragListener(listener: OnViewDragListener?) {
        attacher!!.setOnViewDragListener(listener)
    }

    fun setScale(scale: Float) {
        attacher!!.setScale(scale)
    }

    fun setScale(scale: Float, animate: Boolean) {
        attacher!!.setScale(scale, animate)
    }

    fun setScale(scale: Float, focalX: Float, focalY: Float, animate: Boolean) {
        attacher!!.setScale(scale, focalX, focalY, animate)
    }

    fun setZoomTransitionDuration(milliseconds: Int) {
        attacher!!.setZoomTransitionDuration(milliseconds)
    }

    fun setOnDoubleTapListener(onDoubleTapListener: GestureDetector.OnDoubleTapListener?) {
        attacher!!.setOnDoubleTapListener(onDoubleTapListener)
    }

    fun setOnScaleChangeListener(onScaleChangedListener: OnScaleChangedListener?) {
        attacher!!.setOnScaleChangeListener(onScaleChangedListener)
    }

    fun setOnSingleFlingListener(onSingleFlingListener: OnSingleFlingListener?) {
        attacher!!.setOnSingleFlingListener(onSingleFlingListener)
    }

}