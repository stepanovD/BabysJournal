package com.distep.babyjournal

interface DialogCallbackListener<T: Any> {
    fun onDataReceived(code: ReceiveCode<T>)
}