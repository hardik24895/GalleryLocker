package com.dlminfosoft.gallarylock.activity.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.contestee.extention.getValue
import com.contestee.extention.isEmpty
import com.contestee.extention.showSnackBar
import com.dlminfosoft.gallarylock.R
import com.dlminfosoft.gallarylock.activity.SessionManager
import com.dlminfosoft.gallarylock.activity.utility.Constant
import kotlinx.android.synthetic.main.activity_enter_pin.*

import android.R.id.message
import android.provider.Telephony.Carriers.PASSWORD

import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.AsyncTask
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_enter_pin.txtOK
import android.R.id.message

import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T





class SetPinActivity : AppCompatActivity() {
    lateinit var sessionManager: SessionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager(this)
        setContentView(R.layout.activity_enter_pin)
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
            root.showSnackBar("Please enter valid pin")
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
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}