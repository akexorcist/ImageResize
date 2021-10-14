package com.akexorcist.imageresize

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class ResizeType : Parcelable {
    @Parcelize
    object Fill : ResizeType()

    @Parcelize
    object Crop : ResizeType()
}
