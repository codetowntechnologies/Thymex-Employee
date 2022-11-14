package com.thymex.employee


import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.location.*
import android.os.*
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*
import javax.net.ssl.*


@Suppress("DEPRECATION")
class locationservice:Service() {
    private var longitude: Double = 0.toDouble()
    private var latitude: Double = 0.toDouble()
private var address=""


    private var mypreviousLatLng:LatLng?=null

    override fun onBind(p0: Intent?): IBinder? {

        return null
    }



    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {


    if ((FirebaseAuth.getInstance().currentUser) != null) {
        onTaskRemoved(intent)
        val f = FirebaseAuth.getInstance().currentUser!!

        createNotificationChannel()
    val lm = getSystemService(LOCATION_SERVICE) as LocationManager


        val intent:Intent=Intent(this, MainActivity::class.java)
        val pending:PendingIntent= PendingIntent.getActivity(this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT)

           val  notification:Notification = NotificationCompat.Builder(this,
               "com.thymex.employee").setContentTitle("Office Services")
                 .setContentText("Employee App is Running On This Device ")
                 .setSmallIcon(
                     R.drawable.logo_map1)
                 .setContentIntent(pending).build()

             startForeground(101660, notification)

if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
      if( lm.isLocationEnabled &&  lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
try {
    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
        1000L,
        1.0f,
        object : LocationListener {
            override fun onLocationChanged(location: Location) {
                try {
                    val locations = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    longitude = locations!!.longitude
                    latitude = locations.latitude

                    val ref =
                        FirebaseDatabase.getInstance().reference.child("users").child(f.uid)

                    val userHashmap = HashMap<String, Any>()



                    userHashmap["latitudes"] = "$latitude"
                    userHashmap["longitudes"] = "$longitude"

                    ref.updateChildren(userHashmap).addOnCompleteListener { tasks ->
                        if (tasks.isSuccessful) {


                            if (mypreviousLatLng == null) {
                                MyAsyncTask().execute(latitude, longitude)
                                mypreviousLatLng = LatLng(latitude, longitude)
                            } else {

                              val  mypresentLatLng= LatLng(latitude,longitude)
                                val results = FloatArray(1)
                                Location.distanceBetween(mypreviousLatLng!!.latitude,
                                    mypreviousLatLng!!.longitude,
                                    mypresentLatLng!!.latitude,
                                    mypresentLatLng!!.longitude,
                                    results)
                                if(results[0]>10.00){
                                    MyAsyncTask().execute(latitude, longitude)
                                    mypreviousLatLng=mypresentLatLng
                                }

                            }


                        } else {

                        }
                    }


                } catch (e: Exception) {

                }


            }

        })


}catch (e: Exception){

}




       }
    

    }








        return START_STICKY


    }



return START_NOT_STICKY




    }






    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val notificationchannel: NotificationChannel = NotificationChannel("com.thymex.employee",
            "Foreground",
            NotificationManager.IMPORTANCE_LOW)
            notificationchannel.setSound(null, null)
        val manager: NotificationManager =getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(notificationchannel)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        val service: PendingIntent = PendingIntent.getService(getApplicationContext(), 1001, Intent(
            getApplicationContext(),
            locationservice::class.java), PendingIntent.FLAG_ONE_SHOT)

        val alarmManager: AlarmManager =  getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, service)


    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onDestroy() {
        super.onDestroy()
        if ((FirebaseAuth.getInstance().currentUser) != null) {
            startForegroundService(Intent(this, locationservice::class.java))
        }



        }








    inner class MyAsyncTask : AsyncTask<Double, Double, String>() {



        override fun onPreExecute() {
            super.onPreExecute()

        }



        override fun onProgressUpdate(vararg values: Double?) {
            super.onProgressUpdate(*values)
        }


        override fun doInBackground(vararg params: Double?): String? {


            try{

                val geocoder: Geocoder
                val addresses: List<Address>
                geocoder = Geocoder(this@locationservice, Locale.getDefault())

                addresses = geocoder.getFromLocation(params[0]!!,
                    params[1]!!,
                    1) // Here 1 represent max location result to returned, by documents it recommended 1 to 5


                address =
                    addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()


            }catch (e: Exception){
                address="Can't Find"


            }

















            try{

                if(latitude!=0.0 && longitude!=0.0) {
                    val f = FirebaseAuth.getInstance().currentUser!!
                    FirebaseDatabase.getInstance().reference.child("users").child(f!!.uid).addListenerForSingleValueEvent(
                        object : ValueEventListener {
                            @RequiresApi(Build.VERSION_CODES.O)
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val savedlocation = snapshot.child("savedloc").value.toString()
                                val getloc = address


                                val share = snapshot.child("share").value.toString()
                                if (share != "OFF" && getloc != "Can't Find" && getloc != "") {
                                    if (savedlocation != getloc) {
                                        FirebaseDatabase.getInstance().reference.child("users")
                                            .child(
                                                f!!.uid)
                                            .child("savedloc").setValue(getloc)
                                            .addOnCompleteListener {

                                                if (getloc != "Can't Find" && getloc != "") {


                                                    val date: String =
                                                        Calendar.getInstance().getTime().toString()

                                                    val userHashmap = HashMap<String, Any>()
                                                    userHashmap["historylatitudes"] = "$latitude"
                                                    userHashmap["historylongitudes"] = "$longitude"
                                                    userHashmap["placename"] = getloc
                                                    userHashmap["date"] = date

                                                    val sdf = SimpleDateFormat("yyyyMMddHHmmss")
                                                    val currentDate = sdf.format(Date()).toString()
                                                    userHashmap["tid"] = currentDate

                                                    FirebaseDatabase.getInstance().reference.child("historyofevery")
                                                        .child(
                                                            f!!.uid).child(currentDate)
                                                        .updateChildren(
                                                            userHashmap)


                                                }


                                            }


                                    }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {

                            }

                        })


                }

            }catch (e: Exception){


            }





































return "done"



        }
    }



















}