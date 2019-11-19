package com.dlminfosoft.gallarylock.activity.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.dlminfosoft.gallarylock.R
import com.dlminfosoft.gallarylock.activity.SessionManager

class SplashActivity : AppCompatActivity() {
    lateinit var session: SessionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        session = SessionManager(this)
        //hiding title bar of this activity
        window.requestFeature(Window.FEATURE_NO_TITLE)
        //making this activity full screen
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_splash)

        //4second splash time
        Handler().postDelayed({

            //start main activity
            if(session.isLoggedIn){
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            }else{ startActivity(Intent(this@SplashActivity, SetPinActivity::class.java))
            }

            //finish this activity
            finish()
        },1000)

    }
}