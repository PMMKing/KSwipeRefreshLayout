package com.yuan.library

import android.content.Context
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.yuan.library.R.attr.loadView
import com.yuan.library.R.attr.refreshView

/**
 * Created by shucheng.qu on 2018/6/26
 */
class KSwipeRefreshLayout : ViewGroup {

    var mTargetView: View? = null
    var mRefreshView: View? = null
    var mRefreshCall: RefreshCall? = null
    var mLoadView: View? = null
    var mLoadCall: RefreshCall? = null
    var refreshState: RefreshState = RefreshState.DEFAULT
    var canRefresh = true
    var canLoad = false
    var rawY: Float = 0f
    private var refreshListener: OnRefreshListener? = null

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView(attrs)
    }

    fun initView(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.KSwipeRefreshLayout)
        val refreshMode = typedArray.getInt(R.styleable.KSwipeRefreshLayout_refreshMode, 0)
        val refreshViewName = typedArray.getString(R.styleable.KSwipeRefreshLayout_refreshView)
        val loadViewName = typedArray.getString(R.styleable.KSwipeRefreshLayout_loadView)
        when (refreshMode) {
            0 -> {
                canRefresh = true
                canLoad = false
            }
            1 -> {
                canRefresh = false
                canLoad = true
            }
            2 -> {
                canRefresh = true
                canLoad = true
            }
            else -> {
                canRefresh = false
                canLoad = false
            }
        }

        try {
            val clazz = Class.forName(refreshViewName)
            val constructor = clazz.getConstructor(Context::class.java)
            val refreshView = constructor.newInstance(context)
            if (refreshView is View) setRefreshView(refreshView)
        } catch (e: Exception) {

        }
        try {
            val clazz = Class.forName(loadViewName)
            val constructor = clazz.getConstructor(Context::class.java)
            val loadView = constructor.newInstance(context)
            if (loadView is View) setLoadView(loadView)
        } catch (e: Exception) {

        }

        typedArray.recycle()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        for (i in 0..childCount) {
            val view = getChildAt(i)
            if (view != mRefreshView && view != mLoadView) {
                mTargetView = view
                break
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mRefreshView?.let {
            measureChildWithMargins(mRefreshView, widthMeasureSpec, 0, heightMeasureSpec, 0)
        }
        mTargetView?.let {
            measureChildWithMargins(mTargetView, widthMeasureSpec, 0, heightMeasureSpec, 0)
        }
        mLoadView?.let {
            measureChildWithMargins(mLoadView, widthMeasureSpec, 0, heightMeasureSpec, 0)
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        layoutChildren()
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        val action = ev.action
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                rawY = ev.rawY
            }
            MotionEvent.ACTION_MOVE -> {
                val diffY = ev.rawY - rawY
                val can = (diffY > 10 && canRefresh()) || (diffY < -10 && canLoad())
                if (can) return true
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                rawY = event.rawY
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val diffY = event.rawY - rawY
                val diff = diff(diffY)
                when (refreshState) {
                    RefreshState.DEFAULT -> {
                        layoutChildren(diff(diffY))
                        refreshState = if (diffY >= 0) RefreshState.DOWNPULL else RefreshState.UPPULL
                        when (refreshState) {
                            RefreshState.DOWNPULL -> {
                                mRefreshCall?.startRefresh()
                                mRefreshCall?.refreshDiff(diff)
                            }
                            RefreshState.UPPULL -> {
                                mLoadCall?.startRefresh()
                                mLoadCall?.refreshDiff(diff)
                            }
                        }
                    }
                    RefreshState.DOWNPULL -> {
                        if (diffY >= 0 && canRefresh()) {
                            layoutChildren(diff)
                            mRefreshCall?.refreshDiff(diff)
                        } else {
                            layoutChildren()
                            rawY = event.rawY
                        }
                    }
                    RefreshState.UPPULL -> {
                        if (diffY <= 0 && canLoad()) {
                            layoutChildren(diff)
                            mLoadCall?.refreshDiff(diff)
                        } else {
                            layoutChildren()
                            rawY = event.rawY
                        }
                    }
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                val diffY = Math.abs(event.rawY - rawY)
                val diff = diff(diffY)
                when (refreshState) {
                    RefreshState.DOWNPULL -> {
                        val height = mRefreshView?.measuredHeight ?: 200
                        if (diff > height) {
                            downRefresh(height)
                        } else {
                            refreshState = RefreshState.DEFAULT
                            layoutChildren()
                        }
                    }
                    RefreshState.UPPULL -> {
                        val height = mLoadView?.measuredHeight ?: 200
                        if (diff > height) {
                            upLoad(height)
                        } else {
                            refreshState = RefreshState.DEFAULT
                            layoutChildren()
                        }
                    }
                }

            }

        }
        return super.onTouchEvent(event)
    }

    private fun downRefresh(height: Int = 200) {
        layoutChildren(height)
        refreshState = RefreshState.REFRESHING
        refreshListener?.onRefresh()
        mRefreshCall?.refreshing()
    }

    private fun upLoad(height: Int = 200) {
        layoutChildren(-height)
        refreshState = RefreshState.REFRESHING
        refreshListener?.onLoad()
        mLoadCall?.refreshing()
    }

    /*
    * 计算diff，弹性
    * */
    private fun diff(diffY: Float): Int {
        val abs = Math.abs(diffY)
        val diffAbs = 0.25f * abs + 100F * abs / (abs + 100)
        if (diffY > 0) return diffAbs.toInt() else return (-diffAbs).toInt()
    }


    fun setRefreshView(mRefreshView: View?) {
        mRefreshView?.let {
            this.mRefreshView?.let {
                removeView(this.mRefreshView)
            }
            this.mRefreshView = mRefreshView
            mRefreshView.layoutParams = ViewGroup.MarginLayoutParams(-1, -2)
            addView(mRefreshView)
            if (mRefreshView is RefreshCall) mRefreshCall = mRefreshView
        }
    }

    fun setLoadView(mLoadView: View?) {
        mLoadView?.let {
            this.mLoadView?.let {
                removeView(this.mLoadView)
            }
            this.mLoadView = mLoadView
            mLoadView.layoutParams = ViewGroup.MarginLayoutParams(-1, -2)
            addView(mLoadView)
            if (mLoadView is RefreshCall) mLoadCall = mLoadView
        }
    }

    private fun canRefresh(): Boolean {
        val canScrollVertically = ViewCompat.canScrollVertically(mTargetView, -1)
        return !canScrollVertically && canRefresh && mRefreshView != null
    }

    private fun canLoad(): Boolean {
        val canScrollVertically = ViewCompat.canScrollVertically(mTargetView, 1)
        return !canScrollVertically && canLoad && mLoadView != null
    }

    private fun layoutChildren(diff: Int = 0) {
        if (mTargetView == null) return
        if (diff == 0 && refreshState == RefreshState.REFRESHING) return
        mRefreshView?.let {
            val headerLeft = paddingLeft
            val headerTop = paddingTop - it.measuredHeight + diff
            val headerRight = measuredWidth - paddingRight
            val headerBottom = headerTop + it.measuredHeight
            it.layout(headerLeft, headerTop, headerRight, headerBottom)
        }
        mTargetView?.let {
            val targetLeft = paddingLeft
            val targetTop = paddingTop + diff
            val targetRight = measuredWidth - paddingRight
            val targetBottom = measuredHeight - paddingBottom + diff
            it.layout(targetLeft, targetTop, targetRight, targetBottom)
        }
        mLoadView?.let {
            val footerLeft = paddingLeft
            val footerTop = measuredHeight - paddingBottom + +diff
            val footerRight = measuredWidth - paddingRight
            val footerBottom = footerTop + it.measuredHeight
            it.layout(footerLeft, footerTop, footerRight, footerBottom)
        }

    }


    fun setOnRefreshListener(refreshListener: OnRefreshListener) {
        this.refreshListener = refreshListener
    }

    fun setRefresh(refreshing: Boolean) {
        if (refreshing) {
            downRefresh()
        } else {
            when (refreshState) {
                RefreshState.DOWNPULL -> mRefreshCall?.endRefresh()
                RefreshState.UPPULL -> mLoadCall?.endRefresh()
                else -> {
                }
            }
            refreshState = RefreshState.DEFAULT
            layoutChildren()
        }
    }


    /**
     * {@inheritDoc}
     */
    override fun generateDefaultLayoutParams(): ViewGroup.LayoutParams {
        return ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    /**
     * {@inheritDoc}
     */
    override fun generateLayoutParams(p: ViewGroup.LayoutParams): ViewGroup.LayoutParams {
        return ViewGroup.MarginLayoutParams(p)
    }

    /**
     * {@inheritDoc}
     */
    override fun generateLayoutParams(attrs: AttributeSet): ViewGroup.LayoutParams {
        return ViewGroup.MarginLayoutParams(context, attrs)
    }

}