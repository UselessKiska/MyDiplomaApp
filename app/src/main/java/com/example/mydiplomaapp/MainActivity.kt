package com.example.mydiplomaapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.webkit.MimeTypeMap
import android.widget.EditText
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.example.mydiplomaapp.databinding.ActivityMainBinding
import com.example.mydiplomaapp.model.entity.FileModel
import com.example.mydiplomaapp.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class MainActivity : AppCompatActivity() {

    private var binding:ActivityMainBinding? = null

    private var chosenFile: File? = null
    private var secretKey: SecretKey? = null
    private var iv: ByteArray? = null
    private var chosenFilePath:String? = null
    private var viewModel:MainViewModel? = null
    private var extension:String = ""

    private val filePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            chosenFilePath = uri.path
            contentResolver.openFileDescriptor(uri, "r")?.use { descriptor ->
                var fileName = ""
                val cursor = contentResolver.query(uri, null, null, null, null)
                cursor?.let {
                    it.moveToFirst()
                    fileName = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
                    cursor.close()
                }
                chosenFile = File(getExternalFilesDir(null), fileName)
                binding?.fileName?.text = chosenFile?.name

                try {
                    FileInputStream(descriptor.fileDescriptor).use { inputStream ->
                        FileOutputStream(chosenFile).use { outputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val viewModelFactory = MainViewModel.ViewModelFactory((applicationContext as MainApp).database.getDao())
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]

//        viewModel?.allFiles?.observe(this){
//            if(it!=null){
//                it.forEach {
//                    viewModel?.deleteFile(it)
//                }
//            }
//        }

        binding?.apply{
            btnChooseFile.setOnClickListener{
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    type = "*/*"
                    addCategory(Intent.CATEGORY_OPENABLE)
                }
                filePickerLauncher?.launch(intent.type)
            }

            btnEncrypt.setOnClickListener {
                if(chosenFile!=null) {
                    generateSecretKey()
                    extension = chosenFile?.extension.toString()
                    encryptFile(chosenFile!!)
                }
                else
                    Snackbar.make(it, "File to encrypt is empty",Snackbar.LENGTH_SHORT).show()
            }

            btnDecrypt.setOnClickListener{
                if(chosenFile!=null)
                    decryptFile(chosenFile!!)
                else
                    Snackbar.make(it, "File to decrypt is empty",Snackbar.LENGTH_SHORT).show()
            }

        }
    }

    private fun generateSecretKey() {
        // Generate AES key 256bit
        val keyGenerator = KeyGenerator.getInstance("AES")
        keyGenerator.init(256)
        secretKey = keyGenerator.generateKey()

        // Generate IV
        iv = ByteArray(16)
        SecureRandom().nextBytes(iv)
    }

    private fun encryptFile(file: File) {

        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, IvParameterSpec(iv))

        val storageDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)

        val encryptedData = cipher.doFinal(file.readBytes())
        val encryptedFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), file.nameWithoutExtension)

        try {
            encryptedFile.writeBytes(encryptedData)

            val newFile = File(chosenFilePath,file.nameWithoutExtension)

            Snackbar.make(binding?.root?.rootView!!, newFile.path, Snackbar.LENGTH_SHORT).show()

            //viewModel?.saveFile(FileModel(fileName =file.name, filePassword = "00", fileSecretKey = secretKey?.encoded!!, iv = this.iv!!))
            setBuilder(file)
        } catch (e: IOException) {
            Log.e("IOException", "Error writing encrypted file", e)
            Snackbar.make(binding?.root?.rootView!!, "Error writing encrypted file", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun decryptFile(file: File) {
        viewModel?.getFile(file.nameWithoutExtension)

        viewModel?.currentFile?.observe(this){
            if(it!=null){

                val builder = AlertDialog.Builder(this)

                builder.setTitle("Input Dialog")
                    .setMessage("Please enter password:")

                val inputEditText = EditText(this)
                builder.setView(inputEditText)

                builder.setPositiveButton("OK") { dialog, _ ->
                    val userInput = inputEditText.text.toString()
                    decrypt(file, userInput, it)
                    dialog.dismiss() // Dismiss the dialog
                }

                builder.setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss() // Dismiss the dialog
                }

                val alertDialog: AlertDialog = builder.create()
                alertDialog.show()
            }
            else{
                Snackbar.make(binding?.root?.rootView!!, "File does not exist app database",Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun decrypt(file: File, password:String,fileModel: FileModel) {
        if(password==fileModel.filePassword){
            Snackbar.make(binding?.root?.rootView!!, fileModel.fileName, Snackbar.LENGTH_SHORT).show()
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")

            val newSecretKey = SecretKeySpec(fileModel.fileSecretKey, "AES")

            cipher.init(Cipher.DECRYPT_MODE, newSecretKey, IvParameterSpec(fileModel.iv))

            val decryptedData = cipher.doFinal(file.readBytes())
            val decryptedFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "${fileModel.fileName}_1.${fileModel.extension}")

            try {
                decryptedFile.writeBytes(decryptedData)
                Snackbar.make(binding?.root?.rootView!!,decryptedFile.canonicalPath, Snackbar.LENGTH_SHORT).show()
                viewModel?.deleteFile(fileModel)
            } catch (e: IOException) {
                Log.e("IOException", "Error writing decrypted file", e)
                Snackbar.make(binding?.root?.rootView!!, "Error writing decrypted file", Snackbar.LENGTH_SHORT).show()
            }
        }
        else{
            Snackbar.make(binding?.root?.rootView!!, "Wrong password", Snackbar.LENGTH_SHORT).show()
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.app_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when(id){
            R.id.pin_menu_button->{
                startActivity(Intent(this,PinCodeActivity::class.java))
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun setBuilder(file: File){
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Input Dialog")
            .setMessage("Please password:")

        val inputEditText = EditText(this)
        builder.setView(inputEditText)

        builder.setPositiveButton("OK") { dialog, _ ->
            val userInput = inputEditText.text.toString()
            viewModel?.saveFile(FileModel(fileName =file.nameWithoutExtension, filePassword = userInput, fileSecretKey = secretKey?.encoded!!, iv = this.iv!!, extension = file.extension))
            Snackbar.make(binding?.root?.rootView!!, "File saved in data",Snackbar.LENGTH_SHORT).show()
            dialog.dismiss() // Dismiss the dialog
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }
}