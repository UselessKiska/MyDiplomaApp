package com.example.mydiplomaapp.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mydiplomaapp.Const
import com.example.mydiplomaapp.model.entity.FileModel

@Database(entities = [FileModel::class], version = 1)

abstract class MainDB():RoomDatabase(){
    abstract fun getDao(): MainDAO

    companion object{
        @Volatile
        private var INSTANCE:MainDB?=null

        fun getDB(context: Context):MainDB{
            if(INSTANCE==null){
                synchronized(this){
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        MainDB::class.java,
                        Const.DB_NAME
                    ).build()
                }
            }
            return INSTANCE!!
        }
    }
}