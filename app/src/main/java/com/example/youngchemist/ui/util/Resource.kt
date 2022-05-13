package com.example.youngchemist.ui.util

sealed class Resource<T>(val message: String? = null) {
    class Loading<T>: Resource<T>()
    class Success<T>: Resource<T>()
    class Error<T>(message: String): Resource<T>(message)
}

sealed class TextInputResource<T>(val message: String? = null) {
    class InputInProcess<T> : TextInputResource<T>()
    class SuccessInput<T> : TextInputResource<T>()
    class ErrorInput<T>(message: String?) : TextInputResource<T>(message)
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

class UserState {
    companion object {
        const val LOGIN = 0
        const val REGISTER = 1
        const val LOGGED = 2
    }
}