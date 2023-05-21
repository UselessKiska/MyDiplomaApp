package com.example.mydiplomaapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.mydiplomaapp.model.database.MainDAO
import com.example.mydiplomaapp.model.entity.FileModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class MainViewModel(private val dao:MainDAO):ViewModel() {

    var allFiles = dao.getAllFiles().asLiveData()

    var currentFile:MutableLiveData<FileModel>? = MutableLiveData()

    fun saveFile(file:FileModel){
        viewModelScope.launch(Dispatchers.IO) {
            dao.insertFile(file)
        }
    }

    fun deleteFile(file: FileModel){
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteFile(file)
        }
    }

    fun updateFile(file: FileModel){
        viewModelScope.launch(Dispatchers.IO) {
            dao.updateFile(file)
        }
    }

    fun getFile(name:String){
        viewModelScope.launch(Dispatchers.IO) {
            currentFile?.postValue(dao.getFile(name))
        }
    }

    class ViewModelFactory(private val dao: MainDAO):ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(MainViewModel::class.java)){
                return MainViewModel(dao) as T
            }
            else
                throw Exception("Unknown ViewModel class")
        }
    }
}