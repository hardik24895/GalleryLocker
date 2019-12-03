package com.gallarylock

import android.content.Context
import android.widget.Toast

object Utility {
    fun Context.showToast(msg: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this, msg, duration).show()
    }

}