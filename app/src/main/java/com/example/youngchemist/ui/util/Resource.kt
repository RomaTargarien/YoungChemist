package com.example.youngchemist.ui.util

import android.os.Parcel
import android.os.Parcelable

sealed class Resource<T>(val message: String? = null) {
    class Loading<T>: Resource<T>()
    class Success<T>: Resource<T>()
    class Error<T>(message: String): Resource<T>(message)
}

sealed class ResourceNetwork<T>(val data: T? = null,val message: String? = null) {
    class Loading<T> : ResourceNetwork<T>()
    class Success<T>(data: T?) : ResourceNetwork<T>(data)
    class Error<T>(message: String?): ResourceNetwork<T>(null,message)
}

sealed class FragmentAnimationBehavior {
    class Enter : FragmentAnimationBehavior()
    class Forward : FragmentAnimationBehavior()
    class Back : FragmentAnimationBehavior()
}

sealed class TestExitBehavior {
    class ExitSavingProgress : TestExitBehavior()
    class ExitNoSavingProgress : TestExitBehavior()
}
class UserState {
    companion object {
        const val LOGIN = 0
        const val REGISTER = 1
    }
}