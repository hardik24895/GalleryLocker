package com.gallarylock.modal

import java.io.Serializable

class FileListModal (val id:String,val name: String, val size:String, val originalpath:String, val newpath:String, val foldername:String, var filetype:String) : Serializable