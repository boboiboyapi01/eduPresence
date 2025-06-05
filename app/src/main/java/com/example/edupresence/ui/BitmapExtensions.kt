package com.example.edupresence.ui

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream

// Fungsi ekstensi untuk mengubah Bitmap menjadi ByteArray
fun Bitmap.toByteArray(): ByteArray {
    val stream = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.JPEG, 100, stream)
    return stream.toByteArray()
}
