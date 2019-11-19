package com.dlminfosoft.gallarylock.activity.adapter

import android.content.Context
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.dlminfosoft.gallarylock.R
import com.dlminfosoft.gallarylock.activity.modal.FolderListModal
import java.io.File

class FolderListAdapter(val folderList: ArrayList<FolderListModal>, val context: Context,
                        private val listener: FolderListAdapter.OnItemSelected) :
    RecyclerView.Adapter<FolderListAdapter.ViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderListAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_list_folder_list, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: FolderListAdapter.ViewHolder, position: Int) {
        holder.bindItems(folderList[position],listener,context)
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
            context: Context
        ) {
            val imageView = itemView.findViewById(R.id.imageView) as ImageView
            val txtCount = itemView.findViewById(R.id.txtCount) as TextView
            val f = File(Environment.getExternalStorageDirectory(), "Gallary Locker/" +  Folder.name )
            val files = f.listFiles()
           if( files!=null){
               if (files.size>0){
                   Glide.with(context)
                       .load(files.get(0).absoluteFile)
                       .placeholder(R.drawable.ic_image_black_24dp)
                       .into(imageView)

                   txtCount.text = "Total Item :  " +  files.size.toString()
               }

           }

            val textViewName = itemView.findViewById(R.id.txtName) as TextView
          //  val textViewAddress  = itemView.findViewById(R.id.textViewAddress) as TextView
            textViewName.text = Folder.name
            itemView.setOnClickListener { listener.onItemSelect(adapterPosition,Folder) }
        }
    }

    interface OnItemSelected{
        fun onItemSelect(position: Int, data: FolderListModal)
    }
}