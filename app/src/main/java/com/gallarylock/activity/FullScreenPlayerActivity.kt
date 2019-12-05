package com.gallarylock.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.gallarylock.R
import com.gallarylock.utility.Constant
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ads.AdsMediaSource.MediaSourceFactory
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.toolbar_title.*

class FullScreenPlayerActivity : AppCompatActivity(),
    MediaSourceFactory {
    private val STATE_RESUME_WINDOW = "resumeWindow"
    private val STATE_RESUME_POSITION = "resumePosition"
    private val STATE_PLAYER_FULLSCREEN = "playerFullscreen"
    private var playerView: PlayerView? = null
    private var mVideoSource: MediaSource? = null
    private var mExoPlayerFullscreen = false
    private var mFullScreenButton: FrameLayout? = null
    private var mFullScreenIcon: ImageView? = null
    private var mFullScreenDialog: Dialog? = null
    private var dataSourceFactory: DataSource.Factory? = null
    private var player: SimpleExoPlayer? = null
    private var mResumeWindow = 0
    private var mResumePosition: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        setUpToolbarWithBackArrow("Player",true)
        dataSourceFactory = DefaultDataSourceFactory(
            this,
            Util.getUserAgent(
                this,
                getString(R.string.app_name)
            )
        )
        if (savedInstanceState != null) {
            mResumeWindow = savedInstanceState.getInt(STATE_RESUME_WINDOW)
            mResumePosition = savedInstanceState.getLong(STATE_RESUME_POSITION)
            mExoPlayerFullscreen = savedInstanceState.getBoolean(STATE_PLAYER_FULLSCREEN)
        }
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(STATE_RESUME_WINDOW, mResumeWindow)
        outState.putLong(STATE_RESUME_POSITION, mResumePosition)
        outState.putBoolean(STATE_PLAYER_FULLSCREEN, mExoPlayerFullscreen)
        super.onSaveInstanceState(outState)
    }
    fun setUpToolbarWithBackArrow(strTitle: String? = null, isBackArrow: Boolean = true) {
        setSupportActionBar(toolbar2)
        toolbar2.setNavigationOnClickListener{
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
    private fun initFullscreenDialog() {
        mFullScreenDialog =
            object : Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
                override fun onBackPressed() {
                    if (mExoPlayerFullscreen) closeFullscreenDialog()
                    super.onBackPressed()
                }
            }
    }

    private fun openFullscreenDialog() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        (playerView!!.parent as ViewGroup).removeView(playerView)
        mFullScreenDialog!!.addContentView(
            playerView!!,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        mFullScreenIcon!!.setImageDrawable(
            ContextCompat.getDrawable(
                this@FullScreenPlayerActivity,
                R.drawable.ic_fullscreen_skrink
            )
        )
        mExoPlayerFullscreen = true
        mFullScreenDialog!!.show()

    }

    private fun closeFullscreenDialog() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        (playerView!!.parent as ViewGroup).removeView(playerView)
        (findViewById<View>(R.id.main_media_frame) as FrameLayout).addView(
            playerView
        )
        mExoPlayerFullscreen = false
        mFullScreenDialog!!.dismiss()
        mFullScreenIcon!!.setImageDrawable(
            ContextCompat.getDrawable(
                this@FullScreenPlayerActivity,
                R.drawable.ic_fullscreen_expand
            )
        )
    }

    private fun initFullscreenButton() {
        val controlView: PlayerControlView =
            playerView!!.findViewById(R.id.exo_controller)
        mFullScreenIcon =
            controlView.findViewById(R.id.exo_fullscreen_icon)
        mFullScreenButton =
            controlView.findViewById(R.id.exo_fullscreen_button)
        mFullScreenButton?.setOnClickListener(View.OnClickListener { if (!mExoPlayerFullscreen) openFullscreenDialog() else closeFullscreenDialog() })
    }

    private fun initExoPlayer() {
        player = ExoPlayerFactory.newSimpleInstance(this)
        playerView!!.player = player
        val haveResumePosition = mResumeWindow != C.INDEX_UNSET
        if (haveResumePosition) {
            Log.i("DEBUG", " haveResumePosition ")
            player?.seekTo(mResumeWindow, mResumePosition)
        }
        val uri = intent.getStringExtra(Constant.DATA)
         //  mVideoSource =  ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(uri));

            mVideoSource = createMediaSource(Uri.parse(uri))
        Log.i("DEBUG", " mVideoSource $mVideoSource")
        player?.prepare(mVideoSource)
        player?.setPlayWhenReady(true)
    }

    override fun onResume() {
        super.onResume()
        if (playerView == null) {
            playerView = findViewById(R.id.exoplayer)
            initFullscreenDialog()
            initFullscreenButton()
        }
        initExoPlayer()
        if (mExoPlayerFullscreen) {
            (playerView!!.parent as ViewGroup).removeView(playerView)
            mFullScreenDialog!!.addContentView(
                playerView!!,
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            )
            mFullScreenIcon!!.setImageDrawable(
                ContextCompat.getDrawable(
                    this@FullScreenPlayerActivity,
                    R.drawable.ic_fullscreen_skrink
                )
            )
            mFullScreenDialog!!.show()
        }
    }



    override fun onPause() {
        super.onPause()
        if (playerView != null && player != null) {
            mResumeWindow = player!!.currentWindowIndex
            mResumePosition = Math.max(0, player!!.contentPosition)
            player!!.release()
        }
        if (mFullScreenDialog != null) mFullScreenDialog!!.dismiss()
    }



    override fun getSupportedTypes(): IntArray {
        return intArrayOf(C.TYPE_DASH, C.TYPE_HLS, C.TYPE_OTHER)
    }

    override fun createMediaSource(uri: Uri): MediaSource {
       return ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
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