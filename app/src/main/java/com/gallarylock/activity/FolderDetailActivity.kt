package com.gallarylock.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import com.esafirm.imagepicker.features.ImagePicker
import com.gallarylock.R
import com.gallarylock.Utility
import com.gallarylock.adapter.FileListAdapter
import com.gallarylock.database.FileDBHelper
import com.gallarylock.modal.FileListModal
import com.gallarylock.utility.Constant
import com.gallarylock.utility.Constant.APPLICATON_FOLDER_NAME
import kotlinx.android.synthetic.main.activity_folder_detail.*
import kotlinx.android.synthetic.main.toolbar_title.*
import vn.tungdx.mediapicker.MediaOptions
import vn.tungdx.mediapicker.activities.MediaPickerActivity
import java.io.*
import java.io.File.separatorChar
import java.net.URI
import java.util.*
import kotlin.collections.ArrayList


class FolderDetailActivity : AppCompatActivity() , FileListAdapter.OnItemSelected {
    private var adapter: FileListAdapter? = null
    private var selectedPath: String? = null
    var fileList = ArrayList<FileListModal>()
    var  folderName: String? =null
    private val images = java.util.ArrayList<com.esafirm.imagepicker.model.Image>()
    lateinit var fileDBHelper: FileDBHelper
    var isFABOpen: Boolean = false
    companion object {
        //image pick code
        private val IMAGE_PICK_CODE = 1000;
        //Permission code
        private val PERMISSION_REQUEST = 1001

        //VIDEO pick code
        private val VIDEO_PICK_CODE = 2000;
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
        fab1.setOnClickListener {
            val builder = MediaOptions.Builder()
            var options: MediaOptions? = null
            options = builder.selectVideo().canSelectMultiVideo(false).build()
            MediaPickerActivity.open(
                this,
                VIDEO_PICK_CODE,
                options
            )

            closeFABMenu()
        }
        fab2.setOnClickListener {
            pickImageFromGallery()
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

        if(data.filetype.equals(Constant.VIDEO)){
            val intent = Intent(this,FullScreenPlayerActivity::class.java)
            intent.putExtra(Constant.DATA,data.newpath)
            startActivity(intent)
        }else{
            val intent = Intent(this,FullscreenImageActivity::class.java)
            intent.putExtra(Constant.DATA,data.newpath)
            startActivity(intent)
        }


    }


    override fun onOptionItemSelect(position: Int, data: FileListModal, itemView: View) {
        var popup: PopupMenu? = null;
        popup = PopupMenu(this, itemView)
        popup.inflate(R.menu.poup_folder_detail)

        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->

            when (item!!.itemId) {
                R.id.delete -> {
                    if(File(data.newpath).delete()){
                        val result = fileDBHelper.deleteFile(data.id)
                        getFileList(folderName.toString())
                        Toast.makeText(this, "delete done", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(this, "not done", Toast.LENGTH_SHORT).show();
                    }

                }
                R.id.rename -> {
                    renameFile(data)

                }
                R.id.unhide -> {
                    //savefile(Uri.fromFile(File(data.newpath)),data.originalpath, data.newpath,data.id)

                     unHideFile(data.originalpath,data.newpath,data.id, data.name)
                       // Toast.makeText(this, item.title, Toast.LENGTH_SHORT).show();


                }
            }

            true
        })

        popup.show()
    }


    private fun unHideFile(originalpath: String, newpath: String, id:String, fileName:String) {

       /* val folderDirectory = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/" + fileName

        )*/
          if( File(newpath).renameTo(File(originalpath)))
        {

           // Toast.makeText(this, "moved done...", Toast.LENGTH_SHORT).show();

            val result = fileDBHelper.deleteFile(id)
            getFileList(folderName.toString())
           // Toast.makeText(this, "delete done", Toast.LENGTH_SHORT).show();
          //  EncriptDycript.delete(this, File(newpath))
        /*    if (EncriptDycript.delete(this, File(newpath)))
          {

            }else{
                Toast.makeText(this, "not done", Toast.LENGTH_SHORT).show();
            }*/


            // adapter?.notifyDataSetChanged()
        }else{
              Toast.makeText(this, "Not moved", Toast.LENGTH_SHORT).show();
        }
    /*    var `in`: InputStream? = null
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

            if (EncriptDycript.delete(this, File(originalpath))) {
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
        }*/
    }
    private fun pickImageFromGallery() {
        ImagePicker.create(this)
            .returnAfterFirst(true) // set whether pick or camera action should return immediate result or not. For pick image only work on single mode
            .folderMode(true) // folder mode (false by default)
            .folderTitle("Folder") // folder selection title
            .imageTitle("Tap to select") // image selection title
            .single() // single mode
            .limit(1) // max images can be selected (99 by default)
            .showCamera(true) // show camera or not (true by default)
            .imageDirectory("Camera")
            .origin(images)// directory name for captured image  ("Camera" folder by default)
            .theme(R.style.AppTheme_NoActionBar) // must inherit ef_BaseTheme. please refer to sample
            .enableLog(false) // disabling log
            .start(IMAGE_PICK_CODE) // start image picker activity with request code

    }

