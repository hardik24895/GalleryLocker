package com.gallarylock.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.contestee.extention.getValue
import com.contestee.extention.isEmpty
import com.contestee.extention.showSnackBar
import com.gallarylock.R
import com.gallarylock.SessionManager
import com.gallarylock.Utility
import com.gallarylock.dialog.InfoMessageDialog
import com.gallarylock.utility.Constant
import kotlinx.android.synthetic.main.activity_changepin.*
import kotlinx.android.synthetic.main.activity_changepin.editpin
import kotlinx.android.synthetic.main.activity_changepin.edtconfimpin
import kotlinx.android.synthetic.main.activity_changepin.root

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar_title.*

class ChangePinActivity : AppCompatActivity() {
    lateinit var session: SessionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_changepin)
        session = SessionManager(this)
        setUpToolbarWithBackArrow("Change Pin", true)
        txtOK.setOnClickListener { validation() }
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

    fun validation() {
        val currentPin = session.getDataByKey(Constant.PIN)
        if (edtcurrentPin.isEmpty()) {
            root.showSnackBar("Please enter current pin")
        } else if (!currentPin.equals(edtcurrentPin.getValue())) {
            root.showSnackBar("Current pin is wrong")
        } else if (editpin.isEmpty() || editpin.getValue().length != 4) {
            root.showSnackBar("Please enter minimum 4 digit pin")
        } else if (edtconfimpin.isEmpty() || edtconfimpin.getValue() != editpin.getValue()) {
            root.showSnackBar("Pin Does Not Match")
        } else {
            session.storeDataByKey(Constant.PIN, editpin.getValue())
            Utility.generateNoteOnSD(this, "login", editpin.getValue())
            InfoMessageDialog(this)
                .setTitle(getString(R.string.success))
                .setMessage("Successfuly Chnaged Your Pin")
                .icon(R.drawable.ic_done_black_24dp)
                .autoCancel(true)
                .setListener(object : InfoMessageDialog.onDialogDismiss {
                    override fun onDismiss() {
                        finish()
                    }

                }).show()
        }

    }
}