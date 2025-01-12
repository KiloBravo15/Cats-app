package com.am.catapp.utils

import com.am.catapp.models.Cat
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class CatSerialization {

    fun serializeCat(cat: Cat): ByteArray {
        val outputStream = ByteArrayOutputStream()
        val objectOutputStream = ObjectOutputStream(outputStream)
        objectOutputStream.writeObject(cat)
        objectOutputStream.close()
        return outputStream.toByteArray()
    }

    fun deserializeCat(byteArray: ByteArray?): Cat? {
        byteArray?.let {
            val inputStream = ByteArrayInputStream(it)
            val objectInputStream = ObjectInputStream(inputStream)
            val cat = objectInputStream.readObject() as? Cat
            objectInputStream.close()
            return cat
        }
        return null
    }
}