package com.yujun.trucksharing.views

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.yujun.trucksharing.R
import com.yujun.trucksharing.databinding.FragmentDetailsBinding
import com.yujun.trucksharing.prefmanager.SharedPrefManager

class DetailsFragment : Fragment() {

    companion object {
        fun newInstance() = DetailsFragment()
    }

    var pref: SharedPrefManager? = null

    private var itemLocation: String? = ""
    private var pickUpTime: String? = ""
    private var receiverName: String? = ""
    private var weight: String? = ""
    private var strWidth: String? = ""
    private var height: String? = ""
    private var length: String? = ""
    private var goodType: String? = ""
    private var vehicleType: String? = ""

    val MY_PERMISSIONS_REQUEST_LOCATION = 99

    private val RC_SIGN_IN = 9001
    private var mSignInClient: GoogleSignInClient? = null
    // Firebase instance variables
    private var mFirebaseAuth: FirebaseAuth? = null

    var binding: FragmentDetailsBinding? = null

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        val rootView = inflater.inflate(R.layout.fragment_details, container, false)

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_details, container, false
        )

        pref = SharedPrefManager(this.requireContext())

        itemLocation = arguments?.getString("itemLocation")
        pickUpTime = arguments?.getString("pickUpTime")
        receiverName = arguments?.getString("receiverName")
        weight = arguments?.getString("weight")
        strWidth = arguments?.getString("width")
        height = arguments?.getString("height")
        length = arguments?.getString("length")
        goodType = arguments?.getString("goodType")
        vehicleType = arguments?.getString("vehicleType")

//        val detSender = rootView.findViewById<TextView>(R.id.detSender)
//        val detTime = rootView.findViewById<TextView>(R.id.detTime)
//        val detReceiver = rootView.findViewById<TextView>(R.id.detReceiver)
//        val detDropLocation = rootView.findViewById<TextView>(R.id.detDropLocation)
//        val detWeight = rootView.findViewById<TextView>(R.id.detWeight)
//        val detIndustry = rootView.findViewById<TextView>(R.id.detIndustry)
//        val detWidth = rootView.findViewById<TextView>(R.id.detWidth)
//        val detHeight = rootView.findViewById<TextView>(R.id.detHeight)
//        val detLength = rootView.findViewById<TextView>(R.id.detLength)
//        val getEstimateLocationBtn = rootView.findViewById<AppCompatButton>(R.id.getEstimateLocationBtn)

        binding!!.detSender.text = pref!!.getFULL_NAME()
        binding!!.detTime.text = pickUpTime
        binding!!.detReceiver.text = receiverName
        binding!!.detDropLocation.text = itemLocation
        binding!!.detWeight.text = "$weight kg"
        binding!!.detIndustry.text = goodType
        binding!!.detWidth.text = "$strWidth m"
        binding!!.detHeight.text = "$height m"
        binding!!.detLength.text = "$length m"

        binding!!.getEstimateLocationBtn.setOnClickListener { checkLocationPermission() }

//        val detBackBtn = rootView.findViewById<FloatingActionButton>(R.id.detBackBtn)
        binding!!.detBackBtn.setOnClickListener {
            (this.activity as AppCompatActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.containerMain, HomeFragment.newInstance())
                .commitNow()
        }

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mSignInClient = GoogleSignIn.getClient(this.requireActivity(), gso)

        // Initialize FirebaseAuth
        mFirebaseAuth = FirebaseAuth.getInstance()

//        val chatUser = rootView.findViewById<AppCompatButton>(R.id.chatUser)
        binding!!.chatUser.setOnClickListener {
            signIn()
        }

        val viewModel = ViewModelProviders.of(this)[DetailsViewModel::class.java]

        binding!!.timerViewModel = viewModel
        binding!!.lifecycleOwner = this.viewLifecycleOwner

        // Step 1.7 call create channel
        createChannel(
            getString(R.string.notification_channel_id),
            getString(R.string.notification_channel_name)
        )

//        return rootView
        return binding!!.root
    }

    private fun signIn() {
        val signInIntent = mSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)

    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent in signIn()
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(
                    ApiException::class.java
                )
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("errGoogle", "Google sign in failed: $e")
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        // To authenticate with the signed in Google account
        Log.d("authFirebase", "firebaseAuthWithGoogle:" + acct.id)
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mFirebaseAuth!!.signInWithCredential(credential)
            .addOnSuccessListener(
                this.requireActivity(),
                OnSuccessListener<AuthResult?> {
                    // If sign in succeeds the auth state listener will be notified and logic to
                    // handle the signed in user can be handled in the listener.
                    Log.d(
                        "msgSuccess",
                        "signInWithCredential:success"
                    )
                    (this.activity as AppCompatActivity).supportFragmentManager.beginTransaction()
                        .replace(R.id.containerMain, MsgFragment.newInstance())
                        .commitNow()
                })
            .addOnFailureListener(
                this.requireActivity(),
                OnFailureListener { e ->
                    // If sign in fails, display a message to the user.
                    Log.w(
                        "msgErr",
                        "signInWithCredential",
                        e
                    )
                    Toast.makeText(
                        this.requireContext(), "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                })
    }

    private fun checkLocationPermission() {
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
            intentToLocationMap()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // If request is cancelled, the result arrays are empty.
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) if (grantResults.isNotEmpty()
            && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {

            // permission was granted, do location-related task you need to do.
            if (ContextCompat.checkSelfPermission(
                    this.requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                == PackageManager.PERMISSION_GRANTED
            ) {
                intentToLocationMap()
            }
        } else {
            // permission denied! Disable the functionality that depends on this permission.
            Toast.makeText(this.requireContext(), "Location Permission Not Granted.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun intentToLocationMap() {
        val bundle = Bundle()
        bundle.putString("dropLocation", itemLocation)
        val transaction =
            (this.activity as AppCompatActivity).supportFragmentManager.beginTransaction()
        val fragmentTwo = LocationFragment.newInstance()
        fragmentTwo.arguments = bundle
        transaction.replace(R.id.containerMain, fragmentTwo)
        transaction.addToBackStack(null)
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        transaction.commit()
    }

    // channel
    private fun createChannel(channelId: String, channelName: String) {
        // Step 1.6 START create a channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                // Step 2.4 change importance
                NotificationManager.IMPORTANCE_HIGH
            )// Step 2.6 disable badges for this channel
                .apply {
                    setShowBadge(false)
                }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.breakfast_notification_channel_description)

            val notificationManager = requireActivity().getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)

        }
        // Step 1.6 END create a channel
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // Let's handle onClick back btn
                    (context as AppCompatActivity).supportFragmentManager.beginTransaction()
                        .replace(R.id.containerMain, HomeFragment.newInstance()).commitNow()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            callback
        )
    }
}