package com.wolfsea.imitateparallaxdecorationdemo
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    override fun onContentChanged() {

        super.onContentChanged()
        btn_horizontal.setOnClickListener(this)
        btn_vertical.setOnClickListener(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btn_horizontal -> {

                val intent = Intent(this@MainActivity, HorizontalActivity::class.java)
                startActivity(intent)
            }

            R.id.btn_vertical -> {


            }

            else -> {}
        }
    }
}