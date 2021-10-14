package com.akexorcist.imageresize.resize

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class ResizeType : Parcelable {
    @Parcelize
    object Fill : ResizeType()

    @Parcelize
    object Crop : ResizeType()
}
