package com.dlminfosoft.gallarylock.activity.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.contestee.extention.*
import com.dlminfosoft.gallarylock.R
import com.dlminfosoft.gallarylock.activity.SessionManager
import com.dlminfosoft.gallarylock.activity.utility.Constant
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    lateinit var session: SessionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        session = SessionManager(this)
        setContentView(R.layout.activity_login)
        txtOK.setOnClickListener {  validation() }
    }
    private  fun validation(){
        if(edtpin.isEmpty()){
            root.showSnackBar("Please enter valid pin")
        }else if(edtpin.getValue()!=session.getDataByKey(Constant.PIN)){
           txtPin.visible()
        }else{
            txtPin.invisible()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}