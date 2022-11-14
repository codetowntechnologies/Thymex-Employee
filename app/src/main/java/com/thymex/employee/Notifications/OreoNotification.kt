package com.thymex.employee.Notifications


import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.thymex.employee.R
import java.util.*


@Suppress("DEPRECATION")
class OreoNotification(base: Context?):ContextWrapper(base) {
    private var notificationManager:NotificationManager?=null

    companion object{
        private const val CHANNEL_ID="com.thymex.employee."
        private const val CHANNEL_NAME="officeservices"

    }
    init{
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            createChannel()
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun createChannel()
    {
        val channel=NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.enableLights(false)
        channel.enableVibration(true)
        channel.lockscreenVisibility= Notification.VISIBILITY_PRIVATE
        getManager!!.createNotificationChannel(channel)
    }
    val getManager:NotificationManager? get(){
        if(notificationManager==null){
            notificationManager=getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
        return notificationManager
    }



    @RequiresApi(Build.VERSION_CODES.O)
    fun  getOreoNotification(
        title: String?,
        body: String?,
        pendingIntent: PendingIntent?,
        soundUri: Uri?,
        icon: String?
    ):Notification.Builder{
        return Notification.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(body)
            .setSound(soundUri)
            .setSmallIcon(R.drawable.logo_map1)
            .setAutoCancel(true)
            .setStyle(Notification.BigTextStyle().bigText(body))




    }



}