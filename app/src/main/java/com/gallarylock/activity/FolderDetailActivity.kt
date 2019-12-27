package com.gallarylock.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.contestee.extention.invisible
import com.contestee.extention.visible
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.model.Image
import com.gallarylock.R
import com.gallarylock.Utility
import com.gallarylock.adapter.FileListAdapter
import com.gallarylock.database.FileDBHelper
import com.gallarylock.dialog.DialogFolderSelection
import com.gallarylock.dialog.DialogUnhideOption
import com.gallarylock.modal.FileListModal
import com.gallarylock.utility.AlertDialogHelper
import com.gallarylock.utility.Constant
import com.gallarylock.utility.Constant.APPLICATON_FOLDER_NAME
import com.gallarylock.utility.RecyclerItemClickListener
import kotlinx.android.synthetic.main.activity_folder_detail.*
import kotlinx.android.synthetic.main.toolbar_title.*
import vn.tungdx.mediapicker.MediaOptions
import vn.tungdx.mediapicker.activities.MediaPickerActivity
import java.io.*
import java.util.*
import kotlin.collections.ArrayList


class FolderDetailActivity : AppCompatActivity(), FileListAdapter.OnItemSelected,
    AlertDialogHelper.AlertDialogListener {
    private var adapter: FileListAdapter? = null
    private var selectedPath: String? = null
    var fileList = ArrayList<FileListModal>()
    var SelectedFileList = ArrayList<FileListModal>()
    var folderName: String? = null
    private var images = java.util.ArrayList<com.esafirm.imagepicker.model.Image>()
    lateinit var fileDBHelper: FileDBHelper
    var isFABOpen: Boolean = false
    var mActionMode: ActionMode? = null
    var context_menu: Menu? = null
    var isMultiSelect = false
    var alertDialogHelper: AlertDialogHelper? = null

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
        alertDialogHelper = AlertDialogHelper(this)
        setupRecyclerView()
        folderName = intent.getStringExtra(Constant.DATA)
        setUpToolbarWithBackArrow(folderName, true)
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
        toolbar2.setNavigationOnClickListener {
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
        adapter = FileListAdapter(fileList, SelectedFileList, this, this)
        recyclerView.adapter = adapter
        runLayoutAnimation(recyclerView)
        /* recyclerView.addOnItemTouchListener(
             RecyclerItemClickListener(
                 this,
                 recyclerView,
                 object : RecyclerItemClickListener.OnItemClickListener {
                     override fun onItemClick(view: View?, position: Int) {
                         if (isMultiSelect) multi_select(position)
                         else

                             if (fileList.get(position) .filetype.equals(Constant.VIDEO)) {
                                 val intent = Intent(this@FolderDetailActivity, FullScreenPlayerActivity::class.java)
                                 intent.putExtra(Constant.DATA, fileList.get(position).newpath)
                                 startActivity(intent)
                             } else {
                                 val intent = Intent(this@FolderDetailActivity, FullscreenImageActivity::class.java)
                                 intent.putExtra(Constant.DATA, fileList)
                                 intent.putExtra(Constant.POSITION, position)
                                 startActivity(intent)
                             }
                             Toast.makeText(
                             applicationContext,
                             "Details Page",
                             Toast.LENGTH_SHORT
                         ).show()
                     }

                     override fun onItemLongClick(view: View?, position: Int) {
                         if (!isMultiSelect) {
                              SelectedFileList = ArrayList<FileListModal>()
                             isMultiSelect = true
                             if (mActionMode == null) {
                                 toolbar2.invisible()
                                 mActionMode = startActionMode(mActionModeCallback)
                             }
                         }
                         if(position>-1){
                             multi_select(position)
                         }

                     }
                 })
         )*/
    }

    fun multi_select(position: Int) {
        if (position > -1) {
            if (mActionMode != null) {
                if (SelectedFileList.contains(fileList.get(position))) SelectedFileList.remove(
                    fileList.get(position)
                ) else SelectedFileList.add(fileList.get(position))
                if (SelectedFileList.size > 0) mActionMode!!.title =
                    "" + SelectedFileList.size else mActionMode!!.title =
                    ""
                refreshAdapter()
            }
        }

    }

    fun refreshAdapter() {
        adapter?.folderSelectedList = SelectedFileList
        adapter?.folderList = fileList
        adapter?.notifyDataSetChanged()
    }

    private val mActionModeCallback: ActionMode.Callback =
        object : ActionMode.Callback {
            override fun onCreateActionMode(
                mode: ActionMode,
                menu: Menu
            ): Boolean { // Inflate a menu resource providing context menu items
                val inflater = mode.menuInflater
                inflater.inflate(R.menu.menu_multi_select, menu)
                context_menu = menu
                return true
            }

            override fun onPrepareActionMode(
                mode: ActionMode,
                menu: Menu
            ): Boolean {
                return false // Return false if nothing is done
            }

            override fun onActionItemClicked(
                mode: ActionMode,
                item: MenuItem
            ): Boolean {
                return when (item.itemId) {
                    R.id.action_delete -> {
                        alertDialogHelper?.showAlertDialog(
                            "",
                            "Delete Contact",
                            "DELETE",
                            "CANCEL",
                            1,
                            false
                        )
                        true
                    }

                    R.id.action_unhide -> {
                        unHideSelectFile()
                        true
                    }

                    else -> false
                }
            }

            override fun onDestroyActionMode(mode: ActionMode) {
                mActionMode = null
                isMultiSelect = false
                SelectedFileList = ArrayList<FileListModal>()
                refreshAdapter()
                toolbar2.visible()
            }
        }

    // AlertDialog Callback Functions
    override fun onPositiveClick(from: Int) {
        if (from == 1) {
            if (SelectedFileList.size > 0) {
                for (i in SelectedFileList.indices) fileList.remove(SelectedFileList.get(i))
                adapter?.notifyDataSetChanged()
                if (mActionMode != null) {
                    mActionMode!!.finish()
                }
                Toast.makeText(
                    applicationContext,
                    "Delete Click",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else if (from == 2) {
            if (mActionMode != null) {
                mActionMode!!.finish()

            }
            /*  val mSample =
                  SampleModel("Name" + fileList.size, "Designation" + user_list.size)
              user_list.add(mSample)*/
            adapter?.notifyDataSetChanged()
        }
    }


    override fun onNegativeClick(from: Int) {}

    override fun onNeutralClick(from: Int) {}

    private fun unHideSelectFile() {

        var pathList = ArrayList<FileListModal>()

        if (SelectedFileList.size > 0) {
            for (i in SelectedFileList.indices){
                pathList.add(
                    FileListModal(
                        SelectedFileList[i].id,
                        SelectedFileList[i].name,
                        SelectedFileList[i].size,
                        SelectedFileList[i].originalpath,
                        SelectedFileList[i].newpath,
                        folderName.toString(),
                        ""
                    )
                )
            }


            if (mActionMode != null) {
                mActionMode!!.finish()
            }

        }

        val folderDirectory = File(
            Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/" + "DCIM" + "/" + "Gallary Locker",
            folderName
        )
        if (!folderDirectory.exists()) {
            folderDirectory.mkdirs()
        }


        val dialog = DialogUnhideOption
            .newInstance(
                this,
                pathList,
                folderName.toString(),
                object : DialogUnhideOption.OnItemClick {
                    override fun onItemCLicked(text: String, files: ArrayList<FileListModal>) {

                        if (text.equals(Constant.ORIGINAL)) {
                            for (i in files.indices) {
                                val result = fileDBHelper.deleteFile(files[i].id)
                                savefile(File(files[i].newpath), files[i].originalpath)

                            }

                        } else {
                            for (i in files.indices) {
                                val unhidepath = Environment.getExternalStorageDirectory()
                                    .getAbsolutePath() + "/" + "DCIM" + "/" + "Gallary Locker" + "/" + folderName + "/" + files[i].name


                                val result = fileDBHelper.deleteFile(files[i].id)
                                savefile(File(files[i].newpath), unhidepath)
                            }

                        }


                        getFileList(folderName.toString())

                        //moveFile(path,fileName,rootPath)

                    }
                })
        dialog.show(supportFragmentManager, "ok")
    }

    private fun getFileList(folderName: String) {

        fileList = fileDBHelper.getAllFiles(folderName)
        //SelectedFileList = fileDBHelper.getAllFiles(folderName)
        setupRecyclerView()
        /* val f = File(Environment.getExternalStorageDirectory(), APPLICATON_FOLDER_NAME +"/" + folderName )
         val files = f.listFiles()
         for (inFile in files!!) {
            val size= Utility.calculateSize(inFile.length().toInt()/1024)
           //  fileList.add(FileListModal(inFile.name, size, inFile.absolutePath,"" ))

         }*/
    }

    override fun onItemLongClick(position: Int) {
        if (!isMultiSelect) {
            SelectedFileList = ArrayList<FileListModal>()
            isMultiSelect = true
            if (mActionMode == null) {
                toolbar2.invisible()
                mActionMode = startActionMode(mActionModeCallback)
            }
        }
        if (position > -1) {
            multi_select(position)
        }
    }


    override fun onItemSelect(position: Int, data: FileListModal) {
        if (isMultiSelect) multi_select(position)
        else {
            if (data.filetype.equals(Constant.VIDEO)) {
                val intent = Intent(this, FullScreenPlayerActivity::class.java)
                intent.putExtra(Constant.DATA, data.newpath)
                startActivity(intent)
            } else {
                val intent = Intent(this, FullscreenImageActivity::class.java)
                intent.putExtra(Constant.DATA, fileList)
                intent.putExtra(Constant.POSITION, position)
                startActivity(intent)
            }
        }


    }


    override fun onOptionItemSelect(position: Int, data: FileListModal, itemView: View) {
        var popup: PopupMenu? = null;
        popup = PopupMenu(this, itemView)
        popup.inflate(R.menu.poup_folder_detail)

        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->

            when (item!!.itemId) {
                R.id.delete -> {

                    AskOption(data)

                }
                R.id.rename -> {
                    renameFile(data)

                }
                R.id.unhide -> {
                    //savefile(Uri.fromFile(File(data.newpath)),data.originalpath, data.newpath,data.id)

                    unHideFile(data.originalpath, data.newpath, data.id, data.name)
                    // Toast.makeText(this, item.title, Toast.LENGTH_SHORT).show();


                }
            }

            true
        })

        popup.show()
    }


    private fun unHideFile(originalpath: String, newpath: String, id: String, fileName: String) {

        /* val folderDirectory = File(
             Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/" + fileName

         )*/

        /* val encryptedData = File(newpath).readBytes()
         val decryptedData = ImageEncryptDecrypt(Constant.MY_PASSWORD).decrypt(encryptedData)

             val fos = FileOutputStream(originalpath)
             fos.write(decryptedData)
             fos.close()*/


        var pathList = ArrayList<FileListModal>()
        pathList.add(
            FileListModal(
                id,
                "",
                "",
                originalpath,
                newpath,
                folderName.toString(),
                ""
            )
        )
        val folderDirectory = File(
            Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/" + "DCIM" + "/" + "Gallary Locker",
            folderName
        )
        if (!folderDirectory.exists()) {
            folderDirectory.mkdirs()
        }

        val unhidepath = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/" + "DCIM" + "/" + "Gallary Locker" + "/" + folderName + "/" + fileName


        val dialog = DialogUnhideOption
            .newInstance(
                this,
                pathList,
                folderName.toString(),
                object : DialogUnhideOption.OnItemClick {
                    override fun onItemCLicked(text: String,files: ArrayList<FileListModal>) {
                        val result = fileDBHelper.deleteFile(id)
                        getFileList(folderName.toString())
                        if (text.equals(Constant.ORIGINAL)) {
                            savefile(File(newpath), originalpath)
                        } else {
                            savefile(File(newpath), unhidepath)
                        }

                        //moveFile(path,fileName,rootPath)

                    }
                })
        dialog.show(supportFragmentManager, "ok")

        /* if (File(newpath).renameTo(File(originalpath))) {

             // Toast.makeText(this, "moved done...", Toast.LENGTH_SHORT).show();


             // Toast.makeText(this, "delete done", Toast.LENGTH_SHORT).show();
             //  EncriptDycript.delete(this, File(newpath))
             *//*    if (EncriptDycript.delete(this, File(newpath)))
              {

                }else{
                    Toast.makeText(this, "not done", Toast.LENGTH_SHORT).show();
                }*//*


            // adapter?.notifyDataSetChanged()
        } else {
            Toast.makeText(this, "Not moved", Toast.LENGTH_SHORT).show();
        }*/

    }

    fun savefile(sourceuri: File, Destination: String) {
        var bis: BufferedInputStream? = null
        var bos: BufferedOutputStream? = null
        val encryptedData = sourceuri.readBytes()
        val decryptedData = ImageEncryptDecrypt(Constant.MY_PASSWORD).decrypt(encryptedData)


        // ImageStorageManager.saveToInternalStorage(this,BitmapFactory.decodeByteArray(decryptedData, 0, decryptedData.size),Destination)
        try {
            bis = BufferedInputStream(ByteArrayInputStream(decryptedData))
            bos = BufferedOutputStream(FileOutputStream(Destination, false))
            val buf = ByteArray(1024)
            bis.read(buf)
            do {
                bos.write(buf)
            } while (bis.read(buf) != -1)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                bis?.close()
                bos?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        ImageEncryptDecrypt.insertFile(this, File(Destination))
        ImageEncryptDecrypt.delete(this, sourceuri)
    }

    private fun pickImageFromGallery() {
        ImagePicker.create(this)
            .returnAfterFirst(false) // set whether pick or camera action should return immediate result or not. For pick image only work on single mode
            .folderMode(true) // folder mode (false by default)
            .folderTitle("Folder") // folder selection title
            .imageTitle("Tap to select") // image selection title
            .multi() // single mode // max images can be selected (99 by default)
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
            images = ImagePicker.getImages(data) as java.util.ArrayList<Image>
            MyAsyncTask().execute(images)

        }
        if (requestCode == VIDEO_PICK_CODE) {
            if (MediaPickerActivity.getMediaItemSelected(data) != null) {
                val chosenVideo =
                    MediaPickerActivity.getMediaItemSelected(data).get(0)
                val f = File(chosenVideo.getPathOrigin(this))
                if (f.exists()) {
                    Log.d("original_Path=====", f.absolutePath)
                    //getFilePath(f.absolutePath, Constant.VIDEO)
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

                source.renameTo(destination)
                getFileList(folderName.toString())
                Log.d("rename===", result.toString())


            }

        }

        builder.setNegativeButton(android.R.string.cancel) { dialog, p1 ->
            dialog.cancel()
        }

        builder.show();
    }


    private fun runLayoutAnimation(recyclerView: RecyclerView) {
        val context = recyclerView.getContext()
        val controller =
            AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_slide_right)
        recyclerView.setLayoutAnimation(controller)
        recyclerView.getAdapter()?.notifyDataSetChanged()
        recyclerView.scheduleLayoutAnimation()
    }

    private fun AskOption(file: FileListModal) {
        val myQuittingDialogBox = AlertDialog.Builder(this)
            // set message, title, and icon
            .setTitle("Delete")
            .setMessage("Do you want to delete this file?")
            .setIcon(R.drawable.ic_delete_black_24dp)
            .setPositiveButton("Delete", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface, whichButton: Int) {
                    File(file.newpath).delete()
                    val result = fileDBHelper.deleteFile(file.id)
                    getFileList(folderName.toString())
                    dialog.dismiss()
                }
            })
            .setNegativeButton("cancel", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface, which: Int) {
                    dialog.dismiss()
                }
            })
            .create()
        myQuittingDialogBox.show()
    }

    // AsyncTask inner class
    inner class MyAsyncTask : AsyncTask<List<Image>, Int, String>() {

        private var result: String = "";

        override fun onPreExecute() {
            super.onPreExecute()
            //  progressBar.visibility = View.VISIBLE
        }

        override fun onProgressUpdate(vararg values: Int?) {
            super.onProgressUpdate(*values)
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            // progressBar.visibility = View.GONE
            getFileList(folderName.toString())
            //set result in textView

            images.clear()
        }

        override fun doInBackground(vararg params: List<Image>): String? {

            for (imagelist in images) {

                val newpath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/" + APPLICATON_FOLDER_NAME + "/" + folderName.toString() + "/"

                val encryptByte =
                    ImageEncryptDecrypt(Constant.MY_PASSWORD).encrypt(File(imagelist.path).readBytes())

                val fos = FileOutputStream(newpath + File(imagelist.path).name)
                fos.write(encryptByte)
                fos.close()

                val size = Utility.calculateSize(File(imagelist.path).length().toInt() / 1024)
                val result = fileDBHelper.insertFile(
                    FileListModal(
                        UUID.randomUUID().toString(),
                        File(imagelist.path).name,
                        size,
                        imagelist.path,
                        newpath + File(imagelist.path).name,
                        folderName.toString(),
                        Constant.IMAGE
                    )
                )

                ImageEncryptDecrypt.delete(this@FolderDetailActivity, File(imagelist.path))
                //moveFile(imagelist.path, fileName, newpath +fileName, folderName.toString(), Constant.IMAGE)
            }
            return result
        }
    }

    class ImageStorageManager {
        companion object {
            fun saveToInternalStorage(
                context: Context,
                bitmapImage: Bitmap,
                imageFileName: String
            ): String {
                context.openFileOutput(imageFileName, Context.MODE_PRIVATE).use { fos ->
                    bitmapImage.compress(Bitmap.CompressFormat.PNG, 25, fos)
                }
                return context.filesDir.absolutePath
            }

            fun getImageFromInternalStorage(context: Context, imageFileName: String): Bitmap? {
                val directory = context.filesDir
                val file = File(directory, imageFileName)
                return BitmapFactory.decodeStream(FileInputStream(file))
            }

            fun deleteImageFromInternalStorage(context: Context, imageFileName: String): Boolean {
                val dir = context.filesDir
                val file = File(dir, imageFileName)
                return file.delete()
            }
        }
    }

}