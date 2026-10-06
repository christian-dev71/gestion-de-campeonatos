package com.example.gestiondecampeonatos.data.local.image

interface TeamImageStore {
    /** Imports an image and returns its relative filename. Called on an IO dispatcher. */
    fun importImage(uri: String): String
    fun deleteImage(fileName: String)
}
