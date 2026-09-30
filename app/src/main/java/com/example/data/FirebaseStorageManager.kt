package com.example.data

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import kotlinx.coroutines.tasks.await
import java.util.UUID

object FirebaseStorageManager {
    private const val TAG = "FirebaseStorageManager"
    private val storage: FirebaseStorage
        get() = FirebaseStorage.getInstance()

    /**
     * Upload ByteArray directly to Firebase Storage bucket (modol-connect.firebasestorage.app)
     * and returns the public download URL.
     */
    suspend fun uploadBytes(
        bytes: ByteArray,
        folder: String = "uploads",
        extension: String = "jpg",
        mimeType: String = "image/jpeg"
    ): Result<String> {
        return try {
            val fileName = "${System.currentTimeMillis()}_${UUID.randomUUID()}.$extension"
            val storageRef = storage.reference.child("$folder/$fileName")

            val metadata = StorageMetadata.Builder()
                .setContentType(mimeType)
                .build()

            storageRef.putBytes(bytes, metadata).await()
            val downloadUrl = storageRef.downloadUrl.await().toString()
            Log.d(TAG, "Uploaded to Firebase Storage: $downloadUrl")
            Result.success(downloadUrl)
        } catch (e: Exception) {
            Log.e(TAG, "Failed uploading bytes to Firebase Storage", e)
            Result.failure(e)
        }
    }

    /**
     * Upload Android image Uri directly to Firebase Storage.
     */
    suspend fun uploadUri(
        context: Context,
        uri: Uri,
        folder: String = "receipts"
    ): Result<String> {
        return try {
            val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                ?: return Result.failure(IllegalArgumentException("Could not read file from URI"))

            val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"
            val extension = if (mimeType.contains("png")) "png" else "jpg"

            uploadBytes(bytes, folder, extension, mimeType)
        } catch (e: Exception) {
            Log.e(TAG, "Failed uploading URI to Firebase Storage", e)
            Result.failure(e)
        }
    }
}
