package com.gallarylock.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.os.Environment
import com.gallarylock.modal.FileListModal
import java.io.File


class FileDBHelper (context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {

        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    @Throws(SQLiteConstraintException::class)
    fun insertFile(file: FileListModal): Boolean {
        // Gets the data repository in write mode
        val db = writableDatabase

        // Create a new map of values, where column names are the keys
        val values = ContentValues()
        values.put(DBFiles.FileEntry.COLUMN_ID, file.id)
        values.put(DBFiles.FileEntry.COLUMN_FOLDERNAME, file.foldername)
        values.put(DBFiles.FileEntry.COLUMN_FILETYPE, file.filetype)
        values.put(DBFiles.FileEntry.COLUMN_FILENAME, file.name)
        values.put(DBFiles.FileEntry.COLUMN_SIZE, file.size)
        values.put(DBFiles.FileEntry.COLUMN_ORIGINALPATH, file.originalpath)
        values.put(DBFiles.FileEntry.COLUMN_NEWPATH, file.newpath)
        // Insert the new row, returning the primary key value of the new row
        val newRowId = db.insert(DBFiles.FileEntry.TABLE_NAME, null, values)

        return true
    }

    @Throws(SQLiteConstraintException::class)
    fun deleteFile(fileId: String): Boolean {
        // Gets the data repository in write mode
        val db = writableDatabase
        // Define 'where' part of query.
        val selection = DBFiles.FileEntry.COLUMN_ID + " LIKE ?"
        // Specify arguments in placeholder order.
        val selectionArgs = arrayOf(fileId)
        // Issue SQL statement.
        db.delete(DBFiles.FileEntry.TABLE_NAME, selection, selectionArgs)

        return true
    }

  /*  fun readUser(userid: String): ArrayList<FileListModal> {
        val users = ArrayList<FileListModal>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DBContract.UserEntry.TABLE_NAME + " WHERE " + DBContract.UserEntry.COLUMN_USER_ID + "='" + userid + "'", null)
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


  /*  fun getSingleFolderFileList(foldername: String): ArrayList<FileListModal> {
        val users = ArrayList<FileListModal>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " +DBFolder.FolderEntry.TABLE_NAME + " WHERE " + DBFolder.FolderEntry.COLUMN_ID + "='" + userid + "'", null)
        } catch (e: SQLiteException) {
            // if table not yet present, create it
            db.execSQL(FolderDBHelper.SQL_CREATE_ENTRIES)
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
  fun renameFile(file: FileListModal):Boolean{
      val db = writableDatabase

      // Create a new map of values, where column names are the keys
      val values = ContentValues()
      values.put(DBFiles.FileEntry.COLUMN_ID, file.id)
      values.put(DBFiles.FileEntry.COLUMN_FOLDERNAME, file.foldername)
      values.put(DBFiles.FileEntry.COLUMN_FILETYPE, file.filetype)
      values.put(DBFiles.FileEntry.COLUMN_FILENAME, file.name)
      values.put(DBFiles.FileEntry.COLUMN_SIZE, file.size)
      values.put(DBFiles.FileEntry.COLUMN_ORIGINALPATH, file.originalpath)
      values.put(DBFiles.FileEntry.COLUMN_NEWPATH, file.newpath)
      val whereClause = "_id=?"
      val whereArgs = arrayOf<String>(file.id)
      // Insert the new row, returning the primary key value of the new row
      val newRowId = db.update(DBFiles.FileEntry.TABLE_NAME,  values, whereClause,whereArgs)

      return true
  }

    fun getAllFiles(foldername:String): ArrayList<FileListModal> {
        val files = ArrayList<FileListModal>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DBFiles.FileEntry.TABLE_NAME + " WHERE " + DBFiles.FileEntry.COLUMN_FOLDERNAME + "='" + foldername + "'",null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_ENTRIES)
            return ArrayList()
        }

        var fileid: String
        var filename: String
        var size: String
        var originalpath: String
        var newpath: String
        var foldername:String
        var filetype:String
        if (cursor!!.moveToFirst()) {
            while (cursor.isAfterLast == false) {
                fileid = cursor.getString(cursor.getColumnIndex(DBFiles.FileEntry.COLUMN_ID))
                filename = cursor.getString(cursor.getColumnIndex(DBFiles.FileEntry.COLUMN_FILENAME))
                size = cursor.getString(cursor.getColumnIndex(DBFiles.FileEntry.COLUMN_SIZE))
                originalpath = cursor.getString(cursor.getColumnIndex(DBFiles.FileEntry.COLUMN_ORIGINALPATH))
                newpath = cursor.getString(cursor.getColumnIndex(DBFiles.FileEntry.COLUMN_NEWPATH))
                foldername = cursor.getString(cursor.getColumnIndex(DBFiles.FileEntry.COLUMN_FOLDERNAME))
                filetype = cursor.getString(cursor.getColumnIndex(DBFiles.FileEntry.COLUMN_FILETYPE))

                files.add(FileListModal(fileid,filename,size,originalpath,newpath,foldername,filetype))
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
            "CREATE TABLE " + DBFiles.FileEntry.TABLE_NAME + " (" +
                    DBFiles.FileEntry.COLUMN_ID + " TEXT PRIMARY KEY," +
                    DBFiles.FileEntry.COLUMN_FOLDERNAME + " TEXT," +
                    DBFiles.FileEntry.COLUMN_FILETYPE + " TEXT," +
                    DBFiles.FileEntry.COLUMN_FILENAME + " TEXT," +
                    DBFiles.FileEntry.COLUMN_SIZE + " TEXT," +
                    DBFiles.FileEntry.COLUMN_ORIGINALPATH + " TEXT," +
                    DBFiles.FileEntry.COLUMN_NEWPATH + " TEXT)"

        private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + DBFiles.FileEntry.TABLE_NAME
    }
    }