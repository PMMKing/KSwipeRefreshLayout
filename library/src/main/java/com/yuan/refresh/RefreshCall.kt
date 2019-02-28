package com.yuan.refresh

/**
 * Created by shucheng.qu on 2018/6/29
 */
interface RefreshCall {

    fun startRefresh()

    fun refreshing()

    fun endRefresh()

    fun refreshDiff(diffY: Int)

}