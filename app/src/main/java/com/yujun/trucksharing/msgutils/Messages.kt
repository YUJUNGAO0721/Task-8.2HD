package com.yujun.trucksharing.msgutils

class Messages {
    var text: String? = null
    var fullname: String? = null
    var photoUrl: String? = null
    var imageUrl: String? = null

    constructor() {}
    constructor(text: String?, fullname: String?, photoUrl: String?, imageUrl: String?) {
        this.text = text
        this.fullname = fullname
        this.photoUrl = photoUrl
        this.imageUrl = imageUrl
    }
}