package com.gallarylock.database

import android.provider.BaseColumns

object DBFiles {

    /* Inner class that defines the table contents */
    class FileEntry : BaseColumns {
        companion object {
            val TABLE_NAME = "allfiles"
            val COLUMN_ID = "_id"
            val COLUMN_FOLDERNAME = "folderName"
            val COLUMN_FILETYPE = "type"
            val COLUMN_FILENAME = "filename"
            val COLUMN_SIZE = "filesize"
            val COLUMN_ORIGINALPATH = "fileoriginalpath"
            val COLUMN_NEWPATH = "filenewpath"
        }
    }
}