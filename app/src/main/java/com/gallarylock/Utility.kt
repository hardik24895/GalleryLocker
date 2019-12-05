package com.gallarylock

import android.content.Context
import android.widget.Toast
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


}