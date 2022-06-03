package com.yujun.trucksharing

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.provider.BaseColumns
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.yujun.trucksharing.dbhelper.DbHelper
import com.yujun.trucksharing.model.OrdersModel
import com.yujun.trucksharing.scheme.TruckSharingContract
import com.yujun.trucksharing.views.AccountFragment
import com.yujun.trucksharing.views.DetailsFragment
import com.yujun.trucksharing.views.HomeFragment

class MainActivity : AppCompatActivity() {

    private val dbHelper = DbHelper(this)
    var dataItemsModel : ArrayList<OrdersModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Home Fragment
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.containerMain, HomeFragment.newInstance())
                .commitNow()
        }
    }

    fun createNewDelivery(
        rName: String,
        pDate: String,
        pTime: String,
        pLocation: String,
        pGoodType: String,
        pWeight: String,
        pWidth: String,
        pLength: String,
        pHeight: String,
        pVehicleType: String
    ) {
        val db = dbHelper.writableDatabase

        // Create a new map of values, where column names are the keys
        val values = ContentValues().apply {
            put(TruckSharingContract.OrdersEntry.COLUMN_RECEIVER_NAME, rName)
            put(TruckSharingContract.OrdersEntry.COLUMN_PICK_UP_DATE, pDate)
            put(TruckSharingContract.OrdersEntry.COLUMN_PICK_UP_TIME, pTime)
            put(TruckSharingContract.OrdersEntry.COLUMN_LOCATION, pLocation)
            put(TruckSharingContract.OrdersEntry.COLUMN_GOOD_TYPE, pGoodType)
            put(TruckSharingContract.OrdersEntry.COLUMN_WEIGHT, pWeight)
            put(TruckSharingContract.OrdersEntry.COLUMN_WIDTH, pWidth)
            put(TruckSharingContract.OrdersEntry.COLUMN_LENGTH, pLength)
            put(TruckSharingContract.OrdersEntry.COLUMN_HEIGHT, pHeight)
            put(TruckSharingContract.OrdersEntry.COLUMN_VEHICLE_TYPE, pVehicleType)
        }

        // Insert the new row, returning the primary key value of the new row
        val newRowId = db?.insert(TruckSharingContract.OrdersEntry.TABLE_NAME, null, values)
        Log.d("insertedRow", newRowId.toString())

        supportFragmentManager.beginTransaction()
            .replace(R.id.containerMain, HomeFragment.newInstance())
            .commitNow()
    }

    fun readOrdersData(): ArrayList<OrdersModel> {
        val db = dbHelper.readableDatabase

        /**
         *  columns to return after this query.
         */
        val projection = arrayOf(
            BaseColumns._ID,
            TruckSharingContract.OrdersEntry.COLUMN_RECEIVER_NAME,
            TruckSharingContract.OrdersEntry.COLUMN_PICK_UP_DATE,
            TruckSharingContract.OrdersEntry.COLUMN_PICK_UP_TIME,
            TruckSharingContract.OrdersEntry.COLUMN_LOCATION,
            TruckSharingContract.OrdersEntry.COLUMN_GOOD_TYPE,
            TruckSharingContract.OrdersEntry.COLUMN_WEIGHT,
            TruckSharingContract.OrdersEntry.COLUMN_WIDTH,
            TruckSharingContract.OrdersEntry.COLUMN_LENGTH,
            TruckSharingContract.OrdersEntry.COLUMN_HEIGHT,
            TruckSharingContract.OrdersEntry.COLUMN_VEHICLE_TYPE
        )

        // sort order
        val sortOrder = "${BaseColumns._ID} DESC"

        val cursor = db.query(
            TruckSharingContract.OrdersEntry.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            sortOrder
        )

        dataItemsModel = ArrayList()
        with(cursor) {
            while (moveToNext()) {
                val itemId = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                val itemName = getString(getColumnIndexOrThrow(
                    TruckSharingContract.OrdersEntry.COLUMN_RECEIVER_NAME))
                val itemDate = getString(getColumnIndexOrThrow(
                    TruckSharingContract.OrdersEntry.COLUMN_PICK_UP_DATE))
                val itemTime = getString(getColumnIndexOrThrow(
                    TruckSharingContract.OrdersEntry.COLUMN_PICK_UP_TIME))
                val itemLocation = getString(getColumnIndexOrThrow(
                    TruckSharingContract.OrdersEntry.COLUMN_LOCATION))
                val itemGoodType = getString(getColumnIndexOrThrow(
                    TruckSharingContract.OrdersEntry.COLUMN_GOOD_TYPE))
                val itemWeight = getString(getColumnIndexOrThrow(
                    TruckSharingContract.OrdersEntry.COLUMN_WEIGHT))
                val itemWidth = getString(getColumnIndexOrThrow(
                    TruckSharingContract.OrdersEntry.COLUMN_WIDTH))
                val itemLength = getString(getColumnIndexOrThrow(
                    TruckSharingContract.OrdersEntry.COLUMN_LENGTH))
                val itemHeight = getString(getColumnIndexOrThrow(
                    TruckSharingContract.OrdersEntry.COLUMN_HEIGHT))
                val itemVehicleType = getString(getColumnIndexOrThrow(
                    TruckSharingContract.OrdersEntry.COLUMN_VEHICLE_TYPE))

                dataItemsModel.add(
                    OrdersModel(itemId, itemName, itemDate, itemTime, itemLocation, itemGoodType,
                        itemWeight, itemWidth, itemLength, itemHeight, itemVehicleType)
                )
            }
        }
        cursor.close()
        Log.d("resDataLog1", dataItemsModel.toString())
        return dataItemsModel
    }

    fun navHomeUI(item: MenuItem) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.containerMain, HomeFragment.newInstance())
            .commitNow()
    }

    fun navViewProfile(item: MenuItem) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.containerMain, AccountFragment.newInstance())
            .commitNow()
    }

    fun navOrders(item: MenuItem) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.containerMain, HomeFragment.newInstance())
            .commitNow()
    }

    fun navLogOut(item: MenuItem) {
        val i = Intent(this, UsersActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(i)
    }

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }

    fun intentToDetails(
        itemId: Long,
        itemLocation: String,
        pickUpTime: String,
        receiverName: String,
        weight: String,
        width: String,
        height: String,
        length: String,
        goodType: String,
        vehicleType: String
    ) {
        val bundle = Bundle()
        bundle.putString("itemId", itemId.toString())
        bundle.putString("itemLocation", itemLocation)
        bundle.putString("pickUpTime", pickUpTime)
        bundle.putString("receiverName", receiverName)
        bundle.putString("weight", weight)
        bundle.putString("width", width)
        bundle.putString("height", height)
        bundle.putString("length", length)
        bundle.putString("goodType", goodType)
        bundle.putString("vehicleType", vehicleType)
        val transaction = supportFragmentManager.beginTransaction()
        val fragmentTwo = DetailsFragment.newInstance()
        fragmentTwo.arguments = bundle
        transaction.replace(R.id.containerMain, fragmentTwo)
        transaction.addToBackStack(null)
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        transaction.commit()
    }

    fun shareData(
        itemLocation: String,
        pickUpTime: String,
        receiverName: String,
        weight: String,
        width: String,
        height: String,
        length: String,
        goodType: String,
        vehicleType: String
    ) {
        val intent= Intent()
        intent.action=Intent.ACTION_SEND
        intent.putExtra(Intent.EXTRA_TEXT,
            "Hello, Below are details about the order:\nReceiver Name: $receiverName" +
                    "\nType of Goods: $goodType\nType of Vehicle: $vehicleType" +
                    "\nPick $itemLocation at $pickUpTime\nItem Weight :$weight Kg, Width $width m, " +
                    "Height $height m, and Length $length m."
        )
        intent.type="text/plain"
        startActivity(Intent.createChooser(intent, "Share To:"))
    }
}