package com.yuan.kswiperefreshlayout

import android.content.Context
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import com.cheng.rvadapter.adapter.MultiAdapter
import com.cheng.rvadapter.holder.BaseViewHolder
import com.cheng.rvadapter.manage.ITypeView
import com.yuan.library.OnRefreshListener
import kotlinx.android.synthetic.main.activity_main.*

class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        val view = LayoutInflater.from(this).inflate(R.layout.image, null, false)

        val imageView2 = ImageView(this)
        imageView2.setImageResource(R.mipmap.ic_launcher)

        ks_layout.setRefreshView(view)

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
