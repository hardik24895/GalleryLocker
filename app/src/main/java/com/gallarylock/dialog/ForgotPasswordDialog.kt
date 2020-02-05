package com.gallarylock.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.Environment
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.content.ContextCompat

import com.gallarylock.R
import com.gallarylock.SessionManager
import com.gallarylock.activity.ImageEncryptDecrypt
import com.gallarylock.utility.Constant
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.dialog_forgot_password.*
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.*
import javax.net.ssl.HttpsURLConnection

/**
 * Created by Rajesh Kushvaha
 */
abstract class ForgotPasswordDialog(context: Context?) :
    Dialog(context!!, R.style.DialogWithAnimation), View.OnClickListener,
    DialogInterface.OnDismissListener {
    lateinit var sessionManager: SessionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_forgot_password)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        sessionManager = SessionManager(context)
        setOnDismissListener(this)
        btnSend.setOnClickListener(this)
        etEmail.setText(sessionManager.getDataByKey(Constant.EMAIL))
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btnSend -> if (etEmail.text.toString().trim { it <= ' ' }.isEmpty()) {
                etEmail.error = context.getString(R.string.validation_input_email)
                etEmail.requestFocus()
            } else if (!Patterns.EMAIL_ADDRESS.matcher(etEmail.text.toString().trim { it <= ' ' }).matches()) {
                etEmail.error = context.getString(R.string.validation_input_email)
                etEmail.requestFocus()
            } else {
                val folderDirectory = File(
                    Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + Constant.APPLICATON_FOLDER_NAME + "/" + "Pin" + "/" + "login"
                )
                val encryptedData = folderDirectory.readBytes()
                val decryptedData = ImageEncryptDecrypt(Constant.MY_PASSWORD).decrypt(encryptedData)
                val key: String = String(decryptedData)
                val postDataParams: HashMap<String, String>? = null
                postDataParams?.put("pin", key)
                postDataParams?.put("email", etEmail.text.trim().toString())
                performPostCall("https://tofiktech.000webhostapp.com/techclass/login.php", postDataParams!!)
                // requestForgotPassword()
            }
        }
    }

    fun performPostCall(
        requestURL: String?,
        postDataParams: HashMap<String, String>
    ): String? {
        val url: URL
        var response: String? = ""
        try {
            url = URL(requestURL)
            val conn: HttpURLConnection = url.openConnection() as HttpURLConnection
            conn.setReadTimeout(15000)
            conn.setConnectTimeout(15000)
            conn.setRequestMethod("GET")
            conn.setDoInput(true)
            conn.setDoOutput(true)
            val os: OutputStream = conn.getOutputStream()
            val writer = BufferedWriter(
                OutputStreamWriter(os, "UTF-8")
            )
            writer.write(getPostDataString(postDataParams))
            writer.flush()
            writer.close()
            os.close()
            val responseCode: Int = conn.getResponseCode()
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                var line: String? = null
                val br = BufferedReader(InputStreamReader(conn.getInputStream()))
                while (br.readLine().also({ line = it }) != null) {
                    response += line
                }
            } else {
                response = ""
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return response
    }

    @Throws(UnsupportedEncodingException::class)
    private fun getPostDataString(params: HashMap<String, String>): String {
        val result = StringBuilder()
        var first = true
        for ((key, value) in params.entries) {
            if (first) first = false else result.append("&")
            result.append(URLEncoder.encode(key, "UTF-8"))
            result.append("=")
            result.append(URLEncoder.encode(value, "UTF-8"))
        }
        return result.toString()
    }

    override fun onDismiss(dialogInterface: DialogInterface) {
        val v: View? = etEmail
        if (v != null) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(v.windowToken, 0)
        }
    }

    protected abstract fun onEmailSent()
}