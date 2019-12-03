package com.gallarylock.database

class Name {
    var id: Int = 0
    var fileSize: Int = 0
    var fileName: String? = null
    constructor(id: Int, fileName: String, fileSize:Int) {
        this.id = id
        this.fileName = fileName
        this.fileSize = fileSize
    }
    constructor(fileName: String) {
        this.fileName = fileName
    }
}