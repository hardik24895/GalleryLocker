package com.gallarylock.dialog

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gallarylock.R
import com.gallarylock.activity.ImageEncryptDecrypt
import com.gallarylock.modal.FileListModal
import com.gallarylock.utility.BlurDialogFragment
import com.gallarylock.utility.Constant
import kotlinx.android.synthetic.main.dialog_unhide_option.*
import java.io.File

class DialogUnhideOption(context: Context) : BlurDialogFragment(), LifecycleOwner {
    companion object {
        private lateinit var listener: OnItemClick
        private var filelist = ArrayList<FileListModal>()
        private lateinit var foldername : String
        fun newInstance(
            context: Context,
            folderlist: ArrayList<FileListModal>,
            foldername: String,
            listener: OnItemClick
        ): DialogUnhideOption {
            this.listener = listener
            this.filelist = folderlist
            this.foldername = foldername
            return DialogUnhideOption(context)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppTheme_Dialog_Custom)
    }

    override fun show(manager: FragmentManager, tag: String?) {
        val fragmentTransaction = manager.beginTransaction()
        fragmentTransaction.add(this, tag)
        fragmentTransaction.commitAllowingStateLoss()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_unhide_option, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        val adapter = FilePathAdapter(filelist, context!!)
        recyclerView.adapter = adapter
        val unHidePath = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/" + "Gallary Locker" + "/" + "Unhide" + "/"  + foldername + "/"

        tvUnhide.text = unHidePath
        linOriginal.setOnClickListener {
            rbOriginal.isChecked = true
            rbUnhide.isChecked = false
        }
        linUnhide.setOnClickListener {
            rbOriginal.isChecked = false
            rbUnhide.isChecked = true
        }
        btnUnhide.setOnClickListener {
            if (rbOriginal.isChecked) listener.onItemCLicked(Constant.ORIGINAL, filelist) else listener.onItemCLicked(
                Constant.UNHIDE, filelist
            )
            dismissAllowingStateLoss()
        }
        btncancel.setOnClickListener {
            dismissAllowingStateLoss()
        }

    }

    interface OnItemClick {
        fun onItemCLicked(text:String,fileList: ArrayList<FileListModal>)
    }

    class FilePathAdapter(var folderList: ArrayList<FileListModal>, val context: Context) :
        RecyclerView.Adapter<FilePathAdapter.ViewHolder>() {

        //this method is returning the view for each item in the list
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v =
                LayoutInflater.from(parent.context).inflate(R.layout.item_filepath, parent, false)
            return ViewHolder(v)
        }

        //this method is binding the data on the list
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bindItems(folderList[position], context)
        }

        //this method is giving the size of the list
        override fun getItemCount(): Int {
            return folderList.size
        }

        //the class is hodling the list view
        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            fun bindItems(
                path: FileListModal,
                context: Context
            ) {
                val tvPath = itemView.findViewById(R.id.tvPath) as TextView
                tvPath.text = path.originalpath


            }
        }


    }
}







