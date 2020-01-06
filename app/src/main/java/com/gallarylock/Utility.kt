package com.gallarylock

import android.content.Context
import android.os.Environment
import android.util.Log
import android.widget.Toast
import com.gallarylock.activity.ImageEncryptDecrypt
import com.gallarylock.utility.Constant
import java.io.*
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
    public fun copyDataBseToExternal(inputPath: String, inputFile: String, outputPath: String) {
        var ins: InputStream? = null
        var out: OutputStream? = null
        try {
            //create output directory if it doesn't exist
            val dir = File(outputPath)
            if (!dir.exists()) {
                dir.mkdirs()
            }
            ins = FileInputStream(inputPath)
            out = FileOutputStream(outputPath + "/" + inputFile)
            val buffer = ByteArray(1024)
            var length = ins.read(buffer)
            // read = `in`.read(buffer)
            while (length > 0) {
                //out.write(buffer, 0, read)
                out.write(buffer, 0, length)
                length = ins.read(buffer)
            }
            ins.close()

            // write the output file
            out.flush()
            out.close()
            // delete the original file
            // File(inputPath + inputFile).delete()
        } catch (fnfe1: FileNotFoundException) {
            Log.e("tag", fnfe1.message)
        } catch (e: Exception) {
            Log.e("tag", e.message)
        }
    }
     fun copyDataBseFromExternal(inputPath: String, inputFile: String, outputPath: String) {
        var ins: InputStream? = null
        var out: OutputStream? = null
        try {
            //create output directory if it doesn't exist
            val dir = File(outputPath)
            if (!dir.exists()) {
                dir.mkdirs()
            }
            ins = FileInputStream(inputPath)
            out = FileOutputStream(outputPath)
            val buffer = ByteArray(1024)
            var length = ins.read(buffer)
            // read = `in`.read(buffer)
            while (length > 0) {
                //out.write(buffer, 0, read)
                out.write(buffer, 0, length)
                length = ins.read(buffer)
            }
            ins.close()

            // write the output file
            out.flush()
            out.close()
            // delete the original file
            // File(inputPath + inputFile).delete()
        } catch (fnfe1: FileNotFoundException) {
            Log.e("tag", fnfe1.message)
        } catch (e: Exception) {
            Log.e("tag", e.message)
        }
    }

}