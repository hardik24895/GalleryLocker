package com.gallarylock.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.contestee.extention.*
import com.gallarylock.R
import com.gallarylock.SessionManager
import com.gallarylock.Utility.showToast
import com.gallarylock.dialog.ForgotPasswordDialog
import com.gallarylock.utility.Constant
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.toolbar_title.*

import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.*
import javax.net.ssl.HttpsURLConnection


class LoginActivity : AppCompatActivity() {
    //Permission code
    private val PERMISSION_REQUEST = 1001
    lateinit var session: SessionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        session = SessionManager(this)
        setContentView(R.layout.activity_login)
        txtTitle.text = "Login"
        txtForgot.setOnClickListener {
            object : ForgotPasswordDialog(this) {
                override fun onEmailSent() {

                }
            }.show()
        }

        txtOK.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    PERMISSION_REQUEST
                )
            } else {
                validation()
            }
        }
    }

    private fun validation() {
        val folderDirectory = File(
            Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + Constant.APPLICATON_FOLDER_NAME + "/" + "Pin" + "/" + "login"
        )
        val encryptedData = folderDirectory.readBytes()
        val decryptedData = ImageEncryptDecrypt(Constant.MY_PASSWORD).decrypt(encryptedData)
        val key: String = String(decryptedData)
        Log.e("password", key)
        session.storeDataByKey(Constant.PIN, key)
        if (edtpin.isEmpty()) {
            root.showSnackBar("Please enter valid pin")
        } else if (!key.equals(edtpin.getValue())) {
            txtPin.visible()
        } else {
            txtPin.invisible()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            validation()
            //getListOfFolder()

        } else {

            showToast("Permission denied!")
        }
    }


}