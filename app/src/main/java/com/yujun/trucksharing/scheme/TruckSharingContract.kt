package com.yujun.trucksharing.scheme

import android.provider.BaseColumns

object TruckSharingContract {
    /**
     * Group table contents together in an anonymous companion object class.
     */
    object UsersEntry : BaseColumns {
        const val TABLE_NAME = "t_users"
        const val COLUMN_USER_NAME = "userName"
        const val COLUMN_FULL_NAME = "fullName"
        const val COLUMN_PHONE_NUMBER = "phone"
        const val COLUMN_PASSWORD = "password"
        const val COLUMN_PROFILE_PHOTO = "profilePic"
    }

    object OrdersEntry : BaseColumns {
        const val TABLE_NAME = "t_orders"
        const val COLUMN_RECEIVER_NAME = "receiverName"
        const val COLUMN_PICK_UP_DATE = "pickUpDate"
        const val COLUMN_PICK_UP_TIME = "pickUpTime"
        const val COLUMN_LOCATION = "location"
        const val COLUMN_GOOD_TYPE = "goodType"
        const val COLUMN_WEIGHT = "weight"
        const val COLUMN_WIDTH = "width"
        const val COLUMN_LENGTH = "length"
        const val COLUMN_HEIGHT = "height"
        const val COLUMN_VEHICLE_TYPE = "vehicleType"
    }
}