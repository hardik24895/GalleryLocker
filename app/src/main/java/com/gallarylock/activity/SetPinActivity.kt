package com.gallarylock.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.contestee.extention.getValue
import com.contestee.extention.isEmpty
import com.contestee.extention.showSnackBar
import com.gallarylock.R
import com.gallarylock.SessionManager
import com.gallarylock.Utility
import com.gallarylock.Utility.showToast
import com.gallarylock.utility.Constant
import kotlinx.android.synthetic.main.activity_enter_pin.*

import kotlinx.android.synthetic.main.activity_enter_pin.txtOK
import kotlinx.android.synthetic.main.toolbar_title.*


class SetPinActivity : AppCompatActivity() {
    lateinit var sessionManager: SessionManager
    private val PERMISSION_REQUEST = 1001
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager(this)
        setContentView(R.layout.activity_enter_pin)
        txtTitle.text ="Set Pin"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_REQUEST
            )
        }
        txtOK.setOnClickListener {

            validation()
            /*//Creating SendMail object
            val sm = SendMail(this, edtemail.text.toString(), "Forgot Pin", "done")

            //Executing sendmail to send email
            sm.execute()
          *//*  someTask  (edtemail.text.toString()).execute()*//* */ }


    }





    private  fun validation(){
        var emailPattern : String = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        if(editpin.isEmpty()|| editpin.getValue().length!=4){
            root.showSnackBar("Please enter minimum 4 digit pin")
        }else if(edtconfimpin.isEmpty()|| edtconfimpin.getValue()!=editpin.getValue()){
            root.showSnackBar("Pin Does Not Match")
        }else if(android.util.Patterns.EMAIL_ADDRESS.matcher(emailPattern).matches() && edtemail.getValue().length>0 )
        {
            sessionManager.isLoggedIn = true
            sessionManager.storeDataByKey(Constant.PIN,editpin.getValue())
            sessionManager.storeDataByKey(Constant.EMAIL,edtemail.getValue())
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }else{
            sessionManager.isLoggedIn = true
            sessionManager.storeDataByKey(Constant.PIN,editpin.getValue())
            sessionManager.storeDataByKey(Constant.EMAIL,edtemail.getValue())
            Utility.generateNoteOnSD(this,"login",editpin.getValue())
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

            //getListOfFolder()
        } else {

            showToast("Permission denied!")
        }
    }
}