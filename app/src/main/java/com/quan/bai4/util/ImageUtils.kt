package com.quan.bai4.util

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object ImageUtils {
    /**
     * Copies an image from a content URI to app internal storage (context.filesDir/avatars)
     * and returns the local file absolute path.
     */
    fun saveImageToInternalStorage(context: Context, uri: Uri): String? {
        return try {
            val contentResolver = context.contentResolver
            val avatarDir = File(context.filesDir, "avatars").apply {
                if (!exists()) {
                    mkdirs()
                }
            }

            val fileName = "avatar_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(8)}.jpg"
            val destinationFile = File(avatarDir, fileName)

            contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(destinationFile).use { output ->
                    input.copyTo(output)
                }
            } ?: return null

            destinationFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
