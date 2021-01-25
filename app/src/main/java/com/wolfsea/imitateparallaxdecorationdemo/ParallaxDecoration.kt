package com.wolfsea.imitateparallaxdecorationdemo
import android.app.ActivityManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import androidx.recyclerview.widget.RecyclerView
import java.lang.IllegalArgumentException
import java.lang.NullPointerException

/**
 *@desc 自定义Decoration
 *@author liuliheng
 *@time 2021/1/24  23:07
 **/
open class ParallaxDecoration(private val context: Context) : RecyclerView.ItemDecoration() {

    private var mScreenWidth = 0
    private var mScreenHeight = 0
    private var mMaxVisibleCount = 0
    private var mMinVisibleCount = 0

    private var mBitmapWidth = 0
    private var mBitmapHeight = 0
    private var mBitmapCount = 0

    private var bitmapPool = mutableListOf<Bitmap>()
    private var mIsCanHorizontal = true

    //滚动视差因子
    var parallax : Float = 1.0F

    var autoFill = false
    private var activityManager : ActivityManager? = null
    private lateinit var options: BitmapFactory.Options

    private var mScale = 1.0F
    private var mScaleBitmapWidth = 0
    private var mScaleBitmapHeight = 0

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {

        super.onDraw(c, parent, state)
        if (bitmapPool.isNotEmpty()) {

            val layoutManager = parent.layoutManager ?: throw NullPointerException("LayoutManager不能为空")
            //1.检查滚动的方向
            mIsCanHorizontal = layoutManager.canScrollHorizontally()
            //2.检查最大可视数量
            //3.如果是自动填充满,计算缩放的位图大小
            if (mScreenWidth == 0 || mScreenHeight == 0) {

                mScreenWidth = c.width
                mScreenHeight = c.height

                val allInScreen: Int
                val doubleOutOfScreen: Boolean
                if (mIsCanHorizontal) {
                    if (autoFill) {
                        mScale = mScreenHeight * 1.0F / mBitmapHeight
                        mScaleBitmapWidth = (mBitmapWidth * mScale).toInt()
                    }

                    allInScreen  = mScreenWidth / mScaleBitmapWidth
                    doubleOutOfScreen = mScreenWidth % mScaleBitmapWidth > 1
                } else {
                    if (autoFill) {
                        mScale = mScreenWidth * 1.0F / mBitmapWidth
                        mScaleBitmapHeight = (mBitmapHeight * mScale).toInt()
                    }

                    allInScreen = mScreenHeight / mScaleBitmapHeight
                    doubleOutOfScreen = mScreenHeight % mScaleBitmapHeight > 1
                }

                mMinVisibleCount = allInScreen + 1
                mMaxVisibleCount = if (doubleOutOfScreen) allInScreen + 2 else mMinVisibleCount
            }
            //4.找到第一个可见的Item的索引
            //5.计算第一个可见索引的偏移量
            val parallaxOffset : Float
            val firstVisible: Int
            val firstVisibleOffset : Float
            if (mIsCanHorizontal) {

                parallaxOffset = layoutManager.computeHorizontalScrollOffset(state) * parallax
                firstVisible = (parallaxOffset / mScaleBitmapWidth).toInt()
                firstVisibleOffset = parallaxOffset % mScaleBitmapWidth
            } else {

                parallaxOffset = layoutManager.computeVerticalScrollOffset(state) * parallax
                firstVisible = (parallaxOffset / mScaleBitmapHeight).toInt()
                firstVisibleOffset = parallaxOffset % mScaleBitmapHeight
            }
            //6.计算最佳的绘制个数
            val bestDrawCount = if (firstVisibleOffset.toInt() == 0) mMinVisibleCount else mMaxVisibleCount
            //7.平移到第一个可见的Item偏移量处
            c.save()
            if (mIsCanHorizontal) {
                c.translate(-firstVisibleOffset, 0F)
            } else {
                c.translate(0F, -firstVisibleOffset)
            }
            //8.如果是自动填充,缩放画布绘制
            if (autoFill) {
                c.scale(mScale, mScale)
            }
            //9.绘制当前第一个可见的Bitmap,最大的循环绘制个数是由第6步确定的最佳绘制个数
            for ((i, currentIndex) in (firstVisible until firstVisible + bestDrawCount).withIndex()) {
                if (mIsCanHorizontal) {

                    c.drawBitmap(
                            bitmapPool[currentIndex % mBitmapCount],
                            i * mBitmapWidth.toFloat(),
                            0F, null
                    )
                } else {

                    c.drawBitmap(
                            bitmapPool[currentIndex % mBitmapCount],
                            0F,
                            i * mBitmapHeight.toFloat(), null
                    )
                }
            }
            c.restore()
        }
    }

    /**
     *@desc 设置资源
     *@author:liuliheng
     *@time: 2021/1/24 23:28
    **/
    fun setUpBitmap(bitmaps: List<Bitmap>) {
        bitmapPool.clear()
        bitmapPool.addAll(bitmaps)
        updateConfig()
    }

    /**
     *@desc 增加位图
     *@author:liuliheng
     *@time: 2021/1/24 23:29
    **/
    fun addBitmap(bitmap: Bitmap) {
        bitmapPool.add(bitmap)
        updateConfig()
    }

    /**
     *@desc  设置资源
     *@author:liuliheng
     *@time: 2021/1/24 23:30
    **/
    fun setupResource(resourceIds: List<Int>) {
        bitmapPool.clear()
        for (resourceId in resourceIds) {
            bitmapPool.add(decodeBitmap(resourceId))
        }
        updateConfig()
    }

    /**
     *@desc 增加资源
     *@author:liuliheng
     *@time: 2021/1/24 23:31
    **/
    fun addResource(resourceId: Int) {
        bitmapPool.add(decodeBitmap(resourceId))
        updateConfig()
    }

    /**
     *@desc  解析Bitmap
     *@author:liuliheng
     *@time: 2021/1/24 23:22
    **/
    private fun decodeBitmap(resourceId : Int) : Bitmap {
        if (activityManager == null) {

            options = BitmapFactory.Options()
            activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            if (activityManager!!.isLowRamDevice) {

                options.inPreferredConfig = Bitmap.Config.RGB_565
            }
        }

        return BitmapFactory.decodeResource(context.resources, resourceId, options)
    }

    /**
     *@desc 更新配置
     *@author:liuliheng
     *@time: 2021/1/24 23:17
    **/
    private fun updateConfig() {
        mBitmapCount = bitmapPool.size
        mBitmapWidth = bitmapPool[0].width
        mBitmapHeight = bitmapPool[0].height
        mScaleBitmapWidth = mBitmapWidth
        mScaleBitmapHeight = mBitmapHeight
        bitmapPool.forEach {
            if (it.width != mBitmapWidth || it.height != mBitmapHeight) {
                throw IllegalArgumentException("图片的宽高必须一致")
            }
        }
    }

}