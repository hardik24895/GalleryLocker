package com.gallarylock.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.media.ThumbnailUtils
import android.os.*
import android.os.Environment.getExternalStorageDirectory
import android.provider.MediaStore
import android.util.Log
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

import com.gallarylock.Utility.showToast
import com.gallarylock.adapter.FolderListAdapter
import com.gallarylock.dialog.DialogFolderSelection
import com.gallarylock.modal.FolderListModal
import com.gallarylock.utility.Constant.APPLICATON_FOLDER_NAME
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.model.Image
import com.gallarylock.R
import com.gallarylock.SessionManager
import com.gallarylock.Utility
import com.gallarylock.Utility.copyDataBseFromExternal
import com.gallarylock.Utility.copyDataBseToExternal
import com.gallarylock.database.FileDBHelper
import com.gallarylock.database.FolderDBHelper
import com.gallarylock.modal.FileListModal
import com.gallarylock.utility.Constant
import com.gallarylock.utility.Constant.DB_NAME
import com.gallarylock.utility.Constant.defualtDbFile
import com.gallarylock.utility.Constant.defualtDbFileShm
import com.gallarylock.utility.Constant.defualtDbFileWal
import com.gallarylock.utility.Constant.sdDatabsePath
import com.gallarylock.utility.Constant.sdbackupDBPath
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*
import vn.tungdx.mediapicker.MediaItem
import vn.tungdx.mediapicker.MediaOptions
import vn.tungdx.mediapicker.activities.MediaPickerActivity
import java.io.*
import java.util.*
import javax.mail.Header
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(), FolderListAdapter.OnItemSelected {

    var progress: ProgressDialog? = null
    private var adapter: FolderListAdapter? = null
    private var mediaItem = java.util.ArrayList<MediaItem>()
    lateinit var folderDBHelper: FolderDBHelper

    lateinit var fileDBHelper: FileDBHelper
    private var images = java.util.ArrayList<com.esafirm.imagepicker.model.Image>()

    companion object {
        //image pick code
        private val IMAGE_PICK_CODE = 1000;

        //VIDEO pick code
        private val VIDEO_PICK_CODE = 2000;


        //Permission code
        private val PERMISSION_REQUEST = 1001

        //Permission code
        private val PERMISSION_REQUEST_VIDEO = 1002
    }

    lateinit var session: SessionManager
    var isFABOpen: Boolean = false
    var folderlist = ArrayList<FolderListModal>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        folderDBHelper = FolderDBHelper(this)
        session = SessionManager(this)
        fileDBHelper = FileDBHelper(this)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_REQUEST
            )
        } else {
            createApplicationFolder()
            getListOfFolder()
        }

        fab.setOnClickListener { view ->
            // pickImageFromGallery()
            //  showCreateCategoryDialog()
            if (!isFABOpen) {
                showFABMenu();
            } else {
                closeFABMenu();
            }
        }

        fab1.setOnClickListener {
            val builder = MediaOptions.Builder()
            var options: MediaOptions? = null
            options = builder.selectVideo().canSelectMultiVideo(true).build()
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

        imgNewFolder.setOnClickListener {
            createNewFolder()
        }

        imagSetting.setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }
        syncDataOnInstall()
        progress = ProgressDialog(this);
        progress!!.setTitle("Loading");
        progress!!.setMessage("Wait while loading...");
        progress!!.setCancelable(false); // disable dismiss by tapping outside of the dialog

    }

    private fun syncData() {

        copyDataBseToExternal(
            defualtDbFile.absolutePath,
            DB_NAME,
            sdDatabsePath.absolutePath
        )

        if (defualtDbFileShm.exists()) {
            copyDataBseToExternal(
                defualtDbFileShm.absolutePath,
                DB_NAME,
                sdDatabsePath.absolutePath
            )
        }
        if (defualtDbFileWal.exists()) {
            copyDataBseToExternal(
                defualtDbFileWal.absolutePath,
                DB_NAME,
                sdDatabsePath.absolutePath
            )
        }
    }

    private fun syncDataOnInstall() {


        Handler().postDelayed(Runnable {

            if (sdbackupDBPath.exists()) {
                // currentDB.mkdir()
                copyDataBseFromExternal(
                    sdbackupDBPath.absolutePath,
                    DB_NAME,
                    defualtDbFile.absolutePath
                )
                if (defualtDbFileShm.exists()) {
                    copyDataBseFromExternal(
                        sdbackupDBPath.absolutePath,
                        DB_NAME,
                        defualtDbFileShm.absolutePath
                    )
                }
                if (defualtDbFileWal.exists()) {
                    copyDataBseFromExternal(
                        sdbackupDBPath.absolutePath,
                        DB_NAME,
                        defualtDbFileWal.absolutePath
                    )
                }
            } else {
                copyDataBseToExternal(
                    defualtDbFile.absolutePath,
                    DB_NAME,
                    sdDatabsePath.absolutePath
                )
                if (defualtDbFileShm.exists()) {
                    copyDataBseToExternal(
                        defualtDbFileShm.absolutePath,
                        DB_NAME,
                        sdDatabsePath.absolutePath
                    )
                }
                if (defualtDbFileWal.exists()) {
                    copyDataBseToExternal(
                        defualtDbFileWal.absolutePath,
                        DB_NAME,
                        sdDatabsePath.absolutePath
                    )
                }
            }
            getListOfFolder()
        }, 1000);


    }


    override fun onItemSelect(position: Int, data: FolderListModal) {

        val intent = Intent(this, FolderDetailActivity::class.java)
        intent.putExtra(Constant.DATA, data.name)
        startActivity(intent)
        // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onOptionItemSelect(position: Int, data: FolderListModal, imgOption: View) {
        var popup: PopupMenu? = null;
        popup = PopupMenu(this, imgOption)
        popup.inflate(R.menu.poup_home)

        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->

            when (item!!.itemId) {
                R.id.delete -> {
                    AskOption(data)
                }
                R.id.rename -> {
                    // renameFolder(data)
                    Toast.makeText(this@MainActivity, "Coming soon", Toast.LENGTH_SHORT).show();
                }

            }

            true
        })

        popup.show()
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        adapter = FolderListAdapter(folderlist, this, this, fileDBHelper)
        recyclerView.adapter = adapter
        runLayoutAnimation(recyclerView)

    }

    private fun getListOfFolder() {
        folderlist = folderDBHelper.getAllFOlder()
        adapter?.notifyDataSetChanged()
        setupRecyclerView()
    }

    private fun createApplicationFolder() {
        val parentDirectory = File(getExternalStorageDirectory(), APPLICATON_FOLDER_NAME)
        if (!parentDirectory.exists()) {
            parentDirectory.mkdirs()
            val folderDirectory = File(
                getExternalStorageDirectory().getAbsolutePath() + "/" + APPLICATON_FOLDER_NAME,
                "New Folder"
            )
            if (!folderDirectory.exists()) {
                folderDirectory.mkdirs()
                val result = folderDBHelper.insertFolder(
                    FolderListModal(
                        UUID.randomUUID().toString(),
                        "New Folder",
                        "0"
                    )
                )
                Log.d("newfolder add=====", result.toString())
                getListOfFolder()
            }
        } else {
            val folderDirectory = File(
                getExternalStorageDirectory().getAbsolutePath() + "/" + APPLICATON_FOLDER_NAME,
                "New Folder"
            )
            if (!folderDirectory.exists()) {
                folderDirectory.mkdirs()
                val result = folderDBHelper.insertFolder(
                    FolderListModal(
                        UUID.randomUUID().toString(),
                        "New Folder",
                        "0"
                    )
                )
                Log.d("newfolder add=====", result.toString())
                getListOfFolder()
            }
            getListOfFolder()
        }


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings ->

                true
            else -> super.onOptionsItemSelected(item)
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

    private fun createNewFolder(name: String) {
        val folderDirectory = File(
            getExternalStorageDirectory().getAbsolutePath() + "/" + APPLICATON_FOLDER_NAME,
            name
        )
        if (!folderDirectory.exists()) {
            folderDirectory.mkdirs()
            folderlist.clear()
            adapter?.notifyDataSetChanged()
            val result = folderDBHelper.insertFolder(
                FolderListModal(
                    UUID.randomUUID().toString(),
                    name,
                    "0"
                )
            )
            getListOfFolder()
            syncData()
            Log.d("inserted=====", result.toString())
        } else {
            showToast("Already exist")
        }

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


    @Throws(IOException::class)

    //handle result of picked image
    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            images = ImagePicker.getImages(data) as java.util.ArrayList<Image>

            showRadioButtonDialog(false)


        }
        if (requestCode == VIDEO_PICK_CODE) {
            if (MediaPickerActivity.getMediaItemSelected(data) != null) {
                mediaItem = MediaPickerActivity.getMediaItemSelected(data) as java.util.ArrayList<MediaItem>

                showRadioButtonDialog(true)

                //  val f = File(mediaItem.getPathOrigin(this))
                /* if (f.exists()) {
                     Log.d("original_Path=====", f.absolutePath)
                     //getFilePath(f.absolutePath, Constant.VIDEO)
                 } else {
                     Toast.makeText(
                         this,
                         "File not found",
                         Toast.LENGTH_SHORT
                     ).show()
                 }*/
            }

        }
        /*if (requestCode == VIDEO_PICK_CODE) {
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

        }*/
        /*if (requestCode == ImagePicker.IMAGE_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            val path= data?.getStringArrayListExtra(ImagePicker.EXTRA_IMAGE_PATH);
            getFilePath(path?.get(0).toString())
            //Your Code
            Log.d("Image_path",path?.get(0).toString())
       }

       if (requestCode == VideoPicker.VIDEO_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
       val path =  data?.getStringArrayListExtra(VideoPicker.EXTRA_VIDEO_PATH)
        Log.d("Video_path",path?.get(0).toString())
           getFilePath(path?.get(0).toString())
           //Your Code
       }*/
        if (resultCode == Activity.RESULT_OK && requestCode == PERMISSION_REQUEST_VIDEO) {
            /* val chosenVideo =
                 MediaPickerActivity.getMediaItemSelected(data)[0]
             val f = File(chosenVideo.getPathOrigin(this))
             if (f.exists()) {
                 getFilePath(f.absolutePath)
             }*/
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            createApplicationFolder()
            //getListOfFolder()
        } else {

            showToast("Permission denied!")
        }
    }

    fun createNewFolder() {
        val context = this
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Create New Folder")
        val view = layoutInflater.inflate(R.layout.dialog_create_folder, null)

        val categoryEditText = view.findViewById(R.id.categoryEditText) as EditText

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
                createNewFolder(categoryEditText.text.toString())
            }

        }

        builder.setNegativeButton(android.R.string.cancel) { dialog, p1 ->
            dialog.cancel()
        }

        builder.show();
    }

    fun renameFolder(folder: FolderListModal) {
        val context = this
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Rename Folder")
        val view = layoutInflater.inflate(R.layout.dialog_create_folder, null)

        val categoryEditText = view.findViewById(R.id.categoryEditText) as EditText
        categoryEditText.setText(folder.name)
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
                for (inFile in folderlist) {
                    if (!inFile.name.equals(newCategory)) {
                        val result = folderDBHelper.renameFolder(
                            FolderListModal(
                                folder.id,
                                newCategory.toString(),
                                "0"
                            )
                        )
                        val source = File(
                            getExternalStorageDirectory().getAbsolutePath() + "/" + APPLICATON_FOLDER_NAME,
                            folder.name
                        )
                        val destination = File(
                            getExternalStorageDirectory().getAbsolutePath() + "/" + APPLICATON_FOLDER_NAME,
                            newCategory.toString()
                        )
                        source.renameTo(destination)
                        syncData()
                        getListOfFolder()

                        Log.d("rename===", result.toString())
                    }
                }

            }

        }

        builder.setNegativeButton(android.R.string.cancel) { dialog, p1 ->
            dialog.cancel()
        }

        builder.show();
    }

    private fun showRadioButtonDialog(isVideo:Boolean) {
        val dialog = DialogFolderSelection
            .newInstance(
                this,
                folderlist,
                object : DialogFolderSelection.OnItemClick {
                    override fun onItemCLicked(foldername: RadioButton) {
                        if(isVideo){
                            MyAsyncTaskVideo().execute(foldername.text.toString())
                        }else{
                            MyTask().execute(foldername.text.toString())
                        }

                    }
                })
        dialog.show(supportFragmentManager, "ok")
    }


    override fun onResume() {
        super.onResume()
        getListOfFolder()
    }

    private fun runLayoutAnimation(recyclerView: RecyclerView) {
        val context = recyclerView.getContext()
        val controller =
            AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down)
        recyclerView.setLayoutAnimation(controller)
        recyclerView.getAdapter()?.notifyDataSetChanged()
        recyclerView.scheduleLayoutAnimation()
    }

    private fun AskOption(file: FolderListModal) {
        val myQuittingDialogBox = AlertDialog.Builder(this)
            // set message, title, and icon
            .setTitle("Delete")
            .setMessage("Do you want to delete this folder?")
            .setIcon(R.drawable.ic_delete_black_24dp)
            .setPositiveButton("Delete", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface, whichButton: Int) {
                    //your deleting code
                    val result = folderDBHelper.deleteFolder(file.id)
                    if (result) {
                        val folderDirectory = File(
                            getExternalStorageDirectory().getAbsolutePath() + "/" + APPLICATON_FOLDER_NAME,
                            file.name
                        )

                        if (folderDirectory.exists()) {
                            if (folderDirectory.isDirectory()) {
                                val children = folderDirectory.list()
                                for (i in children.indices) {
                                    File(folderDirectory, children[i]).delete()
                                }
                            }

                            folderDirectory.delete()
                            ImageEncryptDecrypt.delete(this@MainActivity, folderDirectory)
                            folderDBHelper.deleteFolder(file.id)
                            syncData()
                        }

                    }

                    getListOfFolder()
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


    inner class MyTask : AsyncTask<String, Int, String>() {
        private var result: String = "";

        override fun doInBackground(vararg folderName: String): String {
            // do something in background
            for (imagelist in images) {

                val folderName1 = folderName.get(0)
                val newpath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/" + APPLICATON_FOLDER_NAME + "/" + folderName1.toString() + "/"

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
                        folderName1,
                        Constant.IMAGE
                    )
                )
                syncData()
                ImageEncryptDecrypt.delete(this@MainActivity, File(imagelist.path))
                //moveFile(imagelist.path, fileName, newpath +fileName, folderName.toString(), Constant.IMAGE)
            }
            return result
        }

        override fun onPreExecute() {
            super.onPreExecute()
            progress?.show();
            // do something before start
        }


        override fun onPostExecute(result: String) {

            super.onPostExecute(result)
            images.clear()
            getListOfFolder()
            progress?.dismiss()
            // do something after execution
        }
    }
    // AsyncTask inner class

    inner class MyAsyncTaskVideo : AsyncTask<String, Int, String>() {
        private var result: String = "";

        override fun doInBackground(vararg folderName1: String): String {
            // do something in background
            for (imagelist in mediaItem) {
                val folderName = folderName1.get(0)
                val newpath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/" + APPLICATON_FOLDER_NAME + "/" + folderName.toString() + "/"

                val encryptByte =
                    ImageEncryptDecrypt(Constant.MY_PASSWORD).encrypt(File(imagelist.getPathOrigin(this@MainActivity)).readBytes())

                val fos = FileOutputStream(newpath + File(imagelist.getPathOrigin(this@MainActivity)).name)
                fos.write(encryptByte)
                fos.close()

                // val  thumnail =Utility.getThumbnailPathForLocalFile(this@FolderDetailActivity, File(imagelist.getPathOrigin(baseContext)).toUri())

                val thumb = ThumbnailUtils.createVideoThumbnail(imagelist.getPathOrigin(this@MainActivity), MediaStore.Video.Thumbnails.MINI_KIND);
                var encoded = ImageEncryptDecrypt.encodeFromString(thumb)
                val size = Utility.calculateSize(File(imagelist.getPathOrigin(this@MainActivity)).length().toInt() / 1024)
                val result = fileDBHelper.insertFile(
                    FileListModal(
                        UUID.randomUUID().toString(),
                        File(imagelist.getPathOrigin(this@MainActivity)).name,
                        size,
                        imagelist.getPathOrigin(this@MainActivity),
                        newpath + File(imagelist.getPathOrigin(this@MainActivity)).name,
                        folderName.toString(),
                        encoded
                    )
                )

                ImageEncryptDecrypt.delete(this@MainActivity, File(imagelist.getPathOrigin(this@MainActivity)))
                //moveFile(imagelist.path, fileName, newpath +fileName, folderName.toString(), Constant.IMAGE)
            }
            return result
        }

        override fun onPreExecute() {
            super.onPreExecute()
            progress?.show();
            // do something before start
        }


        override fun onPostExecute(result: String) {

            super.onPostExecute(result)
            progress?.dismiss()
            mediaItem.clear()
            getListOfFolder()

            // do something after execution
        }
    }

    /*inner class MyAsyncTaskVideo : AsyncTask<List<MediaItem>, Int, String>() {

        private var result: String = "";

        override fun onPreExecute() {
            super.onPreExecute()
            progress?.show();
        }

        override fun onProgressUpdate(vararg values: Int?) {
            Log.e("values", values.iterator().toString())
            super.onProgressUpdate(*values)
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            getListOfFolder()
            progress?.dismiss();
            mediaItem.clear()
            Utility.copyDataBseToExternal(
                defualtDbFile.absolutePath,
                DB_NAME,
                sdDatabsePath.absolutePath
            )
        }

        override fun doInBackground(vararg params: List<MediaItem>): String? {

            for (imagelist in mediaItem) {

                val newpath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/" + APPLICATON_FOLDER_NAME + "/" + folderName.toString() + "/"

                val encryptByte =
                    ImageEncryptDecrypt(Constant.MY_PASSWORD).encrypt(File(imagelist.getPathOrigin(this@FolderDetailActivity)).readBytes())

                val fos = FileOutputStream(newpath + File(imagelist.getPathOrigin(this@FolderDetailActivity)).name)
                fos.write(encryptByte)
                fos.close()

                // val  thumnail =Utility.getThumbnailPathForLocalFile(this@FolderDetailActivity, File(imagelist.getPathOrigin(baseContext)).toUri())

                val thumb = ThumbnailUtils.createVideoThumbnail(imagelist.getPathOrigin(this@FolderDetailActivity), MediaStore.Video.Thumbnails.MINI_KIND);
                var encoded = ImageEncryptDecrypt.encodeFromString(thumb)
                val size = Utility.calculateSize(File(imagelist.getPathOrigin(this@FolderDetailActivity)).length().toInt() / 1024)
                val result = fileDBHelper.insertFile(
                    FileListModal(
                        UUID.randomUUID().toString(),
                        File(imagelist.getPathOrigin(this@FolderDetailActivity)).name,
                        size,
                        imagelist.getPathOrigin(this@FolderDetailActivity),
                        newpath + File(imagelist.getPathOrigin(this@FolderDetailActivity)).name,
                        folderName.toString(),
                        encoded
                    )
                )

                ImageEncryptDecrypt.delete(this@FolderDetailActivity, File(imagelist.getPathOrigin(baseContext)))
                //moveFile(imagelist.path, fileName, newpath +fileName, folderName.toString(), Constant.IMAGE)
            }
            return result
        }
    }*/
}
