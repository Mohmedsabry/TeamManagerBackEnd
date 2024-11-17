package com.example.util

import io.ktor.http.content.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

suspend fun PartData.FileItem.save(name: String): File {
    val file = File("images/$name")
    withContext(Dispatchers.IO) {
        file.createNewFile()
    }
    val byte = this.provider().toByteArray()
    file.writeBytes(byte)
    return file
}