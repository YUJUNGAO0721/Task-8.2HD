package com.yujun.trucksharing.views

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.yujun.trucksharing.MainActivity
import com.yujun.trucksharing.R
import java.util.*

class PlaceFragment : Fragment() {

    companion object {
        fun newInstance() = PlaceFragment()
    }

    private val AUTOCOMPLETE_REQUEST_CODE = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_place, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the AutocompleteSupportFragment.
        val autocompleteFragment =
            (this.requireContext() as MainActivity).supportFragmentManager
                .findFragmentById(R.id.autocomplete_fragment) as? AutocompleteSupportFragment

        if (!Places.isInitialized()) {
            Places.initialize(this.requireContext(), getString(R.string.api_key), Locale.US)
        }

        // Specify the types of place data to return.
        autocompleteFragment?.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME))

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment?.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                // Get info about the selected place.
                Log.i("selectedPlaceInfo", "Place: ${place.name}, ${place.id}")
            }

            override fun onError(status: Status) {
                // Handle the error.
                Log.i("selectedError", "An error occurred: $status")
            }
        })

        // Set the fields to specify which types of place data to
        // return after the user has made a selection.
        val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)

        // Start the autocomplete intent.
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
            .build(this.requireContext())
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        Log.i("places", "Place: ${place.name}, ${place.id}, ${place.latLng}")
                        // Place: Nairobi, ChIJp0lN2HIRLxgRTJKXslQCz_c, LatLon: lat/lng: (-1.2920659,36.8219462)

                        val bundle = Bundle()
                        bundle.putString("placeName", place.name + ", " + place.latLng?.toString())
                        val transaction = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
                        val fragmentC = NewDeliveryFragment.newInstance()
                        fragmentC.arguments = bundle
                        transaction.replace(R.id.containerMain, fragmentC)
                        transaction.addToBackStack(null)
                        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        transaction.commit()

                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    // TODO: Handle the error.
                    data?.let {
                        val status = Autocomplete.getStatusFromIntent(data)
                        Log.i("placesErr", status.statusMessage ?: "")
                    }
                }
                Activity.RESULT_CANCELED -> {
                    // The user canceled the operation.
                }
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}