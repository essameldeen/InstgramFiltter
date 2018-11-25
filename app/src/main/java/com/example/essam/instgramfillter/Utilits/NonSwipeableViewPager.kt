package com.example.essam.instgramfillter.Utilits

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.animation.DecelerateInterpolator
import android.widget.Scroller
import java.lang.Exception


class NonSwipeableViewPager : ViewPager {
    constructor(context: Context) : super(context) {
        setMyScroller()
    }

    constructor(context: Context, attributes: AttributeSet) : super(context, attributes) {
        setMyScroller()
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return false
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return false
    }

    private fun setMyScroller() {
        try {
            val viewPager = ViewPager::class.java
            val scroller = viewPager.getDeclaredField("mScroller")
            scroller.isAccessible = true
            scroller.set(this, MyScroller(context))

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}

class MyScroller(context: Context?) : Scroller(context, DecelerateInterpolator()) {
    override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int, duration: Int) {
        super.startScroll(startX, startY, dx, dy, 400)
    }
}
