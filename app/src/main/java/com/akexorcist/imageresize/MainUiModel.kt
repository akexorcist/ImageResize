package com.akexorcist.imageresize

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MainUiModel(
    val event: MainEvent,
    val results: List<TestResult>,
    val totalImageResizeCount: Int
) : Parcelable