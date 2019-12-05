package com.gallarylock.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.contestee.extention.invisible
import com.contestee.extention.visible
import com.gallarylock.R
import com.gallarylock.modal.FileListModal
import com.gallarylock.utility.Constant


class FileListAdapter (val folderList: ArrayList<FileListModal>, val context: Context, private val listener: OnItemSelected) :
    RecyclerView.Adapter<FileListAdapter.ViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_folder_deatil_list, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(folderList[position],context,listener)
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return folderList.size
    }

    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(file: FileListModal, context:Context, listener: OnItemSelected) {
            val textViewName = itemView.findViewById(R.id.txtName) as TextView
            val txtSize = itemView.findViewById(R.id.txtSize) as TextView
            val imageview = itemView.findViewById(R.id.imageView) as ImageView
            val imgplay = itemView.findViewById(R.id.imgplay) as ImageView
            val imgOption = itemView.findViewById(R.id.imgOption) as ImageView
            //  val textViewAddress  = itemView.findViewById(R.id.textViewAddress) as TextView

            if(file.filetype.equals(Constant.VIDEO))  imgplay.visible()
            textViewName.text = file.name
            txtSize.text = "Size: " + file.size
            Glide.with(context)
                .load(file.newpath)
                .into(imageview)
            //  textViewAddress.text = user.address
            imgOption.setOnClickListener {listener.onOptionItemSelect(adapterPosition,file,imgOption)  }
            itemView.setOnClickListener { listener.onItemSelect(adapterPosition,file) }
        }
    }
    interface OnItemSelected{
        fun onItemSelect(position: Int, data: FileListModal)
        fun onOptionItemSelect(position: Int, data: FileListModal, itemView: View)
    }
}