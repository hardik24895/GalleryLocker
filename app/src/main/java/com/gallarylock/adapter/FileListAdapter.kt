package com.gallarylock.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.os.Handler
import android.provider.MediaStore
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gallarylock.R
import com.gallarylock.activity.ImageEncryptDecrypt
import com.gallarylock.modal.FileListModal
import com.gallarylock.utility.Constant
import com.sun.mail.iap.ByteArray
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer


class FileListAdapter(
    var folderList: ArrayList<FileListModal>,
    var folderSelectedList: ArrayList<FileListModal>,
    val context: Context,
    private val listener: OnItemSelected
) :
    RecyclerView.Adapter<FileListAdapter.ViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_folder_deatil_list, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(
            folderList[position],
            context,
            listener,
            folderList,
            folderSelectedList,
            position
        )
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return folderList.size
    }

    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(
            file: FileListModal,
            context: Context,
            listener: OnItemSelected,
            folderList: ArrayList<FileListModal>,
            folderSelectedList: ArrayList<FileListModal>,
            position: Int
        ) {
            val textViewName = itemView.findViewById(R.id.txtName) as TextView
            val txtSize = itemView.findViewById(R.id.txtSize) as TextView
            val imageview = itemView.findViewById(R.id.imageView) as ImageView
            val imgplay = itemView.findViewById(R.id.imgplay) as ImageView
            val imgOption = itemView.findViewById(R.id.imgOption) as ImageView
            val cardview = itemView.findViewById(R.id.cardview) as CardView
            //  val textViewAddress  = itemView.findViewById(R.id.textViewAddress) as TextView

            // if(file.filetype.equals(Constant.VIDEO))  imgplay.visible()
            textViewName.text = file.name
            txtSize.text = "Size: " + file.size
            val encryptedData = File(file.newpath).readBytes()
            val decryptedData = ImageEncryptDecrypt(Constant.MY_PASSWORD).decrypt(encryptedData)
            if (folderSelectedList.contains(folderList.get(position)))
                cardview.setBackgroundColor(
                    ContextCompat.getColor(context, R.color.list_item_selected_state)
                ) else
                cardview.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.list_item_normal_state
                    )
                )

            if (file.filetype.equals(Constant.IMAGE)) {
                Glide.with(context)
                    .load(decryptedData)
                    .asBitmap()
                    .into(imageview)
            } else {

                var decodedString = Base64.decode(file.filetype, Base64.DEFAULT)
                var decodedByte =
                    BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)

                val stream = ByteArrayOutputStream()
                decodedByte.compress(Bitmap.CompressFormat.PNG, 100, stream)
                val byteArray = stream.toByteArray()


                /*  val thumb = ThumbnailUtils.createVideoThumbnail(
                      file.originalpath,
                      MediaStore.Video.Thumbnails.MINI_KIND
                  );

                  val bytes: Int = thumb.getByteCount()
                  val buffer =
                      ByteBuffer.allocate(bytes) //Create a new buffer

                  thumb.copyPixelsToBuffer(buffer) //Move the byte data to the buffer


                  val array: kotlin.ByteArray = buffer.array()*/

                Glide.with(context)
                    .load(byteArray)
                    .asBitmap()
                    .into(imageview)


            }


            //  textViewAddress.text = user.address

            itemView.setOnLongClickListener {
                val p = layoutPosition
                listener.onItemLongClick(position)
                println("LongClick: $p")
                true // returning true instead of false, works for me

            }

            imgOption.setOnClickListener {
                listener.onOptionItemSelect(
                    adapterPosition,
                    file,
                    imgOption
                )
            }
            itemView.setOnClickListener { listener.onItemSelect(adapterPosition, file) }
        }
    }

    interface OnItemSelected {
        fun onItemLongClick(position: Int)
        fun onItemSelect(position: Int, data: FileListModal)
        fun onOptionItemSelect(position: Int, data: FileListModal, itemView: View)
    }

}