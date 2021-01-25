package com.wolfsea.imitateparallaxdecorationdemo

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.animation.OvershootInterpolator
import android.widget.LinearLayout
import kotlin.math.atan
import kotlin.math.tan

/**
 *@desc  视差自定义View
 *@author liuliheng
 *@time 2021/1/24  21:48
 **/
class ParallaxRootLayout @JvmOverloads constructor(
    context: Context,
    attr: AttributeSet? = null,
    defaultId: Int = 0
) : LinearLayout(context, attr, defaultId) {

    private var mAngle = 0F
    private var mWidth = 0F
    private var mHeight = 0F

    private var path1 : Path? = null
    private var path2 : Path? = null

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val anim = ValueAnimator.ofFloat(0.0F, 1.0F).apply {
        duration = 1200
        interpolator = OvershootInterpolator(1.5F)
        addUpdateListener {

            val percent: Float = it.animatedValue as Float

            path1?.apply {
                val r = 1.1F.minus(percent)
                reset()
                moveTo(mWidth, 0F)
                lineTo(mWidth, mHeight.times(0.3F))
                lineTo(
                    mWidth.times(r),
                    mHeight.times(0.3F).plus(tan(mAngle).times(mWidth).times(percent))
                )
                lineTo(mWidth.times(r), 0.0F)
                close()
            }

            path2?.apply {
                reset()
                moveTo(0F, mHeight.times(0.7F))
                lineTo(0F, mHeight)
                lineTo(mWidth.times(0.9F).times(percent), mHeight)
                lineTo(
                    mWidth.times(0.9F).times(percent),
                    mHeight.times(0.7F).minus(tan(mAngle).times(0.9F).times(mWidth).times(percent))
                )
                close()
            }

            invalidate()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {

        super.onSizeChanged(w, h, oldw, oldh)

        mWidth = w.toFloat()
        mHeight = h.toFloat()

        path1 = Path()
        path2 = Path()

        mAngle = atan(mHeight.times(0.2f).div(mWidth))
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        anim.start()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (anim.isRunning) {
            anim.end()
            anim.cancel()
        }
    }

    override fun dispatchDraw(canvas: Canvas?) {

        canvas!!.drawColor(resources.getColor(R.color.teal_200))

        path1?.run {
            paint.color = resources.getColor(R.color.purple_500)
            canvas.drawPath(this, paint)
        }

        path2?.run {
            paint.color = resources.getColor(R.color.purple_700)
            canvas.drawPath(this, paint)
        }

        super.dispatchDraw(canvas)
    }

}