package com.yuan.refresh

/**
 * Created by shucheng.qu on 2018/6/27
 */
enum class RefreshState {
    DEFAULT, DOWNPULL, REFRESHING, UPPULL
}

enum class RefreshMode {
    TOP(0), BOTTOM(1), BOTH(2), NEVER(3);

    var mode = 0

    constructor(mode: Int) {
        this.mode = mode
    }


}