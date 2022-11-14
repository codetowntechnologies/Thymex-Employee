package com.thymex.employee


import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.location.*
import android.net.Uri
import android.os.*
import android.os.StrictMode.ThreadPolicy
import android.provider.Settings
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId
import com.luseen.spacenavigation.SpaceItem
import com.luseen.spacenavigation.SpaceNavigationView
import com.luseen.spacenavigation.SpaceOnClickListener
import com.thymex.employee.Notifications.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.anotherphoneinfo.view.*
import kotlinx.android.synthetic.main.informations.view.*
import kotlinx.android.synthetic.main.my_address.*
import kotlinx.android.synthetic.main.my_address.view.*
import kotlinx.android.synthetic.main.passwordch.*
import kotlinx.android.synthetic.main.passwordch.view.*
import kotlinx.android.synthetic.main.thisdeviceinfo.*
import kotlinx.android.synthetic.main.thisdeviceinfo.view.*
import mumayank.com.airlocationlibrary.AirLocation
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.system.exitProcess


@Suppress("DEPRECATION")
open class MainActivity : AppCompatActivity(), OnMapReadyCallback, View.OnClickListener,GoogleMap.OnMarkerClickListener {
    private var lat2: Double? = 0.toDouble()
    private var lng2:Double=0.toDouble()
    private lateinit var list: ArrayList<JSONObject>
    private lateinit var markerall: ArrayList<Marker>
    private lateinit var userslist: ArrayList<users>
    private var lat1: Double = 0.toDouble()
    private var lng1: Double = 0.toDouble()
private var trackmarker1:Marker?=null
    private lateinit var ll: LatLng
    private var myaddressnow = ""
    private var address=""

private val adminuid="PC0L14rTwrVmbU7yi9Ho8gbKfSy2"

    private var home_marker: Marker? = null


    private var gmap: GoogleMap? = null
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var airloc: AirLocation
private  var phoneofmine=""
    private var mcity: String = ""
    private var mstate: String = ""
    private var mcountry: String = ""
    private var mpostalCode: String = ""

    private var apiService: APIService? = null

