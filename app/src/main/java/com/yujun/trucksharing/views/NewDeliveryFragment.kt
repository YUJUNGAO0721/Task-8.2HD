package com.yujun.trucksharing.views

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.yujun.trucksharing.MainActivity
import com.yujun.trucksharing.R
import java.util.*

class NewDeliveryFragment : Fragment() {

    companion object {
        fun newInstance() = NewDeliveryFragment()
    }

    private var m: String? = null
    private var d: String? = null
    private val myCalender: Calendar = Calendar.getInstance()

    private var checkedGoodsText : String? = null
    private var checkedVehicleText : String? = null

    private var pickUpLocation : TextView? = null

    val MY_PERMISSIONS_REQUEST_LOCATION = 99

    private var placeName: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_new_delivery, container, false)

        val a = this.requireActivity() as MainActivity

        placeName = arguments?.getString("placeName")

        val receiverName = rootView.findViewById<EditText>(R.id.receiverName)
        val pickUpDate = rootView.findViewById<TextView>(R.id.pickUpDate)
        val pickUpTime = rootView.findViewById<EditText>(R.id.pickUpTime)
        pickUpLocation = rootView.findViewById<TextView>(R.id.pickUpLocation)

        pickUpLocation!!.text = placeName

        val sectionOne = rootView.findViewById<LinearLayout>(R.id.sectionOne)
        val sectionTwo = rootView.findViewById<LinearLayout>(R.id.sectionTwo)
        val nextBtn = rootView.findViewById<AppCompatButton>(R.id.nextBtn)
        val createOrderBtn = rootView.findViewById<AppCompatButton>(R.id.createOrderBtn)

        // calender dialog picker onClick
        pickUpDate.setOnClickListener {
            val mYear = myCalender[Calendar.YEAR]
            val mMonth = myCalender[Calendar.MONTH]
            val mDay = myCalender[Calendar.DAY_OF_MONTH]
            val datePickerDialog = DatePickerDialog(this.requireContext(),
                { _: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
                    myCalender[year, month] = dayOfMonth
                    m = if (month < 10) {
                        "0" + (month + 1)
                    } else {
                        "" + (month + 1)
                    }
                    d = if (dayOfMonth < 10) {
                        "0$dayOfMonth"
                    } else {
                        "" + dayOfMonth
                    }
                    val fnD = "$year-$m-$d"
                    pickUpDate.text = fnD
                }, mYear, mMonth, mDay
            )
            datePickerDialog.show()
        }

        pickUpLocation!!.setOnClickListener {
            checkLocationPermission()
        }

        // View Next page details
        nextBtn.setOnClickListener {
            when {
                TextUtils.isEmpty(receiverName.text.toString().trim()) -> {
                    receiverName.error = "Name required!"
                }
                TextUtils.isEmpty(pickUpDate.text.toString().trim()) -> {
                    pickUpDate.error = "Date required!"
                }
                TextUtils.isEmpty(pickUpTime.text.toString().trim()) -> {
                    pickUpTime.error = "Pick up time required!"
                }
                TextUtils.isEmpty(pickUpLocation!!.text.toString().trim()) -> {
                    pickUpLocation!!.error = "Location required!"
                }
                else -> {
                    sectionOne.visibility = View.GONE
                    sectionTwo.visibility = View.VISIBLE
                }
            }
        }

        val otherGoods = rootView.findViewById<EditText>(R.id.otherGoods)
        val otherVehicle = rootView.findViewById<EditText>(R.id.otherVehicle)

        val weightTxt = rootView.findViewById<EditText>(R.id.weightTxt)
        val widthTxt = rootView.findViewById<TextView>(R.id.widthTxt)
        val lengthTxt = rootView.findViewById<EditText>(R.id.lengthTxt)
        val heightTxt = rootView.findViewById<EditText>(R.id.heightTxt)

        val radioBtnOtherGoods = rootView.findViewById<RadioButton>(R.id.radioBtnOtherGoods)
        val radioBtnOtherVehicle = rootView.findViewById<RadioButton>(R.id.radioBtnOtherVehicle)

        val radioBtnFurniture = rootView.findViewById<RadioButton>(R.id.radioBtnFurniture)
        val radioBtnDry = rootView.findViewById<RadioButton>(R.id.radioBtnDry)
        val radioBtnFood = rootView.findViewById<RadioButton>(R.id.radioBtnFood)
        val radioBtnBuilding = rootView.findViewById<RadioButton>(R.id.radioBtnBuilding)

        val radioBtnTruck = rootView.findViewById<RadioButton>(R.id.radioBtnTruck)
        val radioBtnVan = rootView.findViewById<RadioButton>(R.id.radioBtnVan)
        val radioBtnFridgeT = rootView.findViewById<RadioButton>(R.id.radioBtnFridgeT)
        val radioBtnMiniTruck = rootView.findViewById<RadioButton>(R.id.radioBtnMiniTruck)

        // create new delivery
        createOrderBtn.setOnClickListener {
            when {
                // Is the radio button checked?
                radioBtnFurniture.isChecked -> {
                    checkedGoodsText = radioBtnFurniture.text.toString()
                }
                radioBtnDry.isChecked -> {
                    checkedGoodsText = radioBtnDry.text.toString()
                }
                radioBtnFood.isChecked -> {
                    checkedGoodsText = radioBtnFood.text.toString()
                }
                radioBtnBuilding.isChecked -> {
                    checkedGoodsText = radioBtnBuilding.text.toString()
                }
                radioBtnOtherGoods.isChecked -> {
                    when {
                        TextUtils.isEmpty(otherGoods.text.toString().trim()) -> {
                            otherGoods.error = "Specify goods!"
                        } else -> { checkedGoodsText = otherGoods.text.toString().trim() }
                    }
                }
            }

            when {
                radioBtnTruck.isChecked -> {
                    checkedVehicleText = radioBtnTruck.text.toString()
                }
                radioBtnVan.isChecked -> {
                    checkedVehicleText = radioBtnVan.text.toString()
                }
                radioBtnFridgeT.isChecked -> {
                    checkedVehicleText = radioBtnFridgeT.text.toString()
                }
                radioBtnMiniTruck.isChecked -> {
                    checkedVehicleText = radioBtnMiniTruck.text.toString()
                }
                radioBtnOtherVehicle.isChecked -> {
                    when {
                        TextUtils.isEmpty(otherVehicle.text.toString().trim()) -> {
                            otherVehicle.error = "Specify Vehicle!"
                        } else -> { checkedVehicleText = otherVehicle.text.toString().trim() }
                    }
                }
            }

            when {
                // other fields
                TextUtils.isEmpty(weightTxt.text.toString().trim()) -> {
                    weightTxt.error = "Weight required!"
                }
                TextUtils.isEmpty(widthTxt.text.toString().trim()) -> {
                    widthTxt.error = "Width required!"
                }
                TextUtils.isEmpty(lengthTxt.text.toString().trim()) -> {
                    lengthTxt.error = "Length required!"
                }
                TextUtils.isEmpty(heightTxt.text.toString().trim()) -> {
                    heightTxt.error = "Height required!"
                }
                else -> {
                    val rName = receiverName.text.toString().trim()
                    val pDate = pickUpDate.text.toString().trim()
                    val pTime = pickUpTime.text.toString().trim()
                    val pLocation = pickUpLocation!!.text.toString().trim()
                    val pWeight = weightTxt.text.toString().trim()
                    val pWidth = widthTxt.text.toString().trim()
                    val pLength = lengthTxt.text.toString().trim()
                    val pHeight = heightTxt.text.toString().trim()
                    val pGoodType = checkedGoodsText!!
                    val pVehicleType = checkedVehicleText!!

                    Log.d("postNewOrder:", "$pWeight kgs W$pWidth L$pLength H$pHeight Goods:$pGoodType Vehicle:$pVehicleType")
                    a.createNewDelivery(rName, pDate, pTime, pLocation, pGoodType, pWeight, pWidth,
                        pLength, pHeight, pVehicleType)
                }
            }
        }

        return rootView
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
            (this.activity as AppCompatActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.containerMain, PlaceFragment.newInstance())
                .commitNow()
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
                (this.activity as AppCompatActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.containerMain, PlaceFragment.newInstance())
                    .commitNow()
            }
        } else {
            // permission denied! Disable the functionality that depends on this permission.
            Toast.makeText(this.requireContext(), "Location Permission Not Granted.", Toast.LENGTH_SHORT).show()
        }
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