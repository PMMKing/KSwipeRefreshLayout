package com.yuan.kswiperefreshlayout

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.widget.ImageView
import com.yuan.refresh.OnRefreshListener
import kotlinx.android.synthetic.main.activity_main.*

class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        val view = LayoutInflater.from(this).inflate(R.layout.image, null, false)

        val imageView2 = ImageView(this)
        imageView2.setImageResource(R.mipmap.ic_launcher)

//        ks_layout.setRefreshView(view)
        ks_layout.setRefreshView(CustomView(this))

        ks_layout.setOnRefreshListener(object : OnRefreshListener {
            override fun onRefresh() {
                Handler().postDelayed({ ks_layout.setRefresh(false) }, 1000)
            }

            override fun onLoad() {
                Handler().postDelayed({ ks_layout.setRefresh(false) }, 1000)
            }
        })
    }
}
