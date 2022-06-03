package com.yujun.trucksharing.views

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.OnSuccessListener
import com.google.gson.Gson
import com.yujun.trucksharing.R
import com.yujun.trucksharing.model.MapData
import com.yujun.trucksharing.prefmanager.SharedPrefManager
import okhttp3.OkHttpClient
import okhttp3.Request


class LocationFragment : Fragment() {

    companion object {
        fun newInstance() = LocationFragment()
    }

    var pref: SharedPrefManager? = null

    private var dropLocation: String? = ""
    private var placeLocation: String? = ""

    private var mMap: GoogleMap? = null

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    val MY_PERMISSIONS_REQUEST_LOCATION = 99

    private var originLatitude: Double? = null
    private var originLongitude: Double? = null

    private var destinationLatitude: Double? = null
    private var destinationLongitude: Double? = null

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */

        mMap = googleMap

        placeLocation = arguments?.getString("dropLocation")

        val parts: List<String> = placeLocation!!.split("lat/lng:")

        val title = parts[0]

        val c = parts[1].split(",")

        val latDouble = c[0].replace(" (", "").toDouble()
        val lonDouble = c[1].replace(")", "").toDouble()

        Log.d("currentLocationPin", "$latDouble, $lonDouble" )
        val coordinatesStr = LatLng(latDouble, lonDouble)
        mMap!!.addMarker(MarkerOptions()
            .position(coordinatesStr)
            .title(title)
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)))
        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(coordinatesStr))


        // Zoom
        mMap!!.uiSettings.isZoomControlsEnabled = true

        // Focus
        addCameraToMap(LatLng(latDouble, lonDouble))
    }

    private fun addCameraToMap(latLng: LatLng) {
        val cameraPosition = CameraPosition.Builder()
            .target(latLng)
            .zoom(5f)
            .build()
        mMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_location, container, false)

        pref = SharedPrefManager(this.requireContext())

        dropLocation = arguments?.getString("dropLocation")

        val locationDropOff = rootView.findViewById<TextView>(R.id.locationDropOff)
        locationDropOff.text = dropLocation

        val locationPickUp = rootView.findViewById<TextView>(R.id.locationPickUp)
        locationPickUp.text = "My Location"

        getLocationPermission()

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this.requireActivity())

        if (ActivityCompat.checkSelfPermission(
                this.requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this.requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSIONS_REQUEST_LOCATION
            )
        }

        val task = fusedLocationProviderClient.lastLocation
        task.addOnSuccessListener(context as Activity,
                OnSuccessListener { location ->
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        // Logic to handle location object
                        Log.d("currentLocation", "${location.latitude}, ${location.longitude}" )
                        // currentLocation: -1.264455, 36.804117
                        mMap!!.addMarker(
                            MarkerOptions().position(
                                LatLng(location.latitude, location.longitude)
                            ).title("Your Location")
                        )

                        /**
                         * Draw driver route
                         */
                        placeLocation = arguments?.getString("dropLocation")
                        val parts: List<String> = placeLocation!!.split("lat/lng:")
                        val c = parts[1].split(",")

                        originLatitude = c[0].replace(" (", "").toDouble()
                        originLongitude = c[1].replace(")", "").toDouble()

                        val originLocation = LatLng(originLatitude!!, originLongitude!!)
                        val destinationLocation = LatLng(location.latitude, location.longitude)
                        val urlLocation = getDirectionURL(originLocation, destinationLocation, "AIzaSyDIKTMCB12E-Gyhp8cA7pb9sRwN4EKvszE")
                        GetDirection(urlLocation).execute()
                        /**
                         * End Draw driver route
                         */
                    }
                })

        val callDriverBtn = rootView.findViewById<AppCompatButton>(R.id.callDriverBtn)
        callDriverBtn.setOnClickListener {
            val mobileNumber = pref!!.getMOBILE()
            val intent = Intent()

            // def action and parse data with intent respectively
            intent.action = Intent.ACTION_DIAL
            intent.data = Uri.parse("tel: $mobileNumber")

            startActivity(intent)
        }

        val approxAmount = rootView.findViewById<TextView>(R.id.approxAmount)

        val bookBtn = rootView.findViewById<AppCompatButton>(R.id.bookNowBtn)
        bookBtn.setOnClickListener {
            val aAmount = approxAmount.text.toString().trim()

            // dialog
            val dialog = Dialog(this.requireContext())
            dialog.setContentView(R.layout.dialog_payment)
            dialog.setCancelable(false)

            val amountToPay = dialog.findViewById<TextView>(R.id.amountToPay)
            val payBtn = dialog.findViewById<Button>(R.id.payBtn)

            amountToPay.text = aAmount

            payBtn.setOnClickListener {
                (context as AppCompatActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.containerMain, PaymentFragment.newInstance()).commitNow()
                dialog.dismiss()
            }

            dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation_1
            dialog.show()
            dialog.setCanceledOnTouchOutside(true)
        }

        return rootView
    }

    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this.requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    context as Activity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder(this.requireContext())
                    .setTitle("Location Permission Needed")
                    .setMessage("This app needs the Location permission, please accept to use location functionality")
                    .setPositiveButton(android.R.string.ok) { _, _ ->
                        //Prompt the user once explanation has been shown
                        ActivityCompat.requestPermissions(
                            context as Activity,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            MY_PERMISSIONS_REQUEST_LOCATION
                        )
                    }
                    .create()
                    .show()
            } else {
                // We can request the permission.
                ActivityCompat.requestPermissions(
                    context as Activity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_LOCATION
                )
            }
        } else {
            // Permission previously granted
            Toast.makeText(this.requireContext(), "Location Permission Granted", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        // If request is cancelled, the result arrays are empty.
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION)
            if (grantResults.isNotEmpty()
            && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {

            // permission was granted, do location-related task you need to do.
            if (ContextCompat.checkSelfPermission(
                    this.requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                == PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(this.requireContext(), "Granted", Toast.LENGTH_SHORT).show()
            }
        } else {
            // permission denied! Disable the functionality that depends on this permission.
            Toast.makeText(this.requireContext(), "Location Permission Not Granted.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    /**
     * Start Draw Location Direction
     */
    private fun getDirectionURL(origin:LatLng, dest:LatLng, secret: String) : String{
        return "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}" +
                "&destination=${dest.latitude},${dest.longitude}" +
                "&sensor=false" +
                "&mode=driving" +
                "&key=$secret"
    }

    @Suppress("DEPRECATION")
    @SuppressLint("StaticFieldLeak")
    private inner class GetDirection(val url : String) : AsyncTask<Void, Void, List<List<LatLng>>>(){
        @Deprecated("Deprecated in Java")
        override fun doInBackground(vararg params: Void?): List<List<LatLng>> {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val data = response.body!!.string()

            val result =  ArrayList<List<LatLng>>()
            try{
                val respObj = Gson().fromJson(data, MapData::class.java)
                val path =  ArrayList<LatLng>()
                for (i in 0 until respObj.routes[0].legs[0].steps.size){
                    path.addAll(decodePolyline(respObj.routes[0].legs[0].steps[i].polyline.points))
                }
                result.add(path)
            }catch (e:Exception){
                e.printStackTrace()
            }
            return result
        }

        @Deprecated("Deprecated in Java")
        override fun onPostExecute(result: List<List<LatLng>>) {
            val lineOption = PolylineOptions()
            for (i in result.indices){
                lineOption.addAll(result[i])
                lineOption.width(10f)
                lineOption.color(Color.BLUE)
                lineOption.geodesic(true)
            }
            mMap!!.addPolyline(lineOption)
        }
    }

    fun decodePolyline(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0
        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng
            val latLng = LatLng((lat.toDouble() / 1E5),(lng.toDouble() / 1E5))
            poly.add(latLng)
        }
        return poly
    }
    /**
     * End Draw Location Direction
     */

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // Let's handle onClick back btn
                    (context as AppCompatActivity).supportFragmentManager.beginTransaction()
                        .replace(R.id.containerMain, DetailsFragment.newInstance()).commitNow()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            callback
        )
    }
}