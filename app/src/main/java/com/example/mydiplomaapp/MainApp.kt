package com.example.mydiplomaapp

import android.app.Application
import com.example.mydiplomaapp.model.database.MainDB

class MainApp:Application(){
    val database by lazy { MainDB.getDB(this) }
}