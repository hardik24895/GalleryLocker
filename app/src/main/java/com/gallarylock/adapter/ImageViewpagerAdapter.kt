package com.gallarylock.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.viewpager.widget.PagerAdapter
import com.ablanco.zoomy.Zoomy
import com.bumptech.glide.Glide
import com.gallarylock.R
import com.gallarylock.activity.ImageEncryptDecrypt
import com.gallarylock.modal.FileListModal
import com.gallarylock.utility.Constant
import java.io.File

class  ImageViewpagerAdapter internal constructor(
    context: Context,
    val fileList: ArrayList<FileListModal>
):PagerAdapter() {
    internal var context:Context
    internal var mLayoutInflater: LayoutInflater

    init{
        this.context = context
        mLayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }
  override  fun isViewFromObject(view: View, `object`:Any):Boolean {
        return view === (`object` as LinearLayout)
    }
    override fun instantiateItem(container:ViewGroup, position:Int):Any {
        val itemView = mLayoutInflater.inflate(R.layout.viewpager_image_item, container, false)
        val imageView = itemView.findViewById(R.id.ivBackground) as ImageView
        val encryptedData = File(fileList.get(position).newpath).readBytes()
        // val decryptedData: ByteArray = EncriptDycript.decrypt(encryptedData,Constant.secretKey)
        val decryptedData = ImageEncryptDecrypt(Constant.MY_PASSWORD).decrypt(encryptedData)

        Glide.with(context)
            .load(decryptedData)
            .asBitmap()
            .into(imageView)

        val builder = Zoomy.Builder(context as Activity).target(imageView)
        builder.register()

        container.addView(itemView)
        return itemView
    }
  override  fun destroyItem(container: ViewGroup, position:Int, `object`:Any) {
        container.removeView(`object` as LinearLayout)
    }

    override fun getCount(): Int {
     return   fileList.size
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}