package com.agrocareai.mobile.utils

import android.content.Context
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FileUtils {

    fun saveBitmapToInternalStorage(context: Context, bitmap: Bitmap): String {
        // Create a unique filename based on time
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "IMG_$timeStamp.jpg"

        // Get the directory (private app storage)
        val directory = context.filesDir
        val file = File(directory, fileName)

        // Save the file
        FileOutputStream(file).use { stream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
        }

        // Return the absolute path to save in the Database
        return file.absolutePath
    }
}