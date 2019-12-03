package com.gallarylock.database

import android.provider.BaseColumns

object DBFolder {

    /* Inner class that defines the table contents */
    class FolderEntry : BaseColumns {
        companion object {
            val TABLE_NAME = "allfolder"
            val COLUMN_ID = "_id"
            val COLUMN_FOLDERNAME = "folderName"
            val COLUMN_ITEM = "item"
        }
    }
}