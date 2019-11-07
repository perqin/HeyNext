package com.perqin.heynext

import android.app.Notification
import android.content.Context
import android.content.SharedPreferences
import android.media.AudioManager
import android.os.Handler
import android.os.HandlerThread
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import android.view.KeyEvent
import androidx.preference.PreferenceManager

class BandNotificationListenerService : NotificationListenerService() {
    private lateinit var actionNoAction: String
    private lateinit var actionNext: String
    private lateinit var actionPrevious: String
    private lateinit var sp: SharedPreferences
    private lateinit var actionHandler: Handler

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "onCreate")
        actionNoAction = getString(R.string.spv_actionNoAction)
        actionNext = getString(R.string.spv_actionNext)
        actionPrevious = getString(R.string.spv_actionPrevious)
        sp = PreferenceManager.getDefaultSharedPreferences(this)
        val handlerThread = HandlerThread("mediaAction")
        handlerThread.start()
        actionHandler = Handler(handlerThread.looper)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy")
        (actionHandler.looper.thread as HandlerThread).quitSafely()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        runOnUiThread {
            if (sbn.packageName == PN_HEY_PLUS) {
                val notificationText = sbn.notification.extras.get(Notification.EXTRA_TEXT)
                Log.i(TAG, "onNotificationPosted: Received notification from HeyPlus: $notificationText")
                when (notificationText) {
                    TEXT_RUNNING -> getString(R.string.spk_runningAction)
                    TEXT_RIDING -> getString(R.string.spk_ridingAction)
                    else -> return@runOnUiThread
                }.let {
                    // Get action for this type of notification
                    sp.getString(it, actionNoAction)?:actionNoAction
                }.let {
                    performAction(it)
                }
            }
        }
    }

    private fun performAction(action: String) {
        Log.i(TAG, "performAction: $action")
        val keyCode = when(action) {
            actionNext -> KeyEvent.KEYCODE_MEDIA_NEXT
            actionPrevious -> KeyEvent.KEYCODE_MEDIA_PREVIOUS
            else -> return
        }
        (getSystemService(Context.AUDIO_SERVICE) as AudioManager).run {
            dispatchMediaKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, keyCode))
            dispatchMediaKeyEvent(KeyEvent(KeyEvent.ACTION_UP, keyCode))
        }
    }

    companion object {
        private const val TAG = "BandNotiService"
        private const val PN_HEY_PLUS = "com.ryeex.groot"
        private const val TEXT_RUNNING = "跑步定位中"
        private const val TEXT_RIDING = "骑行定位中"
    }
}