package com.gallarylock

import android.content.Context
import android.os.Environment
import android.widget.Toast
import com.gallarylock.activity.ImageEncryptDecrypt
import com.gallarylock.utility.Constant
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.IOException
import java.text.DecimalFormat


object Utility {
    fun Context.showToast(msg: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this, msg, duration).show()
    }

    fun calculateSize(size: Int): String {
        var hrSize = ""
        val m = size / 1024.0
        val dec = DecimalFormat("0.00")
        hrSize = if (m > 1) {
            dec.format(m).plus(" MB")
        } else {
            dec.format(size).plus(" KB")
        }
        return hrSize
    }

    fun generateNoteOnSD(
        context: Context?,
        sFileName: String?,
        sBody: String?
    ) {
        try {

            val folderDirectory = File(
                Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + Constant.APPLICATON_FOLDER_NAME,
                "Pin"
            )
            if (!folderDirectory.exists()) {
                folderDirectory.mkdirs()

            }
            val gpxfile = File(folderDirectory, sFileName)
            val writer = FileWriter(gpxfile)
            writer.append(sBody)
            writer.flush()
            writer.close()
            val encryptByte = ImageEncryptDecrypt(Constant.MY_PASSWORD).encrypt(gpxfile.readBytes())
            val fos = FileOutputStream(gpxfile.absoluteFile)
            fos.write(encryptByte)
            fos.close()



        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}