package com.gallarylock.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.Environment
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.contestee.extention.getValue
import com.gallarylock.R
import com.gallarylock.SessionManager
import com.gallarylock.activity.ImageEncryptDecrypt
import com.gallarylock.utility.Constant
import kotlinx.android.synthetic.main.dialog_change_email.*

import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.HashMap
import javax.net.ssl.HttpsURLConnection

open class DialogChangeEmail(context: Context?) :
    Dialog(context!!, R.style.DialogWithAnimation), View.OnClickListener,
    DialogInterface.OnDismissListener {
    lateinit var sessionManager: SessionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_change_email)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        sessionManager = SessionManager(context)
        setOnDismissListener(this)
        btnok.setOnClickListener(this)
        etEmail.setText(sessionManager.getDataByKey(Constant.EMAIL))
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btnok -> if (etEmail.text.toString().trim { it <= ' ' }.isEmpty()) {
                etEmail.error = context.getString(R.string.validation_input_email)
                etEmail.requestFocus()
            } else if (!Patterns.EMAIL_ADDRESS.matcher(etEmail.text.toString().trim { it <= ' ' }).matches()) {
                etEmail.error = context.getString(R.string.validation_input_email)
                etEmail.requestFocus()
            } else {
                sessionManager.storeDataByKey(Constant.EMAIL,etEmail.getValue())
                dismiss()
            }
        }
    }



    override fun onDismiss(dialogInterface: DialogInterface) {
        val v: View? = etEmail
        if (v != null) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(v.windowToken, 0)
        }
    }


}