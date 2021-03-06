package com.wolfsea.imitateparallaxdecorationdemo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_horizontal.*

class HorizontalActivity : AppCompatActivity() {

    private val bgs = intArrayOf(
        R.drawable.rd_gua_seed_1, R.drawable.rd_gua_seed_2, R.drawable.rd_gua_seed_3,
        R.drawable.rd_gua_seed_4, R.drawable.rd_gua_seed_5, R.drawable.rd_gua_seed_6,
    )

    private lateinit var parallaxAdapter: ParallaxAdapter
    private var lastItemDecoration : RecyclerView.ItemDecoration? = null

    private var parallaxSize = 1F
    private var autoFillBitmap = false

    override fun onContentChanged() {

        super.onContentChanged()

        parallax_tv.text = "parallax:0.0"
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_horizontal)

        parallaxAdapter = ParallaxAdapter()
        parallax_horizontal_rv.apply {
            layoutManager =
                LinearLayoutManager(this@HorizontalActivity, LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
            adapter = parallaxAdapter
            lastItemDecoration = ParallaxDecoration(this.context).apply {
                setupResource(bgs.asList())
                parallax = parallaxSize
                autoFill = autoFillBitmap
            }
            addItemDecoration(lastItemDecoration!!)
        }



        parallax_seek_bar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                parallaxSize = progress * 1.0F / 100
                parallax_tv.text = "parallax:$parallaxSize"
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                updateItemDecoration(autoFillBitmap)
            }
        })

        parallax_cb.setOnCheckedChangeListener { _, mIsChecked ->
            run {
                if (mIsChecked) {

                    updateItemDecoration(true)
                } else {

                    updateItemDecoration(false)
                }
            }
        }

    }

    /**
     *@desc 更新ItemDecoration
     *@author:liuliheng
     *@time: 2021/1/26 0:11
    **/
    private fun updateItemDecoration(mIsAutoFill: Boolean) {
        if (lastItemDecoration != null) {
            parallax_horizontal_rv.removeItemDecoration(lastItemDecoration!!)
        }
        autoFillBitmap = mIsAutoFill
        lastItemDecoration = ParallaxDecoration(this).apply {
            setupResource(bgs.asList())
            parallax = parallaxSize
            autoFill = autoFillBitmap
        }
        parallax_horizontal_rv.addItemDecoration(lastItemDecoration!!)
    }
}