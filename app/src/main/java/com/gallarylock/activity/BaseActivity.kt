package com.gallarylock.activity

import android.R
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.provider.Settings
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.gallarylock.SessionManager
import com.google.android.material.snackbar.Snackbar

class BaseActivity : AppCompatActivity() {
    var title: TextView? = null
    var toolbar: Toolbar? = null
    var session: SessionManager? = null
    var permissionListener: setPermissionListener? = null
    val shouldPerformDispatchTouch = true
    var lastClickTime: Long = 0
    var snackbar: Snackbar? = null
    var dialog: ProgressDialog? = null

    //private CountDownTimer timer;

    //private CountDownTimer timer;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        session = SessionManager(this)
        //AppClass.context = this
        /*timer = new CountDownTimer(TimeUnit.MINUTES.toMillis(5), 1000) {
            @Override
            public void onTick(long l) {
                Logger.e("Time remain to session expire : " + TimeUnit.MILLISECONDS.toSeconds(l));
            }

            @Override
            public void onFinish() {
                new PromptMessageDialog(AppClass.context)
                        .cancelable(false)
                        .setMessage("Your session has been expire!, Please login to continue")
                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                session.clearSession(AppClass.context, SignInActivity.class);
                            }
                        }).show();
            }
        };*/
//resetTimer();
    }

    override fun onStart() {
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        super.onStart()
    }

    override fun onDestroy() {
        dismissSnackBar()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        //if (timer != null) timer.cancel();
        super.onDestroy()
    }

    fun dismissSnackBar() {
        if (snackbar != null && snackbar!!.isShown) snackbar!!.dismiss()
    }

    fun getProgressBar(): ProgressDialog? {
        return getProgressBar(null)
    }

    fun getProgressBar(message: String?): ProgressDialog? {
        if (dialog == null) dialog = ProgressDialog(this)
        dialog!!.setMessage(message)
        return dialog
    }

    fun hideProgressBar() {
        if (dialog != null && dialog!!.isShowing()) {
            dialog!!.dismiss()
        }
    }

    fun showToastShort(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun showToastLong(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }




    /*fun setUpToolbar(strTitle: String?) {
        setUpToolbarWithBackArrow(strTitle, false)
    }

    fun setUpToolbarWithBackArrow(strTitle: String?) {
        setUpToolbarWithBackArrow(strTitle, true)
    }*/

    /*fun setUpToolbarWithBackArrow(
        strTitle: String?,
        isBackArrow: Boolean
    ) {
        toolbar = findViewById(R.id.toolbar2)
        setSupportActionBar(toolbar)
        val actionBar: ActionBar = this!!.getSupportActionBar()!!
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false)
            actionBar.setDisplayHomeAsUpEnabled(isBackArrow)
            actionBar.setHomeAsUpIndicator(R.drawable.v_ic_back_arrow)
            title = toolbar!!.findViewById(R.id.title)
            title.text=strTitle
        }
    }*/

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    fun preventDoubleClick(view: View?) { // preventing double, using threshold of 1000 ms
        if (SystemClock.elapsedRealtime() - lastClickTime < 1000) {
            return
        }
        lastClickTime = SystemClock.elapsedRealtime()
    }

    fun showSoftKeyboard(editText: EditText?) {
        val imm =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }

    fun hideSoftKeyboard() {
        try {
            val imm =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(getWindow().getCurrentFocus()!!.getWindowToken(), 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        var ret = false
        return try {
            val view: View = this!!.getCurrentFocus()!!
            ret = super.dispatchTouchEvent(event)
            if (shouldPerformDispatchTouch) {
                if (view is EditText) {
                    val w: View = this!!.getCurrentFocus()!!
                    val scrCords = IntArray(2)
                    if (w != null) {
                        w.getLocationOnScreen(scrCords)
                        val x = event.rawX + w.left - scrCords[0]
                        val y = event.rawY + w.top - scrCords[1]
                        if (event.action == MotionEvent.ACTION_UP
                            && (x < w.left || x >= w.right || y < w.top || y > w.bottom)
                        ) {
                            val imm =
                                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            imm.hideSoftInputFromWindow(getCurrentFocus()!!.getWindowToken(), 0)
                        }
                    }
                }
            }
            ret
        } catch (e: Exception) {
            ret
        }
    }


    override fun onUserInteraction() {
        super.onUserInteraction()
        //resetTimer();
    }

    open fun resetTimer() { /*if (session.isLoggedIn() && timer != null) {
            timer.cancel();
            timer.start();
        }*/
    }

  /*  fun showPermissionSettingDialog(message: String?) {
        val builder =
            AlertDialog.Builder(this)
        builder.setTitle(R.string.permission_required)
        builder.setMessage(message)
        builder.setPositiveButton(
            R.string.app_settings,
            DialogInterface.OnClickListener { dialog, which ->
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                intent.addCategory(Intent.CATEGORY_DEFAULT)
                intent.data = Uri.parse("package:" + getPackageName())
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                startActivity(intent)
                dialog.dismiss()
            })
        builder.setNegativeButton(
            R.string.cancel
        ) { dialog, which -> dialog.dismiss() }
        builder.create().show()
    }
*/
    fun requestAppPermissions(
        requestedPermissions: Array<String?>,
        requestCode: Int, listener: setPermissionListener?
    ) {
        this.permissionListener = listener
        var permissionCheck = PackageManager.PERMISSION_GRANTED
        for (permission in requestedPermissions) {
            permissionCheck =
                permissionCheck + ContextCompat.checkSelfPermission(
                    this,
                    permission!!
                )
        }
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                requestedPermissions,
                requestCode
            )
        } else {
            if (permissionListener != null) permissionListener!!.onPermissionGranted(requestCode)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    permission!!
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                if (permissionListener != null) permissionListener!!.onPermissionGranted(requestCode)
                break
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    permission
                )
            ) {
                if (permissionListener != null) permissionListener!!.onPermissionDenied(requestCode)
                break
            } else {
                if (permissionListener != null) permissionListener!!.onPermissionNeverAsk(requestCode)
                break
            }
        }
    }

    interface setPermissionListener {
        fun onPermissionGranted(requestCode: Int)
        fun onPermissionDenied(requestCode: Int)
        fun onPermissionNeverAsk(requestCode: Int)
    }
}
