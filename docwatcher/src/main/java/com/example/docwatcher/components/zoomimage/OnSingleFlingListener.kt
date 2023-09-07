package com.example.docwatcher.components.zoomimage

import android.view.MotionEvent

/*
 * Created by Sudhanshu Kumar on 05/09/23.
 */

interface OnSingleFlingListener {

    fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean

}