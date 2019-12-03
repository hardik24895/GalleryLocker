package com.gallarylock.activity

import android.annotation.SuppressLint
import android.media.Image
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.gallarylock.R
import com.gallarylock.adapter.FileListAdapter
import com.gallarylock.modal.FileListModal
import com.gallarylock.utility.Constant.APPLICATON_FOLDER_NAME

import kotlinx.android.synthetic.main.activity_folder_detail.*
import java.io.File

class FolderDetailActivity : AppCompatActivity() , FileListAdapter.OnItemSelected {
    private var adapter: FileListAdapter? = null
    private var selectedPath: String? = null
    val fileList = ArrayList<FileListModal>()
    var  folderName: String? =null
    private val images = java.util.ArrayList<Image>()
    var isFABOpen: Boolean = false
    companion object {
        //image pick code
        private val IMAGE_PICK_CODE = 1000;
        //Permission code
        private val PERMISSION_REQUEST = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_folder_detail)
        setupRecyclerView()
        folderName =intent.getStringExtra("name")
        getFileList(folderName.toString())



        fab.setOnClickListener { view ->
            // pickImageFromGallery()
            //  showCreateCategoryDialog()
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
        val f = File(Environment.getExternalStorageDirectory(), APPLICATON_FOLDER_NAME +"/" + folderName )
        val files = f.listFiles()
        for (inFile in files!!) {
            fileList.add(FileListModal(inFile.name, inFile.absolutePath))

        }
    }

    override fun onItemSelect(position: Int, data: FileListModal) {

    }

    override fun onOptionItemSelect(position: Int, data: FileListModal, itemView: View) {

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