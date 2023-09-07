package com.example.docwatcher.components.zoomimage

/*
 * Created by Sudhanshu Kumar on 05/09/23.
 */

interface OnGestureListener {

    fun onDrag(dx: Float, dy: Float)

    fun onFling(
        startX: Float, startY: Float, velocityX: Float,
        velocityY: Float
    )

    fun onScale(scaleFactor: Float, focusX: Float, focusY: Float)

    fun onScale(scaleFactor: Float, focusX: Float, focusY: Float, dx: Float, dy: Float)

}