    private var firebaseUser: FirebaseUser?=null
private lateinit var toolbar:androidx.appcompat.widget.Toolbar
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        setContentView(R.layout.activity_main)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        toolbar=findViewById(R.id.appbar)
        setSupportActionBar(toolbar)
        apiService = Client.Client.getClient("https://fcm.googleapis.com/")!!.create(APIService::class.java)
        mapFragment = supportFragmentManager.findFragmentById(R.id.fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fab.setOnClickListener(this)
        userslist=ArrayList<users>()
        markerall = ArrayList<Marker>()
        faball.setOnClickListener(this)

try{
    startForegroundService(Intent(this@MainActivity,
        locationservice::class.java))
}catch (e: Exception){

}
        list = ArrayList<JSONObject>()
        askforlocations()
        val spaceNavigationView:SpaceNavigationView = findViewById(R.id.bottom_navigation_view)
        spaceNavigationView.initWithSaveInstanceState(savedInstanceState)
        spaceNavigationView.addSpaceItem(SpaceItem("Employee Status", R.drawable.iii))
        spaceNavigationView.addSpaceItem(SpaceItem("info", R.drawable.ins))
        spaceNavigationView.isSelected=false


        firebaseUser= FirebaseAuth.getInstance().currentUser




        checkBattery()



        FirebaseDatabase.getInstance().reference.child("users").child(FirebaseAuth.getInstance().currentUser!!.uid)
            .addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        phoneofmine=snapshot.child("phone").value.toString()
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })



        try{
    val refreshedToken = FirebaseInstanceId.getInstance().getToken().toString()
    updateToken(refreshedToken)
}catch (e: java.lang.Exception){

}
        spaceNavigationView.setSpaceOnClickListener(object : SpaceOnClickListener {
            override fun onCentreButtonClick() {
                try {
                    askforlocations()
                } catch (e: Exception) {

                }
                if (gmap != null) {


                    airloc =
                        AirLocation(this@MainActivity, true, true, object : AirLocation.Callbacks {
                            @SuppressLint("SetTextI18n")
                            override fun onFailed(locationFailedEnum: AirLocation.LocationFailedEnum) {
                                Toast.makeText(this@MainActivity,
                                    "Failed to get the current position",
                                    Toast.LENGTH_SHORT).show()

                            }

                            @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
                            override fun onSuccess(location: Location) {
                                try {


                                    if (home_marker != null) {
                                        home_marker!!.remove()
                                    }
                                    ll = LatLng(lat1, lng1)
                                    val circleDrawable: Drawable =
                                        getResources().getDrawable(R.drawable.smartphone);
                                    val markerIcon: BitmapDescriptor? = getMarkerIconFromDrawable(
                                        circleDrawable)
                                    myaddress()
                                    home_marker = gmap!!.addMarker(MarkerOptions().position(ll)
                                        .title("This Device : $myaddressnow").icon(markerIcon)
                                    )
                                    home_marker!!.showInfoWindow()
                                    gmap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(ll,
                                        14.0f))

                                } catch (e: Exception) {
                                    Toast.makeText(this@MainActivity,
                                        "Failed to get the device's position",
                                        Toast.LENGTH_SHORT).show()

                                }
                                spaceNavigationView.isSelected = false

                            }


                        })

                }


            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onItemClick(itemIndex: Int, itemName: String) {
                when (itemIndex) {
                    0 -> {
                        FirebaseDatabase.getInstance().reference.child("users")
                            .child(firebaseUser!!.uid)
                            .addListenerForSingleValueEvent(
                                object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {

                                        FirebaseDatabase.getInstance().reference.child("users")
                                            .child(firebaseUser!!.uid)
                                            .addListenerForSingleValueEvent(
                                                object : ValueEventListener {
                                                    override fun onDataChange(snapshot: DataSnapshot) {


                                                        try {
                                                            val bottoms = BottomSheetDialog(
                                                                this@MainActivity)

                                                            val inflater =
                                                                LayoutInflater.from(this@MainActivity)
                                                            var view2: View =
                                                                inflater.inflate(R.layout.thisdeviceinfo,
                                                                    null)


                                                            FirebaseDatabase.getInstance().reference.child(
                                                                "users")
                                                                .child(
                                                                    firebaseUser!!.uid)
                                                                .addListenerForSingleValueEvent(
                                                                    object :
                                                                        ValueEventListener {
                                                                        override fun onDataChange(
                                                                            snapshot: DataSnapshot
                                                                        ) {
                                                                            val check =
                                                                                snapshot.child(
                                                                                    "share").value


                                                                            if (check!!.equals(
                                                                                    "OFF")
                                                                            ) {
                                                                                view2.switchtrack.setChecked(
                                                                                    false)

                                                                            } else {
                                                                                view2.switchtrack.isChecked =
                                                                                    true
                                                                            }

                                                                        }

                                                                        override fun onCancelled(
                                                                            error: DatabaseError
                                                                        ) {

                                                                        }

                                                                    })


                                                            view2.switchtrack.setOnCheckedChangeListener(
                                                                CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
                                                                    if (isChecked) {
                                                                        Toast.makeText(this@MainActivity,
                                                                            "Live Sharing ON",
                                                                            Toast.LENGTH_LONG)
                                                                            .show()
                                                                        FirebaseDatabase.getInstance().reference.child(
                                                                            "users").child(
                                                                            firebaseUser!!.uid)
                                                                            .child(
                                                                                "share")
                                                                            .setValue("ON")


                                                                        val f =
                                                                            FirebaseAuth.getInstance().currentUser!!
                                                                        try {
                                                                            val ref =
                                                                                FirebaseDatabase.getInstance().reference.child(
                                                                                    "users")
                                                                                    .child(f.uid)

                                                                            val userHashmap =
                                                                                HashMap<String, Any>()



                                                                            userHashmap["latitudes"] =
                                                                                "$lat1"
                                                                            userHashmap["longitudes"] =
                                                                                "$lng1"

                                                                            ref.updateChildren(
                                                                                userHashmap)
                                                                                .addOnCompleteListener { tasks ->
                                                                                    if (tasks.isSuccessful) {

                                                                                        MyAsyncTask1().execute(
                                                                                            lat1,
                                                                                            lng1)


                                                                                        sendNotification(
                                                                                            adminuid,
                                                                                            FirebaseAuth.getInstance().uid,
                                                                                            "ONLINE")
                                                                                    } else {

                                                                                    }
                                                                                }


                                                                        } catch (e: Exception) {

                                                                        }


                                                                    } else {
                                                                        Toast.makeText(this@MainActivity,
                                                                            "Live Sharing Stop",
                                                                            Toast.LENGTH_LONG)
                                                                            .show()
                                                                        FirebaseDatabase.getInstance().reference.child(
                                                                            "users").child(
                                                                            firebaseUser!!.uid)
                                                                            .child(
                                                                                "share")
                                                                            .setValue("OFF")

                                                                        sendNotification(
                                                                            adminuid,
                                                                            FirebaseAuth.getInstance().uid,
                                                                            "OFFLINE")


                                                                    }
                                                                })


                                                            bottoms.setContentView(view2)

                                                            bottoms.show()
                                                        } catch (e: java.lang.Exception) {
                                                            Toast.makeText(this@MainActivity,
                                                                "Can't Show Device Info",
                                                                Toast.LENGTH_SHORT).show()
                                                        }

                                                    }

                                                    override fun onCancelled(error: DatabaseError) {

                                                    }
                                                })


                                    }

                                    override fun onCancelled(error: DatabaseError) {

                                    }
                                })


                    }
                    1 -> {

                        val bottomsheet: BottomSheetDialog = BottomSheetDialog(this@MainActivity)
                        val view: View = layoutInflater.inflate(R.layout.anotherphoneinfo, null)

                        bottomsheet.setContentView(view)
                        bottomsheet.show()

                    }

                }
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onItemReselected(itemIndex: Int, itemName: String) {
                when (itemIndex) {
                    0 -> {
                        FirebaseDatabase.getInstance().reference.child("users")
                            .child(firebaseUser!!.uid)
                            .addListenerForSingleValueEvent(
                                object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {

                                        FirebaseDatabase.getInstance().reference.child("users")
                                            .child(firebaseUser!!.uid)
                                            .addListenerForSingleValueEvent(
                                                object : ValueEventListener {
                                                    override fun onDataChange(snapshot: DataSnapshot) {


                                                        try {
                                                            val bottoms =
                                                                BottomSheetDialog(this@MainActivity)

                                                            val inflater =
                                                                LayoutInflater.from(this@MainActivity)
                                                            var view2: View =
                                                                inflater.inflate(R.layout.thisdeviceinfo,
                                                                    null)


                                                            FirebaseDatabase.getInstance().reference.child(
                                                                "users")
                                                                .child(
                                                                    firebaseUser!!.uid)
                                                                .addListenerForSingleValueEvent(
                                                                    object :
                                                                        ValueEventListener {
                                                                        override fun onDataChange(
                                                                            snapshot: DataSnapshot
                                                                        ) {
                                                                            val check =
                                                                                snapshot.child("share").value


                                                                            if (check!!.equals("OFF")) {
                                                                                view2.switchtrack.setChecked(
                                                                                    false)

                                                                            } else {
                                                                                view2.switchtrack.isChecked =
                                                                                    true
                                                                            }

                                                                        }

                                                                        override fun onCancelled(
                                                                            error: DatabaseError
                                                                        ) {

                                                                        }

                                                                    })


                                                            view2.switchtrack.setOnCheckedChangeListener(
                                                                CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
                                                                    if (isChecked) {
                                                                        Toast.makeText(this@MainActivity,
                                                                            "Live Sharing ON",
                                                                            Toast.LENGTH_LONG)
                                                                            .show()
                                                                        FirebaseDatabase.getInstance().reference.child(
                                                                            "users").child(
                                                                            firebaseUser!!.uid)
                                                                            .child(
                                                                                "share")
                                                                            .setValue("ON")


                                                                        val f =
                                                                            FirebaseAuth.getInstance().currentUser!!
                                                                        try {
                                                                            val ref =
                                                                                FirebaseDatabase.getInstance().reference.child(
                                                                                    "users")
                                                                                    .child(f.uid)

                                                                            val userHashmap =
                                                                                HashMap<String, Any>()



                                                                            userHashmap["latitudes"] =
                                                                                "$lat1"
                                                                            userHashmap["longitudes"] =
                                                                                "$lng1"

                                                                            ref.updateChildren(
                                                                                userHashmap)
                                                                                .addOnCompleteListener { tasks ->
                                                                                    if (tasks.isSuccessful) {

                                                                                        MyAsyncTask1().execute(
                                                                                            lat1,
                                                                                            lng1)
                                                                                        sendNotification(
                                                                                            adminuid,
                                                                                            FirebaseAuth.getInstance().uid,
                                                                                            "ONLINE")
                                                                                    } else {

                                                                                    }
                                                                                }

                                                                        } catch (e: Exception) {

                                                                        }


                                                                    } else {
                                                                        Toast.makeText(this@MainActivity,
                                                                            "Live Sharing Stop",
                                                                            Toast.LENGTH_LONG)
                                                                            .show()
                                                                        FirebaseDatabase.getInstance().reference.child(
                                                                            "users").child(
                                                                            firebaseUser!!.uid)
                                                                            .child(
                                                                                "share")
                                                                            .setValue("OFF")

                                                                        sendNotification(
                                                                            adminuid,
                                                                            FirebaseAuth.getInstance().uid,
                                                                            "OFFLINE")


                                                                    }
                                                                })


                                                            bottoms.setContentView(view2)

                                                            bottoms.show()
                                                        } catch (e: java.lang.Exception) {
                                                            Toast.makeText(this@MainActivity,
                                                                "Can't Show Device Info",
                                                                Toast.LENGTH_SHORT).show()
                                                        }

                                                    }

                                                    override fun onCancelled(error: DatabaseError) {

                                                    }
                                                })


                                    }

                                    override fun onCancelled(error: DatabaseError) {

                                    }
                                })


                    }
                    1 -> {


                        val bottomsheet: BottomSheetDialog = BottomSheetDialog(this@MainActivity)
                        val view: View = layoutInflater.inflate(R.layout.anotherphoneinfo, null)

                        bottomsheet.setContentView(view)
                        bottomsheet.show()

                    }

                }
            }
        })

        getSupportActionBar()!!.title=""







    }

    private fun updateToken(tk: String?){
        try {
            val ref = FirebaseDatabase.getInstance().reference.child("Tokens")
            val token1 = Token(tk)
            ref.child(firebaseUser!!.uid).setValue(token1)
        }catch (e: java.lang.Exception){

        }
    }




    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.map_options, menu)

        return true
    }


    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        // Change the map type based on the user's selection.
        R.id.normal_map -> {
            gmap!!.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            gmap!!.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            gmap!!.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            gmap!!.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        R.id.logout -> {

            FirebaseDatabase.getInstance().goOnline();
            FirebaseAuth.getInstance().signOut()
            if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT) {
                (applicationContext.getSystemService(ACTIVITY_SERVICE) as ActivityManager)
                    .clearApplicationUserData() // note: it has a return value!
            } else {
                // use old hacky way, which can be removed
                // once minSdkVersion goes above 19 in a few years.
            }
            startActivity(Intent(this, LoginActivity::class.java))
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            finishAffinity()

            exitProcess(-1)

            true

        }
        R.id.psschange -> {

            val changebottom = BottomSheetDialog(this)
            val v: View = layoutInflater.inflate(R.layout.passwordch, null)








            v.chjangepassword.setOnClickListener {

                val fbf: FirebaseUser = FirebaseAuth.getInstance().currentUser!!
                val oldpass = v.oldpass.text.toString()
                val newpass = v.newpass.text.toString()

                val credential: AuthCredential = EmailAuthProvider
                    .getCredential("${phoneofmine}@office.com", "$oldpass");





                fbf.reauthenticate(credential).addOnCompleteListener {
                    if (it.isSuccessful) {
                        fbf.updatePassword(newpass).addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(this, "Password Updated", Toast.LENGTH_LONG).show()

                            } else {
                                Toast.makeText(this,
                                    "Can not update new password",
                                    Toast.LENGTH_LONG).show()

                            }
                        }

                    } else {
                        Toast.makeText(this, "Old Password not correct", Toast.LENGTH_LONG).show()
                    }
                }


            }








            changebottom.setContentView(v)
            changebottom.show()






            true

        }

        else -> super.onOptionsItemSelected(item)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        airloc.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        airloc.onRequestPermissionsResult(requestCode, permissions, grantResults)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    override fun onMapReady(map: GoogleMap?) {
        gmap = map

        try{
            askforlocations()
        }catch (e: Exception){

        }

        gmap!!.setOnMarkerClickListener(this)
        if (gmap != null) {
            airloc = AirLocation(this, true, true, object : AirLocation.Callbacks {
                @SuppressLint("SetTextI18n")
                override fun onFailed(locationFailedEnum: AirLocation.LocationFailedEnum) {
                    Toast.makeText(this@MainActivity,
                        "Failed to get the current position",
                        Toast.LENGTH_SHORT).show()

                }

                @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
                override fun onSuccess(location: Location) {
                    try {
                        lat1 = location.latitude
                        lng1 = location.longitude

                        ll = LatLng(lat1, lng1)

                        val circleDrawable: Drawable =
                            getResources().getDrawable(R.drawable.smartphone);
                        val markerIcon: BitmapDescriptor? =
                            getMarkerIconFromDrawable(circleDrawable)
                        myaddress()

                        home_marker?.setIcon(null)
                        home_marker = gmap!!.addMarker(MarkerOptions().position(ll)
                            .title("This Device : $myaddressnow").icon(markerIcon)
                        )
                        home_marker!!.showInfoWindow()

                        gmap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(ll, 15.0f))

                    } catch (e: Exception) {
                        Toast.makeText(this@MainActivity,
                            "Failed to get the current position",
                            Toast.LENGTH_SHORT).show()

                    }

                }


            })

        }

    }


    private  fun getMarkerIconFromDrawable(drawable: Drawable): BitmapDescriptor? {
        val canvas = Canvas()
        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888)
        canvas.setBitmap(bitmap)
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        drawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
fun mydetails(){
    try{
        val bottom = BottomSheetDialog(this)
        bottom.setTitle("MY Information")
        val inflater = LayoutInflater.from(this)
        var view1: View = inflater.inflate(R.layout.my_address, null)
        view1.city_value.setText(mcity)
        view1.myaddress.setText("My Location : " + myaddressnow)
        view1.state_value.setText(mstate)
        view1.country_value.setText(mcountry)
        view1.postal_value.setText(mpostalCode)
        bottom.setContentView(view1)

        bottom.show()
        }
    catch (e: Exception){
        Toast.makeText(this, "Can't access...try after some time", Toast.LENGTH_SHORT).show()
    }
}

    @SuppressLint("UseCompatLoadingForDrawables")
    @Suppress("DEPRECATION")
    override fun onClick(v: View?) {


        when (v!!.id) {


            R.id.fab -> {
                try {
                    askforlocations()
                } catch (e: Exception) {

                }
                if (gmap != null) {
                    airloc = AirLocation(this, true, true, object : AirLocation.Callbacks {
                        @SuppressLint("SetTextI18n")
                        override fun onFailed(locationFailedEnum: AirLocation.LocationFailedEnum) {
                            Toast.makeText(
                                this@MainActivity,
                                "Failed to get the current position",
                                Toast.LENGTH_SHORT
                            ).show()

                        }

                        @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
                        override fun onSuccess(location: Location) {
                            try {

                                ll = LatLng(lat1, lng1)
                                myaddress()


                                if (home_marker != null) {
                                    home_marker!!.remove()
                                }

                                val circleDrawable: Drawable =
                                    getResources().getDrawable(R.drawable.smartphone);
                                val markerIcon: BitmapDescriptor? = getMarkerIconFromDrawable(
                                    circleDrawable)

                                home_marker = gmap!!.addMarker(MarkerOptions().position(ll)
                                    .title("This Device : $myaddressnow").icon(markerIcon)
                                )
                                home_marker!!.showInfoWindow()
                                gmap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(ll, 14.5f))
                                mydetails()

                            } catch (e: Exception) {
                                Toast.makeText(this@MainActivity,
                                    "Can not get Details",
                                    Toast.LENGTH_LONG).show()
                            }
                        }

                    })


                }


            }

            R.id.faball -> {
                val fid = firebaseUser!!.uid
                if (gmap != null) {
                    gmap!!.clear()
                }
                try {
                    askforlocations()
                } catch (e: Exception) {

                }
                val circleDrawable: Drawable =
                    getResources().getDrawable(R.drawable.smartphone);
                val markerIcon: BitmapDescriptor? = getMarkerIconFromDrawable(
                    circleDrawable)
                ll = LatLng(lat1, lng1)
                home_marker = gmap!!.addMarker(MarkerOptions().position(ll)
                    .title("My Position : $myaddressnow").icon(markerIcon)
                )
                userslist.clear()
                markerall.clear()
                FirebaseDatabase.getInstance().reference.child("users")
                    .addListenerForSingleValueEvent(
                        object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for (i in snapshot.children) {
                                    val allusers: users? = i.getValue(users::class.java)


                                    val latitude1 = allusers!!.getlatitudes()
                                    val longitudes1 = allusers!!.getlongitudes()
                                    val share = allusers!!.getshare()

                                    val uidf = allusers.getuid()
                                    if (uidf != fid && share != "OFF") {
                                        val llll =
                                            LatLng(latitude1!!.toDouble(), longitudes1!!.toDouble())
                                        val phonenum = allusers.getphone()
                                        val name = allusers.getname()

                                        val circleDrawable1: Drawable =
                                            getResources().getDrawable(R.drawable.autro);
                                        val markerIcon1: BitmapDescriptor? =
                                            getMarkerIconFromDrawable(
                                                circleDrawable1)
                                        val marker = gmap!!.addMarker(
                                            MarkerOptions().position(llll).title(phonenum)
                                                .icon(markerIcon1)
                                                .snippet("Employee :" + name)

                                        )
                                        markerall.add(marker)


                                        userslist.add(allusers!!)


                                    }
                                }

                                for (i in 0..(markerall.size - 1)) {
                                    markerall[i].showInfoWindow()
                                }


                            }

                            override fun onCancelled(error: DatabaseError) {

                            }
                        })


            }


        }
    }













    // My location
   private fun myaddress() {
        val geocoder: Geocoder
        val addresses: List<Address>

            geocoder = Geocoder(this, Locale.getDefault())

            addresses = geocoder.getFromLocation(lat1,
                lng1,
                1) // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        try {
            val address: String =
                addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

            myaddressnow = address
            mcity = addresses[0].getLocality().toString()
            mstate = addresses[0].getAdminArea().toString()
            mcountry = addresses[0].getCountryName().toString()
            mpostalCode = addresses[0].getPostalCode().toString()
        }catch (e: Exception){

        }

    }



