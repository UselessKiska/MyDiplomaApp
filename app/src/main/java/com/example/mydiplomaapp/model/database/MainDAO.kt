package com.example.mydiplomaapp.model.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.mydiplomaapp.Const
import com.example.mydiplomaapp.model.entity.FileModel
import kotlinx.coroutines.flow.Flow

@Dao
interface MainDAO {

    @Insert
    suspend fun insertFile(file:FileModel)

    @Delete
    suspend fun deleteFile(file:FileModel)

    @Update
    suspend fun updateFile(file: FileModel)

    @Query("select * from ${Const.TABLE_NAME} where :name = fileName")
    suspend fun getFile(name:String): FileModel

    @Query("select * from ${Const.TABLE_NAME} ")
    fun getAllFiles(): Flow<List<FileModel>>

}