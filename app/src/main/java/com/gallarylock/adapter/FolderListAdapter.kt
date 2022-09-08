package com.gallarylock.adapter

import android.content.Context
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gallarylock.R
import com.gallarylock.activity.ImageEncryptDecrypt
import com.gallarylock.database.FileDBHelper
import com.gallarylock.modal.FolderListModal
import com.gallarylock.utility.Constant
import com.gallarylock.utility.Constant.APPLICATON_FOLDER_NAME
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class FolderListAdapter(
    val folderList: ArrayList<FolderListModal>, val context: Context,
    private val listener: OnItemSelected, val filesDBHelper: FileDBHelper
) :
    RecyclerView.Adapter<FolderListAdapter.ViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list_folder_list, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(folderList[position], listener, context, filesDBHelper)
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return folderList.size
    }

    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(
            Folder: FolderListModal,
            listener: OnItemSelected,
            context: Context,
            filesDBHelper: FileDBHelper
        ) {
            val imageView = itemView.findViewById(R.id.imageView) as ImageView
            val imgOption = itemView.findViewById(R.id.imgOption) as ImageView
            val txtCount = itemView.findViewById(R.id.txtCount) as TextView

            if (filesDBHelper.getAllFiles(Folder.name).size > 0) {
                val encryptedData =
                    File(filesDBHelper.getAllFiles(Folder.name).get(0).newpath).readBytes()
                val decryptedData = ImageEncryptDecrypt(Constant.MY_PASSWORD).decrypt(encryptedData)

                Glide.with(context)
                    .load(decryptedData)
                    .into(imageView)

                txtCount.text =
                    "Total Item :  " + filesDBHelper.getAllFiles(Folder.name).size.toString()
            }
            val f = File(
                Environment.getExternalStorageDirectory(),
                APPLICATON_FOLDER_NAME + "/" + Folder.name
            )
            /*  val files = f.listFiles()
              if (files != null) {
                  if (files.size > 0) {
                      Glide.with(context)
                          .load(files.get(0).absoluteFile)
                          .into(imageView)

                      txtCount.text = "Total Item :  " + files.size.toString()
                  }

              }*/

            val textViewName = itemView.findViewById(R.id.txtName) as TextView
            //  val textViewAddress  = itemView.findViewById(R.id.textViewAddress) as TextView
            textViewName.text = Folder.name
            imgOption.setOnClickListener {
                listener.onOptionItemSelect(
                    adapterPosition,
                    Folder,
                    imgOption
                )
            }
            itemView.setOnClickListener { listener.onItemSelect(adapterPosition, Folder) }
        }
    }

    interface OnItemSelected {
        fun onItemSelect(position: Int, data: FolderListModal)
        fun onOptionItemSelect(position: Int, data: FolderListModal, itemView: View)
    }

}