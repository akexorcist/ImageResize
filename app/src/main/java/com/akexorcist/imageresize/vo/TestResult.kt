package com.akexorcist.imageresize.vo

import android.os.Parcelable
import com.akexorcist.imageresize.resize.ResizeType
import kotlinx.parcelize.Parcelize

@Parcelize
data class TestResult(
    val preferredSize: Int,
    val actualWidth: Int,
    val actualHeight: Int,
    val executionTime: Long,
    val status: ResizeStatus,
    val ratio: ImageRatio,
    val resizeType: ResizeType
) : Parcelable