    //handle result of picked image
    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            val images = ImagePicker.getImages(data)
            val sb = StringBuilder()
            var i = 0
            val l = images.size
            while (i < l) {
                sb.append(images[i].path)
                i++
            }



            getFilePath(sb.trim().toString(), Constant.IMAGE)


        }
        if (requestCode == VIDEO_PICK_CODE) {
            if (MediaPickerActivity.getMediaItemSelected(data) != null) {
                val chosenVideo =
                    MediaPickerActivity.getMediaItemSelected(data).get(0)
                val f = File(chosenVideo.getPathOrigin(this))
                if (f.exists()) {
                    Log.d("original_Path=====", f.absolutePath)
                    getFilePath(f.absolutePath, Constant.VIDEO)
                } else {
                    Toast.makeText(
                        this,
                        "File not found",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }

    }
    private fun getFilePath(originalpath: String, type: String) {
        var fileName: String =
            originalpath.substring(originalpath.lastIndexOf("/") + 1);
        val newpath = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/" + APPLICATON_FOLDER_NAME + "/" + folderName.toString() + "/"
        //moveFile(path,fileName,rootPath)
        moveFile(originalpath, fileName, newpath, folderName.toString(), type)

    }
    private fun moveFile(
        originalpath: String,
        inputFile: String,
        newpath: String,
        folderName: String,
        type: String
    ) {
        /*  if( File(inputPath).renameTo(File(outputPath + inputFile + ".hide")))
          {
              File(inputPath).delete()

              adapter?.notifyDataSetChanged()
          }else{
              Log.e("notMoved", "error")
          }*/
        //  var path: Path = Files.move(Paths.get(inputFile),Paths.get(outputPath + inputFile + ".hide"))
        var `in`: InputStream? = null
        var out: OutputStream? = null
        try {
            //create output directory if it doesn't exist
            val dir = File(originalpath)
            if (!dir.exists()) {
                dir.mkdirs()
            }
            `in` = FileInputStream(originalpath)
            out = FileOutputStream(newpath + inputFile)
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

            val size = Utility.calculateSize(File(originalpath).length().toInt() / 1024)
            val result = fileDBHelper.insertFile(
                FileListModal(
                    UUID.randomUUID().toString(),
                    inputFile,
                    size,
                    originalpath,
                    newpath + inputFile,
                    folderName,
                    type
                )
            )
            getFileList(folderName)
            if (result) Log.d("file add===", "yes")
            if (EncriptDycript.delete(this, File(originalpath))) {
                Log.d("delete", "yes")
            }
            // delete the original file
            //  File(inputPath).delete()
        } catch (fnfe1: FileNotFoundException) {
            Log.e("tag", "error")
        } catch (e: Exception) {
            Log.e("tag", e.message)
        }
    }

    fun renameFile(inFile: FileListModal) {
        val context = this
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Rename Folder")
        val view = layoutInflater.inflate(R.layout.dialog_create_folder, null)

        val categoryEditText = view.findViewById(R.id.categoryEditText) as EditText
        categoryEditText.setText(inFile.name)
        builder.setView(view);

        // set up the ok button
        builder.setPositiveButton(android.R.string.ok) { dialog, p1 ->
            val newCategory = categoryEditText.text
            var isValid = true
            if (newCategory.isBlank()) {
                categoryEditText.error = getString(R.string.validation_empty)
                isValid = false

            }

            if (isValid) {
                // do something

                        val size = Utility.calculateSize(File(inFile.newpath).length().toInt() / 1024)
                        val result = fileDBHelper.renameFile(
                            FileListModal(
                                inFile.id,
                                newCategory.toString(),
                                size,
                                inFile.originalpath,
                                inFile.newpath,
                                inFile.foldername,
                                inFile.filetype
                            )
                        )
                        val source = File(
                            Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + APPLICATON_FOLDER_NAME,
                           folderName + "/" + inFile.newpath
                        )
                        val destination = File(
                            Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + APPLICATON_FOLDER_NAME,
                           folderName + "/" + newCategory.toString()
                        )

                       source.renameTo( destination)
                        getFileList(folderName.toString())
                        Log.d("rename===", result.toString())



            }

        }

        builder.setNegativeButton(android.R.string.cancel) { dialog, p1 ->
            dialog.cancel()
        }

        builder.show();
    }

    fun savefile(sourceuri: Uri, destinationFilename:String,newpath: String,id: String)  {
        val sourceFilename: String? = sourceuri.path

        var bis: BufferedInputStream? = null
        var bos: BufferedOutputStream? = null
        try {
            bis = BufferedInputStream(FileInputStream(sourceFilename))
            bos = BufferedOutputStream(FileOutputStream(destinationFilename, false))
            val buf = ByteArray(1024)
            bis.read(buf)
            do {
                bos.write(buf)
            } while (bis.read(buf) !== -1)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                bis?.close()
                bos?.close()

                if (EncriptDycript.delete(this, File(newpath))) {
                    val result = fileDBHelper.deleteFile(id)
                    if(result){
                        folderName?.let { getFileList(it) }
                        Log.d("delete", "yes")
                    }

                }else{
                    Toast.makeText(this, "No", Toast.LENGTH_SHORT).show();
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}