package com.chemist.youngchemist.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Answer(
    var text: String = "",
    var itIsRight: Boolean = false,
    var position: Int = -1
): Parcelable