package com.gallarylock.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ablanco.zoomy.Zoomy
import com.bumptech.glide.Glide
import com.gallarylock.R
import com.gallarylock.utility.Constant
import kotlinx.android.synthetic.main.activity_fullscreen_image.*


class FullscreenImageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen_image)
        hideNavigationBar()

        val uri = intent.getStringExtra(Constant.DATA)
        Glide.with(this)
            .load(uri)
            .into(ivBackground)
        imgClose.setOnClickListener { finish() }
        val builder = Zoomy.Builder(this).target(ivBackground)
        builder.register()
    }


    private fun hideNavigationBar() {
        val currentApiVersion = Build.VERSION.SDK_INT

        val flags = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

        if (currentApiVersion >= Build.VERSION_CODES.KITKAT) {
            window.decorView.systemUiVisibility = flags
            // Code below is to handle presses of Volume up or Volume down.
            // Without this, after pressing volume buttons, the navigation bar will
            // show up and won't hide
            val decorView = window.decorView
            decorView
                .setOnSystemUiVisibilityChangeListener { visibility ->
                    if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                        decorView.systemUiVisibility = flags
                    }
                }
        }

    }

    @SuppressLint("NewApi")
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        val currentApiVersion = Build.VERSION.SDK_INT
        if (currentApiVersion >= Build.VERSION_CODES.KITKAT && hasFocus) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }


}
