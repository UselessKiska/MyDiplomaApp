package com.example.mydiplomaapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.OpenableColumns
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.webkit.MimeTypeMap
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.mydiplomaapp.databinding.ActivityMainBinding
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

class MainActivity : AppCompatActivity() {

    private var binding:ActivityMainBinding? = null

    private var chosenFile: File? = null
    private lateinit var secretKey: SecretKey
    private lateinit var iv: ByteArray

    private var extension:String = ""

    private val filePickerLauncher2 = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            contentResolver.openFileDescriptor(uri, "r")?.use { descriptor ->
                var fileName = ""
                val cursor = contentResolver.query(uri, null, null, null, null)
                cursor?.let {
                    it.moveToFirst()
                    fileName = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
                    cursor.close()
                }
                chosenFile = File(cacheDir, fileName)
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
        binding?.apply{
            btnChooseFile.setOnClickListener{
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    type = "*/*"
                    addCategory(Intent.CATEGORY_OPENABLE)
                }
                filePickerLauncher2?.launch(intent.type)
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
        val encryptedFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "encryptedFile")

        try {
            encryptedFile.writeBytes(encryptedData)
            Snackbar.make(binding?.root?.rootView!!, "Encrypted file saved to ${encryptedFile.path}", Snackbar.LENGTH_SHORT).show()
        } catch (e: IOException) {
            Log.e("IOException", "Error writing encrypted file", e)
            Snackbar.make(binding?.root?.rootView!!, "Error writing encrypted file", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun decryptFile(file: File) {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(iv))

        val decryptedData = cipher.doFinal(file.readBytes())
        val decryptedFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "decryptedFile.$extension")

        try {
            decryptedFile.writeBytes(decryptedData)
            Snackbar.make(binding?.root?.rootView!!,decryptedFile.canonicalPath, Snackbar.LENGTH_SHORT).show()
        } catch (e: IOException) {
            Log.e("IOException", "Error writing decrypted file", e)
            Snackbar.make(binding?.root?.rootView!!, "Error writing decrypted file", Snackbar.LENGTH_SHORT).show()
        }
    }

//    override fun onUserLeaveHint() {
//        super.onUserLeaveHint()
//
//        if(isFinishing)
//            return
//        finishAffinity()
//    }
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
}