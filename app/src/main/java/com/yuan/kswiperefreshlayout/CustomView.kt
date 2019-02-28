package com.yuan.kswiperefreshlayout

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.yuan.refresh.RefreshCall
import kotlinx.android.synthetic.main.refresh_header.view.*
import kotlin.concurrent.thread

/**
 * Created by shucheng.qu on 2018/7/2
 */
class CustomView : LinearLayout, RefreshCall {

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
    }

    fun initView() {
        LayoutInflater.from(context).inflate(R.layout.refresh_header, this, true)
        setBackgroundColor(Color.GRAY)
    }

    override fun startRefresh() {
        tv.text = "开始下拉刷新"
    }

    override fun refreshing() {
        tv.text = "下拉刷新中..."
        thread {
            while (true) {
                Thread.sleep(6)
                iv.post {
                    iv.rotation++
                }
            }
        }
    }

    override fun endRefresh() {
        tv.text = "下拉刷新成功"
    }

    override fun refreshDiff(diffY: Int) {
        iv.rotation = diffY.toFloat()
    }
}