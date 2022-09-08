package com.gallarylock.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.gallarylock.modal.FileListModal
import com.gallarylock.modal.FolderListModal

class FolderDBHelper (context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    override fun onOpen(db: SQLiteDatabase?) {
        super.onOpen(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    @Throws(SQLiteConstraintException::class)
    fun insertFolder(folder: FolderListModal): Boolean {
        // Gets the data repository in write mode
        val db = writableDatabase

        // Create a new map of values, where column names are the keys
        val values = ContentValues()
        values.put( DBFolder.FolderEntry.COLUMN_ID, folder.id)
        values.put( DBFolder.FolderEntry.COLUMN_FOLDERNAME, folder.name)
        values.put( DBFolder.FolderEntry.COLUMN_ITEM, folder.totalItem)
        // Insert the new row, returning the primary key value of the new row
        val newRowId = db.insert(DBFolder.FolderEntry.TABLE_NAME, null, values)

        return true
    }

    @Throws(SQLiteConstraintException::class)
        fun deleteFolder(fileId: String): Boolean {
        // Gets the data repository in write mode
        val db = writableDatabase
        // Define 'where' part of query.
        val selection = DBFolder.FolderEntry.COLUMN_ID + " LIKE ?"
        // Specify arguments in placeholder order.
        val selectionArgs = arrayOf(fileId)
        // Issue SQL statement.
        db.delete(DBFolder.FolderEntry.TABLE_NAME, selection, selectionArgs)

        return true
    }


    fun renameFolder(folder: FolderListModal):Boolean{
        val db = writableDatabase

        // Create a new map of values, where column names are the keys
        val values = ContentValues()
        values.put( DBFolder.FolderEntry.COLUMN_ID, folder.id)
        values.put( DBFolder.FolderEntry.COLUMN_FOLDERNAME, folder.name)
        values.put( DBFolder.FolderEntry.COLUMN_ITEM, folder.totalItem)
        val whereClause = "_id=?"
        val whereArgs = arrayOf<String>(folder.id)
        // Insert the new row, returning the primary key value of the new row
        val newRowId = db.update(DBFolder.FolderEntry.TABLE_NAME,  values, whereClause,whereArgs)

        return true
    }

      /*fun getSingleFolderFileList(foldername: String): ArrayList<FileListModal> {
          val users = ArrayList<FileListModal>()
          val db = writableDatabase
          var cursor: Cursor? = null
          try {
              cursor = db.rawQuery("select * from " +DBFolder.FolderEntry.TABLE_NAME + " WHERE " + DBFolder.FolderEntry.COLUMN_ID + "='" + userid + "'", null)
          } catch (e: SQLiteException) {
              // if table not yet present, create it
              db.execSQL(SQL_CREATE_ENTRIES)
              return ArrayList()
          }

          var name: String
          var age: String
          if (cursor!!.moveToFirst()) {
              while (cursor.isAfterLast == false) {
                  name = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_NAME))
                  age = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_AGE))

                  users.add(UserModel(userid, name, age))
                  cursor.moveToNext()
              }
          }
          return users
      }*/

    fun getAllFOlder(): ArrayList<FolderListModal> {
        val files = ArrayList<FolderListModal>()

        val db = writableDatabase

        if(db.isOpen) {
            Log.e("not open", "yes")
        }else{
            Log.e("not open", "no")
        }
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DBFolder.FolderEntry.TABLE_NAME, null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_ENTRIES)
            return ArrayList()
        }

        var fileid: String
        var filename: String
        var size: String
        if (cursor!!.moveToFirst()) {
            while (cursor.isAfterLast == false) {
                fileid = cursor.getString(cursor.getColumnIndex( DBFolder.FolderEntry.COLUMN_ID))
                filename = cursor.getString(cursor.getColumnIndex( DBFolder.FolderEntry.COLUMN_FOLDERNAME))
                size = cursor.getString(cursor.getColumnIndex( DBFolder.FolderEntry.COLUMN_ITEM))

                files.add(FolderListModal(fileid,filename,size))
                cursor.moveToNext()
            }
        }
        return files
    }

    companion object {
        // If you change the database schema, you must increment the database version.
        val DATABASE_VERSION = 1
        val DATABASE_NAME = "galleryloker.db"

        private val SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DBFolder.FolderEntry.TABLE_NAME + " (" +
                    DBFolder.FolderEntry.COLUMN_ID + " TEXT PRIMARY KEY," +
                    DBFolder.FolderEntry.COLUMN_FOLDERNAME + " TEXT," +
                    DBFolder.FolderEntry.COLUMN_ITEM +  " TEXT)"

        private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " +  DBFolder.FolderEntry.TABLE_NAME
    }
}