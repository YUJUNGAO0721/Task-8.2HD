package com.yujun.trucksharing.model

class OrdersModel {
    var itemId : Long? = null
    var receiverName : String? = null
    var pickUpDate : String? = null
    var pickUpTime : String? = null
    var itemLocation : String? = null
    var goodType : String? = null
    var weight : String? = null
    var width : String? = null
    var length : String? = null
    var height : String? = null
    var vehicleType : String? = null

    /**
     * Secondary constructor
     */
    constructor(
        itemId: Long?,
        receiverName: String?,
        pickUpDate: String?,
        pickUpTime: String?,
        itemLocation: String?,
        goodType: String?,
        weight: String?,
        width: String?,
        length: String?,
        height: String?,
        vehicleType: String?
    ) {
        this.itemId = itemId
        this.receiverName = receiverName
        this.pickUpDate = pickUpDate
        this.pickUpTime = pickUpTime
        this.itemLocation = itemLocation
        this.goodType = goodType
        this.weight = weight
        this.width = width
        this.length = length
        this.height = height
        this.vehicleType = vehicleType
    }
}