package com.yujun.trucksharing.prefmanager

import android.content.Context
import android.content.SharedPreferences

class SharedPrefManager(context: Context?) {
    var PRIVATE_MODE = 0
    var pref: SharedPreferences? = null

    var editor: SharedPreferences.Editor? = null
    var SHARED_PREF_NAME = "db_pref"

    var _context: Context? = context

    // variable declaration
    var PROFILE_PHOTO = "profilePhoto"
    var FULL_NAME = "fullName"
    var MOBILE = "mobile"
    var USER_NAME = "userName"

    // Constructor init
    init {
        pref = _context!!.getSharedPreferences(SHARED_PREF_NAME, PRIVATE_MODE)
        editor = pref!!.edit()
    }

    /**
     * start getters and setters block
     */
    @JvmName("getPROFILE_PHOTO1")
    fun getPROFILE_PHOTO(): String? {
        return if (pref!!.getString(PROFILE_PHOTO, null) != null) pref!!.getString(
            PROFILE_PHOTO,
            null
        ) else ""
    }

    @JvmName("setPROFILE_PHOTO1")
    fun setPROFILE_PHOTO(profilePhoto: String?) {
        editor!!.putString(PROFILE_PHOTO, profilePhoto)
        editor!!.apply()
    }

    @JvmName("getFULL_NAME1")
    fun getFULL_NAME(): String? {
        return if (pref!!.getString(FULL_NAME, null) != null) pref!!.getString(
            FULL_NAME,
            null
        ) else ""
    }

    @JvmName("setFULL_NAME1")
    fun setFULL_NAME(fullName: String?) {
        editor!!.putString(FULL_NAME, fullName)
        editor!!.apply()
    }

    @JvmName("getMOBILE1")
    fun getMOBILE(): String? {
        return if (pref!!.getString(MOBILE, null) != null) pref!!.getString(
            MOBILE,
            null
        ) else ""
    }

    @JvmName("setMOBILE1")
    fun setMOBILE(mobile: String?) {
        editor!!.putString(MOBILE, mobile)
        editor!!.apply()
    }

    @JvmName("getUSER_NAME1")
    fun getUSER_NAME(): String? {
        return if (pref!!.getString(USER_NAME, null) != null) pref!!.getString(
            USER_NAME,
            null
        ) else ""
    }

    @JvmName("setUSER_NAME1")
    fun setUSER_NAME(userName: String?) {
        editor!!.putString(USER_NAME, userName)
        editor!!.apply()
    }
}