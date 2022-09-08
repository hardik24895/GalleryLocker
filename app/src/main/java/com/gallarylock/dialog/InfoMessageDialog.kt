package com.gallarylock.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View

import com.gallarylock.R
import kotlinx.android.synthetic.main.dialog_info_message.*

/**
 * Created by Rajesh Kushvaha
 */
class InfoMessageDialog(context: Context?) : Dialog(context!!, R.style.DialogWithAnimation), DialogInterface.OnDismissListener {
    private var cancelable:Boolean? = true
    private var autoCancel = false
    private var showCloseButton = false
    private var title: String? = null
    private var message: String? = null
    private var icon = -1
    private var listener: onDialogDismiss? = null
    fun icon(icon: Int): InfoMessageDialog {
        this.icon = icon
        return this
    }

    fun setTitle(title: String?): InfoMessageDialog {
        this.title = title
        return this
    }

    fun setMessage(message: String?): InfoMessageDialog {
        this.message = message
        return this
    }

    fun cancelable(cancelable: Boolean): InfoMessageDialog {
        this.cancelable = cancelable
        return this
    }

    fun autoCancel(enable: Boolean): InfoMessageDialog {
        autoCancel = enable
        return this
    }

    fun showCloseButton(enable: Boolean): InfoMessageDialog {
        showCloseButton = enable
        return this
    }

    fun setListener(listener: onDialogDismiss?): InfoMessageDialog {
        this.listener = listener
        return this
    }

    override fun onDismiss(dialogInterface: DialogInterface) {
        callDismiss()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.dialog_info_message)
        cancelable?.let { setCanceledOnTouchOutside(it) }
        cancelable?.let { setCancelable(it) }
        setOnDismissListener(this)
        ivIcon.visibility = if (icon != -1) View.VISIBLE else View.GONE
        if (icon != -1) ivIcon.setImageResource(icon)
        tvTitle.visibility = if (title != null) View.VISIBLE else View.GONE
        if (title != null) tvTitle.text = title
        tvMessage.visibility = if (message != null) View.VISIBLE else View.GONE
        if (message != null) tvMessage.text = message
        ivClose.visibility = if (showCloseButton) View.VISIBLE else View.GONE
        ivClose.setOnClickListener {
            dismiss()
            callDismiss()
        }
        if (!autoCancel) return
        Handler().postDelayed({
            dismiss()
            callDismiss()
        }, 5000)
    }

    private fun callDismiss() {
        if (listener != null) listener!!.onDismiss()
    }

    interface onDialogDismiss {
        fun onDismiss()
    }
}