//end


 //Address for draw routes
  private  fun findaddress(lat: Double, lng: Double):String{
        val geocoder: Geocoder
        val addresses: List<Address>
       try {
           geocoder = Geocoder(this, Locale.getDefault())

           addresses = geocoder.getFromLocation(lat,
               lng,
               1) // Here 1 represent max location result to returned, by documents it recommended 1 to 5


           val address: String =
               addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
           return address
       }catch (e: Exception){
           Toast.makeText(this@MainActivity, "Can not find Details", Toast.LENGTH_LONG).show()
       }

return "None"
    }

    //End








    override fun onMarkerClick(markername: Marker?): Boolean {
        if (home_marker!!.equals(markername)) {
            mydetails()
        } else if (userslist.isEmpty()) {
            Toast.makeText(this, "No Employee on the map", Toast.LENGTH_SHORT).show()

            return false } else {
            try { for (i in 0..(userslist.size - 1)) {
                var phonenumber=""
                var share=""

                if (markerall[i].equals(markername)) {
                    val xx = i
                    val bottomsheet = BottomSheetDialog(this)
                    bottomsheet.setTitle("Employee Information")
                    val inflater = LayoutInflater.from(this)
                    val view: View = inflater.inflate(R.layout.informations, null)
                    phonenumber = userslist[xx].getphone().toString()
                    val latttt=userslist[xx].getlatitudes()!!.toDouble()
                    val longggg=userslist[xx].getlongitudes()!!.toDouble()

                    val name=userslist[xx].getname().toString()
                   share= userslist[xx].getshare().toString()
                    if(share=="OFF"){
                        view.availableop.setText("Employee: $name is not on Work")
                    }else{
                        view.availableop.setText("Employee: $name is on Working")
                    }
                    view.phnty.setText("$phonenumber")
                    val pp=findaddress(latttt, longggg)
                    view.placename11.setText(pp)
                   view.calluser.setOnClickListener {
                       val intent = Intent(Intent.ACTION_DIAL)
                       intent.data = Uri.parse("tel:$phonenumber")
                       startActivity(intent)
                   }
                    bottomsheet.setContentView(view)
                    bottomsheet.show()
                }




            }


            }catch (e: Exception){

                Toast.makeText(this, "Can't get details...try again/Refresh", Toast.LENGTH_SHORT).show()
            }            }
        return true
    }


    private fun askforlocations(){
        val lm = getSystemService(LOCATION_SERVICE) as LocationManager


        @SuppressLint("MissingPermission")
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            if (lm.isLocationEnabled && lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                try {

                    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        0L,
                        0.0f,
                        object : LocationListener {

                            override fun onLocationChanged(location: Location) {
                                try {
                                    val locations =
                                        lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                                    if (locations != null) {
                                        lng1 = locations!!.longitude
                                        lat1 = locations.latitude
                                    }


                                } catch (e: Exception) {

                                }


                            }

                        })


                } catch (e: Exception) {

                }


            }


        }

    }

    override fun onResume() {
        super.onResume()
        askforlocations()
        checkBattery()
    }

    override fun onStart() {
        super.onStart()
        askforlocations()
        checkBattery()
    }







    private fun sendNotification(receiver: String, username: String?, status: String) {
        var email = ""
        var body=""
        var name=""


        val reff = FirebaseDatabase.getInstance().reference.child("Tokens")
        val query = reff.orderByKey().equalTo(receiver)
        val firebaseuser = FirebaseAuth.getInstance().uid
        FirebaseDatabase.getInstance().reference.child("users").child(firebaseuser!!).addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    email = snapshot.child("phone").getValue(String::class.java).toString()
                    name = snapshot.child("name").getValue(String::class.java).toString()
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

        if(status=="ONLINE"){
            body="Sharing his/her live location (Available Status On) from his/her app. Now You can see his/her every Live Movement on the app all the time and you will get every location history of his/her movement in your admin app"
        }else{
            body="stopped his/her live location Sharing (Available Status OFF) from his/her app. Now You can not see his/her Live Movement and Now you will not get any new updated history of his/her movement as he/she stopped location sharing"
        }


        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (i in snapshot.children) {
                    val token: Token? = i.getValue(Token::class.java)
                    val data = Data(firebaseuser!!,
                        R.drawable.logo_map1,
                        "$email : $body",
                        "Notification From Employee $name : $email",
                        receiver)

                    val sender = Sender(data, token!!.getToken().toString())
                    apiService!!.sendNotification(sender)
                        .enqueue(object : Callback<MyResponse> {
                            override fun onResponse(
                                call: Call<MyResponse>,
                                response: retrofit2.Response<MyResponse>,
                            ) {
                                if (response.code() == 200) {
                                    if (response.body()!!.success != 1) {
                                        Toast.makeText(this@MainActivity,
                                            "Failed to Sent Notification to the Admin. Admin is not login on his app or Network Problem",
                                            Toast.LENGTH_LONG).show()
                                    } else {
                                        Toast.makeText(this@MainActivity,
                                            "Admin will be notified about your current available status",
                                            Toast.LENGTH_LONG).show()

                                    }
                                }

                            }

                            override fun onFailure(call: Call<MyResponse>, t: Throwable) {

                            }


                        })
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }






    @SuppressLint("StaticFieldLeak")
    inner class MyAsyncTask1 : AsyncTask<Double, Double, String>() {



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
                geocoder = Geocoder(this@MainActivity, Locale.getDefault())

                addresses = geocoder.getFromLocation(params[0]!!,
                    params[1]!!,
                    1) // Here 1 represent max location result to returned, by documents it recommended 1 to 5


                address =
                    addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()


            }catch (e: Exception){
                address="Can't Find"


            }

















            try{

                if(lat1!=0.0 && lng1!=0.0) {
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
                                                    userHashmap["historylatitudes"] = "$lat1"
                                                    userHashmap["historylongitudes"] = "$lng1"
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





  private  fun isIgnoringBatteryOptimizations(context: Context): Boolean {
        val pwrm = context.applicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager
        val name = context.applicationContext.packageName
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return pwrm.isIgnoringBatteryOptimizations(name)
        }
        return true
    }




    private fun checkBattery() {
        if (!isIgnoringBatteryOptimizations(applicationContext) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // val name = "OFFICE SERVICES:  Employee App"
            //  Toast.makeText(applicationContext, "Battery optimization -> All apps -> $name -> Don't optimize", Toast.LENGTH_LONG).show()
            val builderr=AlertDialog.Builder(this)
            builderr.setIcon(R.drawable.logo_map1)
            builderr.setTitle("Attention Please! Read Must")
            builderr.setMessage("Compulsory: Please turn OFF Battery Optimization for this app. Its Compulsory For Background Feature.\n\n\nAdditional:(Just read these once)\n1.If You are using Xiaomi Redmi model Phone, then off the MIUI Optimization for this app manually from app settings.\n2.Don't use the app in ultra low power mode or Data Saver Mode")
            builderr.setPositiveButton("Compulsory: Turn Off Battery Optimization from here",
                DialogInterface.OnClickListener { dialogInterface, i ->

                    val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                    startActivity(intent)
                    dialogInterface.dismiss()
                    finish()

                })

            val alert= builderr.create()
            alert.show()
            alert.setCanceledOnTouchOutside(false)
            alert.setCancelable(false)

        }
    }





}








