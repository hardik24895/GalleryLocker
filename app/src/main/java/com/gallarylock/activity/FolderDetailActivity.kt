package com.gallarylock.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import com.gallarylock.R
import com.gallarylock.Utility
import com.gallarylock.adapter.FileListAdapter
import com.gallarylock.database.FileDBHelper
import com.gallarylock.modal.FileListModal
import com.gallarylock.utility.Constant
import com.gallarylock.utility.Constant.APPLICATON_FOLDER_NAME

import kotlinx.android.synthetic.main.activity_folder_detail.*
import kotlinx.android.synthetic.main.toolbar_title.*
import java.io.*
import java.util.*
import kotlin.collections.ArrayList

class FolderDetailActivity : AppCompatActivity() , FileListAdapter.OnItemSelected {
    private var adapter: FileListAdapter? = null
    private var selectedPath: String? = null
    var fileList = ArrayList<FileListModal>()
    var  folderName: String? =null
    private val images = java.util.ArrayList<Image>()
    lateinit var fileDBHelper: FileDBHelper
    var isFABOpen: Boolean = false
    companion object {
        //image pick code
        private val IMAGE_PICK_CODE = 1000;
        //Permission code
        private val PERMISSION_REQUEST = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fileDBHelper = FileDBHelper(this)
        setContentView(R.layout.activity_folder_detail)
        setupRecyclerView()
        folderName =intent.getStringExtra(Constant.DATA)
        setUpToolbarWithBackArrow(folderName,true)
        getFileList(folderName.toString())

        fab.setOnClickListener { view ->
            if (!isFABOpen) {
                showFABMenu();
            } else {
                closeFABMenu();
            }
        }

        fab2.setOnClickListener {
            //pickImageFromGallery()
            closeFABMenu()
        }

    }
    fun setUpToolbarWithBackArrow(strTitle: String? = null, isBackArrow: Boolean = true) {
        setSupportActionBar(toolbar2)
        toolbar2.setNavigationOnClickListener{
            finish()
        }
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false)
            actionBar.setDisplayHomeAsUpEnabled(isBackArrow)
            actionBar.setHomeAsUpIndicator(R.drawable.v_ic_back_arrow)
            if (strTitle != null) txtTitle?.text = strTitle
        }
    }
    @SuppressLint("RestrictedApi")
    private fun showFABMenu() {
        fab1.visibility = View.VISIBLE
        fab2.visibility = View.VISIBLE
        isFABOpen = true
        fab1.animate().translationY(-resources.getDimension(R.dimen.standard_55))
        fab2.animate().translationY(-resources.getDimension(R.dimen.standard_105))

    }
    @SuppressLint("RestrictedApi")
    private fun closeFABMenu() {
        isFABOpen = false
        fab1.animate().translationY(resources.getDimension(R.dimen.standard_55))
        fab2.animate().translationY(resources.getDimension(R.dimen.standard_105))
        fab1.visibility = View.GONE
        fab2.visibility = View.GONE

    }
    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        adapter = FileListAdapter(fileList, this,this)
        recyclerView.adapter = adapter
    }

    private fun getFileList(folderName : String){

        fileList =fileDBHelper.getAllFiles(folderName)

        setupRecyclerView()
       /* val f = File(Environment.getExternalStorageDirectory(), APPLICATON_FOLDER_NAME +"/" + folderName )
        val files = f.listFiles()
        for (inFile in files!!) {
           val size= Utility.calculateSize(inFile.length().toInt()/1024)
          //  fileList.add(FileListModal(inFile.name, size, inFile.absolutePath,"" ))

        }*/
    }

    override fun onItemSelect(position: Int, data: FileListModal) {
        val intent = Intent(this,FullscreenImageActivity::class.java)
        intent.putExtra(Constant.DATA,data.originalpath)
        startActivity(intent)

    }

    override fun onOptionItemSelect(position: Int, data: FileListModal, itemView: View) {
        var popup: PopupMenu? = null;
        popup = PopupMenu(this, itemView)
        popup.inflate(R.menu.poup_folder_detail)

        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->

            when (item!!.itemId) {
                R.id.delete -> {
                    Toast.makeText(this, item.title, Toast.LENGTH_SHORT).show();
                }
                R.id.rename -> {
                    Toast.makeText(this, item.title, Toast.LENGTH_SHORT).show();
                }
                R.id.unhide -> {



                        unHideFile(data.originalpath,data.newpath,data.id)
                        Toast.makeText(this, item.title, Toast.LENGTH_SHORT).show();


                }
            }

            true
        })

        popup.show()
    }


    private fun unHideFile(originalpath: String, newpath: String, id:String) {

       /*   if( File(newpath).renameTo(File(originalpath)))
        {
            if (EncriptDycript.delete(this, File(newpath)))

            adapter?.notifyDataSetChanged()
        }else{
            Log.e("notMoved", "error")
        }*/
        var `in`: InputStream? = null
        var out: OutputStream? = null
        try {
            //create output directory if it doesn't exist
            val dir = File(newpath)
            if (!dir.exists()) {
                dir.mkdirs()
            }
            `in` = FileInputStream(newpath)
            out = FileOutputStream(originalpath)
            val buffer = ByteArray(1024)
            val read: Int
            var length = `in`.read(buffer)
            // read = `in`.read(buffer)
            while (length > 0) {
                //out.write(buffer, 0, read)
                out.write(buffer, 0, length)
                length = `in`.read(buffer)
            }
            `in`.close()
            `in` = null
            // write the output file
            out.flush()
            out.close()
            out = null

            if (EncriptDycript.delete(this, File(newpath))) {
                val result = fileDBHelper.deleteFile(id)
                if(result){
                    folderName?.let { getFileList(it) }
                    Log.d("delete", "yes")
                }

            }else{
                Toast.makeText(this, "No", Toast.LENGTH_SHORT).show();
            }
            // delete the original file
            //  File(inputPath).delete()
        } catch (fnfe1: FileNotFoundException) {
            Log.e("tag", "error")
        } catch (e: Exception) {
            Log.e("tag", e.message)
        }
    }


    /*override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        } else {

            showToast("Permission denied!")
        }
    }*/
   /* private fun pickImageFromGallery() {
        ImagePicker.create(this)
            .returnAfterFirst(true) // set whether pick or camera action should return immediate result or not. For pick image only work on single mode
            .folderMode(true) // folder mode (false by default)
            .folderTitle("Folder") // folder selection title
            .imageTitle("Tap to select") // image selection title
            .single() // single mode
            .limit(1) // max images can be selected (99 by default)
            .showCamera(false) // show camera or not (true by default)
            .imageDirectory("Camera")
            .origin(images)// directory name for captured image  ("Camera" folder by default)
            .theme(R.style.AppTheme_NoActionBar) // must inherit ef_BaseTheme. please refer to sample
            .enableLog(false) // disabling log
            .start(IMAGE_PICK_CODE) // start image picker activity with request code

    }*/
}