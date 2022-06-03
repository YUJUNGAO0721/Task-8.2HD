package com.yujun.trucksharing.dbhelper

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import com.yujun.trucksharing.scheme.TruckSharingContract

class DbHelper(context: Context)
    : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    /**
     * Create a database using an SQL helper.
     * Implement methods that create and maintain the database and tables.
     */

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_USER_ENTRIES)
        db.execSQL(SQL_CREATE_ORDERS_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        /**
         * This database is only a cache for online data, so its upgrade policy is to simply to
         * discard the data and start over.
         */
        db.execSQL(SQL_DELETE_USERS_ENTRIES)
        db.execSQL(SQL_DELETE_ORDERS_ENTRIES)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    companion object {
        /**
         * If you change the database schema, you must increment the database version.
         */
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "TruckSharingDb.db"

        private const val SQL_CREATE_USER_ENTRIES =
            "CREATE TABLE ${TruckSharingContract.UsersEntry.TABLE_NAME} (" +
                    "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                    "${TruckSharingContract.UsersEntry.COLUMN_USER_NAME} TEXT," +
                    "${TruckSharingContract.UsersEntry.COLUMN_FULL_NAME} TEXT," +
                    "${TruckSharingContract.UsersEntry.COLUMN_PHONE_NUMBER} TEXT," +
                    "${TruckSharingContract.UsersEntry.COLUMN_PASSWORD} TEXT," +
                    "${TruckSharingContract.UsersEntry.COLUMN_PROFILE_PHOTO} TEXT)"

        private const val SQL_CREATE_ORDERS_ENTRIES =
            "CREATE TABLE ${TruckSharingContract.OrdersEntry.TABLE_NAME} (" +
                    "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                    "${TruckSharingContract.OrdersEntry.COLUMN_RECEIVER_NAME} TEXT," +
                    "${TruckSharingContract.OrdersEntry.COLUMN_PICK_UP_DATE} TEXT," +
                    "${TruckSharingContract.OrdersEntry.COLUMN_PICK_UP_TIME} TEXT," +
                    "${TruckSharingContract.OrdersEntry.COLUMN_LOCATION} TEXT," +
                    "${TruckSharingContract.OrdersEntry.COLUMN_GOOD_TYPE} TEXT,"+
                    "${TruckSharingContract.OrdersEntry.COLUMN_WEIGHT} TEXT," +
                    "${TruckSharingContract.OrdersEntry.COLUMN_WIDTH} TEXT," +
                    "${TruckSharingContract.OrdersEntry.COLUMN_LENGTH} TEXT," +
                    "${TruckSharingContract.OrdersEntry.COLUMN_HEIGHT} TEXT," +
                    "${TruckSharingContract.OrdersEntry.COLUMN_VEHICLE_TYPE} TEXT)"

        private const val SQL_DELETE_USERS_ENTRIES =
            "DROP TABLE IF EXISTS ${TruckSharingContract.UsersEntry.TABLE_NAME}"

        private const val SQL_DELETE_ORDERS_ENTRIES =
            "DROP TABLE IF EXISTS ${TruckSharingContract.OrdersEntry.TABLE_NAME}"
    }
}