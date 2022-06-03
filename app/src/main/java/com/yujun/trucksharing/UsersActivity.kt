package com.yujun.trucksharing

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.yujun.trucksharing.dbhelper.DbHelper
import com.yujun.trucksharing.prefmanager.SharedPrefManager
import com.yujun.trucksharing.scheme.TruckSharingContract
import com.yujun.trucksharing.views.LoginFragment

class UsersActivity : AppCompatActivity() {

    private val dbHelper = DbHelper(this)
    var pref: SharedPrefManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users)

        pref = SharedPrefManager(this@UsersActivity)

        // Login Fragment
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, LoginFragment.newInstance())
                .commitNow()
        }
    }

    fun createNewUser(
        fullName: String,
        userName: String,
        phoneNo: String,
        password: String,
        photoStr: String
    ) {
        val db = dbHelper.writableDatabase

        // Create a new map of values, where column names are the keys
        val values = ContentValues().apply {
            put(TruckSharingContract.UsersEntry.COLUMN_FULL_NAME, fullName)
            put(TruckSharingContract.UsersEntry.COLUMN_USER_NAME, userName)
            put(TruckSharingContract.UsersEntry.COLUMN_PHONE_NUMBER, phoneNo)
            put(TruckSharingContract.UsersEntry.COLUMN_PASSWORD, password)
            put(TruckSharingContract.UsersEntry.COLUMN_PROFILE_PHOTO, photoStr)
        }

        // Insert the new row, returning the primary key value of the new row
        val newRowId = db?.insert(TruckSharingContract.UsersEntry.TABLE_NAME, null, values)
        Log.d("insertedRow", newRowId.toString())

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, LoginFragment.newInstance())
            .commitNow()
    }


    fun loginUser(usrName: String, usrPassword: String) {
        val db = dbHelper.readableDatabase

        // Def columns to return after query
        val projection = arrayOf(
            TruckSharingContract.UsersEntry.COLUMN_USER_NAME,
            TruckSharingContract.UsersEntry.COLUMN_PASSWORD,
            TruckSharingContract.UsersEntry.COLUMN_FULL_NAME,
            TruckSharingContract.UsersEntry.COLUMN_PHONE_NUMBER
        )

        // Filter results WHERE "userName" = 'Jones', NOTE that 'Jones' is the selectionArgs
        val selection = "${TruckSharingContract.UsersEntry.COLUMN_USER_NAME} = ?"
        val selectionArgs = arrayOf(usrName)

        // SQL statement.
        val cursor = db.query(
            TruckSharingContract.UsersEntry.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null, null, null)

        var uName = ""
        var uPass = ""
        var uFullName = ""
        var uMobile = ""
        with(cursor) {
            while (moveToNext()) {
                uName = getString(getColumnIndexOrThrow(TruckSharingContract.UsersEntry.COLUMN_USER_NAME))
                uPass = getString(getColumnIndexOrThrow(TruckSharingContract.UsersEntry.COLUMN_PASSWORD))
                uFullName = getString(getColumnIndexOrThrow(TruckSharingContract.UsersEntry.COLUMN_FULL_NAME))
                uMobile = getString(getColumnIndexOrThrow(TruckSharingContract.UsersEntry.COLUMN_PHONE_NUMBER))

                // log data
                Log.d("resData", "user:$uName pass:$uPass")
            }
        }

        // if user success take go to home
        if (uName == usrName && uPass == usrPassword) {
            Toast.makeText(this@UsersActivity, "Login Success", Toast.LENGTH_SHORT).show()

            pref!!.setUSER_NAME(uName)
            pref!!.setFULL_NAME(uFullName)
            pref!!.setMOBILE(uMobile)

            val h = Intent(this@UsersActivity, MainActivity::class.java)
            h.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(h)
        } else {
            Toast.makeText(this@UsersActivity, "Invalid User, Retry", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }
}