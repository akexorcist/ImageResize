package com.akexorcist.imageresize.utils

import java.text.DecimalFormat

class NumberUtils {
    fun toDisplayNumber(value: Int): String {
        val format = DecimalFormat("#,###")
        return format.format(value)
    }
}