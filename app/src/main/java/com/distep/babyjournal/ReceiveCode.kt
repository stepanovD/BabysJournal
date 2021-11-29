package com.distep.babyjournal

sealed class ReceiveCode<out T : Any> {
    data class Success<out T : Any>(val data: T) : ReceiveCode<T>()
    data class Cancel(val cancel: Boolean = true) : ReceiveCode<Nothing>()
    data class Error(val message: String, val status: Int?) : ReceiveCode<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$message]"
            is Cancel -> "Cancel"
        }
    }
}