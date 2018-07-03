package com.yuan.kswiperefreshlayout

import android.content.Context
import android.content.Intent
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

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val view = LayoutInflater.from(this).inflate(R.layout.image, null, false)
        ks_layout.setLoadView(view)
        ks_layout.setOnRefreshListener(object : OnRefreshListener {
            override fun onRefresh() {
                Handler().postDelayed({ ks_layout.setRefresh(false) }, 1500)
            }

            override fun onLoad() {
                Handler().postDelayed({ ks_layout.setRefresh(false) }, 1500)
            }
        })
        ks_layout.setRefresh(true)


        val adapter = MultiAdapter<String>(this).addTypeView(object : ITypeView<String> {
            override fun isForViewType(p0: String?, p1: Int): Boolean {
                return true
            }

            override fun createViewHolder(p0: Context, p1: ViewGroup): BaseViewHolder<*> {
                return ViewHolder(p0, LayoutInflater.from(p0).inflate(R.layout.image, p1, false))
            }
        })
        rv_list.layoutManager = LinearLayoutManager(this)
        rv_list.adapter = adapter
        val list = mutableListOf<String>()
        for (i in 1..2) {
            list.add("")
        }
        adapter.data = list
        adapter.setOnItemClickListener { view, any, i ->
            startActivity(Intent(this, TestActivity::class.java))
        }
    }
}
