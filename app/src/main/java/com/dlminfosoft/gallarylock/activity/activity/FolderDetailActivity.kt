package com.dlminfosoft.gallarylock.activity.activity

import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.dlminfosoft.gallarylock.R
import com.dlminfosoft.gallarylock.activity.adapter.FileListAdapter
import com.dlminfosoft.gallarylock.activity.modal.FileListModal
import com.dlminfosoft.gallarylock.activity.modal.FolderListModal
import kotlinx.android.synthetic.main.activity_folder_detail.*
import java.io.File

class FolderDetailActivity : AppCompatActivity() {
    private var adapter: FileListAdapter? = null
    private var selectedPath: String? = null
    val fileList = ArrayList<FileListModal>()
    var  folderName: String? =null
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

    }
    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        adapter = FileListAdapter(fileList, this)
        recyclerView.adapter = adapter
    }

    private fun getFileList(folderName : String){
        val f = File(Environment.getExternalStorageDirectory(), "Gallary Locker/" + folderName )
        val files = f.listFiles()
        for (inFile in files!!) {
            fileList.add(FileListModal(inFile.name, inFile.absolutePath))

        }
    }
}