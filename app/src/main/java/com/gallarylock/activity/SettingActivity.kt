package com.gallarylock.activity

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.gallarylock.R
import com.gallarylock.SessionManager
import com.gallarylock.dialog.DialogChangeEmail
import com.gallarylock.utility.Constant
import kotlinx.android.synthetic.main.activity_setting.*
import kotlinx.android.synthetic.main.toolbar_title.*
import java.io.File

class SettingActivity: AppCompatActivity() {
    lateinit var session: SessionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        session = SessionManager(this)
        setContentView(R.layout.activity_setting)
        setUpToolbarWithBackArrow("Setting", true)

        txtchangePin.setOnClickListener {
            startActivity(Intent(this, ChangePinActivity::class.java))
        }
        txtchangeEmail.setOnClickListener {
            object : DialogChangeEmail(this){}.show()
        }
    }
    fun setUpToolbarWithBackArrow(strTitle: String? = null, isBackArrow: Boolean = true) {
        setSupportActionBar(toolbar2)
        toolbar2.setNavigationOnClickListener {
            finish()
        }
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false)
            actionBar.setDisplayHomeAsUpEnabled(isBackArrow)
            actionBar.setHomeAsUpIndicator(R.drawable.v_ic_back_arrow)
            if (strTitle != null) txtTitle?.text = strTitle
        }
    }
}