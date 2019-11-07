package com.perqin.heynext

import android.os.Handler
import android.os.Looper

val uiHandler = Handler(Looper.getMainLooper())

inline fun runOnUiThread(crossinline block: () -> Unit) {
    if (Thread.currentThread().id == Looper.getMainLooper().thread.id) {
        block()
    } else {
        uiHandler.post {
            block()
        }
    }
}
