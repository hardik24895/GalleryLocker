package com.dlminfosoft.gallarylock.activity

import android.app.NotificationManager
import android.content.Context
import com.dlminfosoft.gallarylock.R


class SessionManager(val context: Context) {
    private val pref: SecurePreferences

    init {
        val PREF_NAME = context.resources.getString(R.string.app_name)
        pref = SecurePreferences.getInstance(context, PREF_NAME)
    }

    var isLoggedIn: Boolean
        get() = pref.containsKey(KEY_IS_LOGIN) && pref.getBoolean(KEY_IS_LOGIN, false)
        set(isLoggedIn) = storeDataByKey(KEY_IS_LOGIN, isLoggedIn)



    @JvmOverloads
    fun getDataByKey(Key: String, DefaultValue: String = ""): String {
        val returnValue: String
        if (pref.containsKey(Key)) {
            returnValue = pref.getString(Key, DefaultValue)
        } else {
            returnValue = DefaultValue
        }
        return returnValue
    }

    fun getDataByKey(Key: String, DefaultValue: Boolean): Boolean? {
        return if (pref.containsKey(Key)) {
            pref.getBoolean(Key, DefaultValue)
        } else {
            DefaultValue
        }
    }

    fun getDataByKey(Key: String, DefaultValue: Int): Int {
        return if (pref.containsKey(Key)) {
            pref.getInt(Key, DefaultValue)
        } else {
            DefaultValue
        }
    }

    fun storeDataByKey(key: String, Value: Int) {
        pref.putInt(key, Value)
        pref.commit()
    }

    fun storeDataByKey(key: String, Value: String) {
        pref.putString(key, Value)
        pref.commit()
    }

    fun storeDataByKey(key: String, Value: Boolean) {
        pref.putBoolean(key, Value)
        pref.commit()
    }

    operator fun contains(key: String): Boolean {
        return pref.containsKey(key)
    }

    fun remove(key: String) {
        pref.removeValue(key)
    }

    fun clearSession() {
        val notificationManager =
            context.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()

        pref.clear()
        pref.commit()
    }

    companion object {
        private const val KEY_IS_LOGIN = "isLogin"
        private const val KEY_USER_INFO = "user"
        private const val PIN ="pin"
    }
}