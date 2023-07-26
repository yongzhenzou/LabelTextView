package com.example.textdemo

import android.content.res.Resources
import android.util.TypedValue

/**
 * @author yongzhen_zou@163.com
 * @date 2023/7/26 11:19
 */
val Int.dp: Float
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), Resources.getSystem().displayMetrics
    )