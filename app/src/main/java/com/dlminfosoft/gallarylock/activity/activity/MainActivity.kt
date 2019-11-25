package com.dlminfosoft.gallarylock.activity.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dlminfosoft.gallarylock.R

import kotlinx.android.synthetic.main.activity_main.*
import android.content.pm.PackageManager
import android.os.Build

import androidx.core.app.ActivityCompat
import com.dlminfosoft.gallarylock.activity.Utility.showToast
import android.os.Environment.getExternalStorageDirectory
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import com.dlminfosoft.gallarylock.activity.adapter.FolderListAdapter
import com.dlminfosoft.gallarylock.activity.modal.FolderListModal

import android.util.Log
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.model.Image

import kotlinx.android.synthetic.main.toolbar.*
import java.io.*
import android.widget.RadioButton

import android.widget.RadioGroup

import android.app.Dialog
import android.os.Environment
import android.view.*
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import com.dlminfosoft.gallarylock.activity.dialog.DialogFolderSelection
import kotlinx.android.synthetic.main.dialog_radio_btn.*
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths


class MainActivity : AppCompatActivity(), FolderListAdapter.OnItemSelected {


    private var adapter: FolderListAdapter? = null

    companion object {
        //image pick code
        private val IMAGE_PICK_CODE = 1000;
        //Permission code
        private val PERMISSION_REQUEST = 1001
    }

    var isFABOpen: Boolean = false
    val folderlist = ArrayList<FolderListModal>()
    private val images = java.util.ArrayList<Image>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        fab2.setOnClickListener {
            pickImageFromGallery()
            closeFABMenu()
        }

        imgNewFolder.setOnClickListener {
            createNewFolder()
        }
    }

    override fun onItemSelect(position: Int, data: FolderListModal) {

        val intent = Intent(this, FolderDetailActivity::class.java)
        intent.putExtra("name", data.name)
        startActivity(intent)
        // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        adapter = FolderListAdapter(folderlist, this, this)
        recyclerView.adapter = adapter
    }

    private fun getListOfFolder() {
        val f = File(getExternalStorageDirectory(), "Gallary Locker")
        val files = f.listFiles()
        for (inFile in files!!) {
            if (inFile.isDirectory()) {
                folderlist.add(FolderListModal(inFile.name, 0))
            }
        }
        setupRecyclerView()
    }

    private fun createApplicationFolder() {
        val parentDirectory = File(getExternalStorageDirectory(), "Gallary Locker")
        if (!parentDirectory.exists()) {
            parentDirectory.mkdirs()
            val folderDirectory = File(
                getExternalStorageDirectory().getAbsolutePath() + "/" + "Gallary Locker",
                "New Folder"
            )
            if (!folderDirectory.exists()) {
                folderDirectory.mkdirs()
                getListOfFolder()
            }
        } else {
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
            getExternalStorageDirectory().getAbsolutePath() + "/" + "Gallary Locker",
            name
        )
        if (!folderDirectory.exists()) {
            folderDirectory.mkdirs()
            folderlist.clear()
            adapter?.notifyDataSetChanged()
            getListOfFolder()
        } else {
            showToast("Already exist")
        }

    }

    private fun pickImageFromGallery() {
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

    }

    private fun getFilePath(path: String) {
        showRadioButtonDialog(path)

    }

    private fun moveFile(inputPath: String, inputFile: String, outputPath: String) {


       var path:Path =Files.move(Paths.get(inputFile),Paths.get(outputPath + inputFile + ".hide"))
        var `in`: InputStream? = null
        var out: OutputStream? = null
        try {
            //create output directory if it doesn't exist
            val dir = File(outputPath)
            if (!dir.exists()) {
                dir.mkdirs()
            }
            `in` = FileInputStream(inputPath)
            out = FileOutputStream(outputPath + inputFile + ".hide")
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

            adapter?.notifyDataSetChanged()
            // delete the original file
            File(inputPath).delete()
        } catch (fnfe1: FileNotFoundException) {
            Log.e("tag", "error")
        } catch (e: Exception) {
            Log.e("tag", e.message)
        }
    }

    @Throws(IOException::class)

    //handle result of picked image
    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            val images = ImagePicker.getImages(data) as java.util.ArrayList<Image>
            val sb = StringBuilder()
            var i = 0
            val l = images.size
            while (i < l) {
                sb.append(images[i].path)
                i++
            }
            getFilePath(sb.trim().toString())


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

    private fun showRadioButtonDialog(path: String) {
        val dialog = DialogFolderSelection
            .newInstance(
                this,
                folderlist,
                object : DialogFolderSelection.OnItemClick {
                    override fun onItemCLicked(text: RadioButton) {
                        var fileName: String = path.substring(path.lastIndexOf("/") + 1);
                        val rootPath = Environment.getExternalStorageDirectory()
                            .getAbsolutePath() + "/Gallary Locker/" + text.text + "/"
                        //moveFile(path,fileName,rootPath)
                        moveFile(path, fileName, rootPath)
                    }
                })
        dialog.show(supportFragmentManager, "ok")
    }
}
