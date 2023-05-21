package com.example.mydiplomaapp.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mydiplomaapp.Const
import javax.crypto.SecretKey

@Entity(tableName = Const.TABLE_NAME)

data class FileModel(
    @PrimaryKey(autoGenerate = true)
    val id:Long? = null,
    val fileName:String,
    val filePassword:String,
    val fileSecretKey: ByteArray,
    val iv: ByteArray,
    val extension:String,
)
