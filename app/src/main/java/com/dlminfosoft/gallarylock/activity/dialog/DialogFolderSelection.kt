package com.dlminfosoft.gallarylock.activity.dialog

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import com.bumptech.glide.Glide
import com.dlminfosoft.gallarylock.R
import com.dlminfosoft.gallarylock.activity.modal.FolderListModal
import com.dlminfosoft.gallarylock.activity.utility.BlurDialogFragment
import kotlinx.android.synthetic.main.dialog_radio_btn.*

class DialogFolderSelection  (context: Context) : BlurDialogFragment(), LifecycleOwner {
    companion object {
        private lateinit var listener: OnItemClick
        private var folderlist = ArrayList<FolderListModal>()
        fun newInstance(
            context: Context,
            folderlist:ArrayList<FolderListModal>,
            listener: OnItemClick
        ): DialogFolderSelection {
            this.listener = listener
            this.folderlist = folderlist
            return DialogFolderSelection(context)
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
        return inflater.inflate(R.layout.dialog_radio_btn, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        for (i in folderlist) {
            val rb = RadioButton(context) // dynamically creating RadioButton and adding to RadioGroup.
            rb.setText(i.name)
            radio_group.addView(rb)
        }
        btnok.setOnClickListener {
            val Id:Int = radio_group.checkedRadioButtonId
            val rb :RadioButton = view.findViewById(Id)
            listener.onItemCLicked(rb)
            dismissAllowingStateLoss()
        }
        btncancel.setOnClickListener {
            dismissAllowingStateLoss()
        }
    }



    interface OnItemClick {
        fun onItemCLicked(text: RadioButton)
    }
}




