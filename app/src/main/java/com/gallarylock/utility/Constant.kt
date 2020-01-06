package com.gallarylock.utility

import android.os.Environment
import java.io.File

object Constant {
    const val ORIGINAL="original"
    const val UNHIDE="unhide"
    const val PIN = "pin"
    const val EMAIL = "id"
    const val DATA = "data"
    const val IMAGE = "image"
    const val VIDEO = "video"

    const val ERROR = "error"
    const val UNAUTHORIZED = "unauthorized"
    const val COUNTRY_ID = "countryId"
    const val STATE_ID = "stateId"
    const val ENABLE = "visible"
    const val USER_ID = "userId"
    const val POSITION = "position"
    const val HOME = "home"
    const val TOTAL_VOTE = "totalvotes"
    const val PROFILE_PIC = "profilePic"
    const val NAME = "name"
    const val FNAME = "fname"
    const val LNAME = "lname"
    const val COUNTRY = "country"
    const val STATE = "state"
    const val CITY = "city"
    const val URI = "uri"
    const val GLOBAL = "global"
    const val TYPE = "type"
    const val AS_HOST: String = "asHost"
    const val NOTIFICATION_TYPE = "notificationType"

    const val PROFILE_PHOTO = "profilePhoto"
    const val COVER_PHOTO = "coverPhoto"
    const val BUCKET_NAME = "contestee"
    const val BUCKET_ACCESS_KEY = "AKIAJBM3XU4HNVVTAOXA"
    const val BUCKET_SECRETE_KEY = "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"

    const val MY_MAIL = "kanzariyahardikkumar@gmail.com"
    const val MY_PASSWORD = "Kanzariya24895"
    const val APPLICATON_FOLDER_NAME = ".Gallary Locker";


   const val defualtDBPath = "//data//com.gallarylock//databases//gallaryloker.db"
    const val defualtDBPathShm = "//data//com.gallarylock//databases//gallaryloker.db-shm"
    const val defualtDBPathWal = "//data//com.gallarylock//databases//gallaryloker.db-wal"
   const val DB_NAME="gallaryloker.db"
    val sdDatabsePath = File(
        Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/" + APPLICATON_FOLDER_NAME + "/" + "Databse"
    )

    val sdbackupDBPath = File(sdDatabsePath.absolutePath + "/" + "gallaryloker.db")
    val data = Environment.getDataDirectory()
    val defualtDbFile = File(data, defualtDBPath)
    val defualtDbFileShm = File(data, defualtDBPathShm)
    val defualtDbFileWal = File(data, defualtDBPathWal)
}