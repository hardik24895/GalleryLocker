package com.dlminfosoft.gallarylock.activity.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dlminfosoft.gallarylock.R
import com.dlminfosoft.gallarylock.activity.modal.FileListModal


class FileListAdapter (val folderList: ArrayList<FileListModal>, val context: Context) :
    RecyclerView.Adapter<FileListAdapter.ViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileListAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_folder_deatil_list, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(folderList[position],context)
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return folderList.size
    }

    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(Folder: FileListModal,context:Context) {
            val textViewName = itemView.findViewById(R.id.txtName) as TextView
            val imageview = itemView.findViewById(R.id.image) as ImageView
            //  val textViewAddress  = itemView.findViewById(R.id.textViewAddress) as TextView
            textViewName.text = Folder.name
            Glide.with(context)
                .load(Folder.path)
                .into(imageview)
            //  textViewAddress.text = user.address
        }
    }

}