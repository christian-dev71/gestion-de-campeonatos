package com.example.gestiondecampeonatos.data.local.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import java.io.File
import java.io.IOException
import java.util.UUID

class LocalTeamImageStore(private val context: Context) : TeamImageStore {
    override fun importImage(uri: String): String {
        val directory = File(context.filesDir, DIRECTORY)
        if (!directory.exists() && !directory.mkdirs()) throw IOException("Cannot create image directory")
        val file = File(directory, "${UUID.randomUUID()}.png")
        val bitmap = decodeTeamImage(context, Uri.parse(uri), 512)
        try {
            file.outputStream().use { output ->
                if (!bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)) {
                    throw IOException("Cannot encode team image")
                }
            }
            return file.name
        } catch (error: Exception) {
            file.delete()
            throw error
        } finally {
            bitmap.recycle()
        }
    }

    override fun deleteImage(fileName: String) {
        File(File(context.filesDir, DIRECTORY), fileName).delete()
    }

    companion object {
        const val DIRECTORY = "team_images"

        fun imageUri(context: Context, fileName: String): Uri =
            Uri.fromFile(File(File(context.filesDir, DIRECTORY), fileName))
    }
}

/** Bounds decoding to thumbnail size and respects the source orientation. */
fun decodeTeamImage(context: Context, uri: Uri, maxSize: Int): Bitmap {
    val source = ImageDecoder.createSource(context.contentResolver, uri)
    return ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
        decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
        val scale = minOf(1.0, maxSize.toDouble() / maxOf(info.size.width, info.size.height))
        decoder.setTargetSize(
            (info.size.width * scale).toInt().coerceAtLeast(1),
            (info.size.height * scale).toInt().coerceAtLeast(1),
        )
    }
}
