package com.karan.clock.UI

import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.PowerManager
import android.os.SystemClock
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.view.ContentInfoCompat.Flags
import com.karan.clock.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

private var inAction = false
private var id = -1
private lateinit var noti : Notification

class AlarmBroadcastReceiver : BroadcastReceiver() {


    companion object {
        private const val CHANEL_ID = "alarm_notification"
        private const val CHANEL_NAME = "Alarm"
        private const val CHANEL_DESCRIPTION = "Shows Alarm notifications"
        private const val NOTIFICATION_ID = 46
    }

    override fun onReceive(context: Context?, intent: Intent?) {

        val db : AlarmDataBaseHelper by lazy {
            AlarmDataBaseHelper.getInstance()
        }

        val label = intent?.getStringExtra("Label")
        id = intent!!.getIntExtra("id",-1)

        val powerManager = context?.getSystemService(Context.POWER_SERVICE) as PowerManager
        powerManager.newWakeLock(
            PowerManager.SCREEN_DIM_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "ClockApp::WakeLock"
        ).acquire(5*60*1000)


        CoroutineScope(Dispatchers.Default).launch {
            val mp = MediaPlayer.create(context, Settings.System.DEFAULT_ALARM_ALERT_URI)
            try {
                inAction = true
                while (inAction) {
                    if (!mp.isPlaying) mp.start()
                    delay(1000)
                }
            } finally {
                mp.stop()
                mp.release()
            }
        }

        val notiManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(CHANEL_ID, CHANEL_NAME, NotificationManager.IMPORTANCE_HIGH).apply {
                description = CHANEL_DESCRIPTION
                notiManager.createNotificationChannel(this@apply)
            }
        }
        val actionIntent = PendingIntent.getBroadcast(
            context,
            0,
            Intent(context, ActionBroadCastReceive::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )



        noti = NotificationCompat.Builder(context , CHANEL_ID).apply {
            setSmallIcon(R.drawable.icon_alarm)
            setContentTitle("ALARM")
            setContentText(label)
            setPriority(NotificationCompat.PRIORITY_HIGH)
            setAutoCancel(false)
            setOngoing(true)
            addAction(
                R.drawable.icon_pause,
                "Stop Alarm",
                actionIntent
            )
        }.build()

        notiManager.notify(NOTIFICATION_ID,noti)
        db.switchAlarm(id,0)
    }

    class ActionBroadCastReceive : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            // on receive
            val nm = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.cancel(NOTIFICATION_ID)
            inAction = false
        }
    }
}