package com.example.mydiplomaapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.mydiplomaapp.databinding.ActivityFirstPageBinding
import com.google.android.material.snackbar.Snackbar

class FirstPageActivity : AppCompatActivity() {

    private var myPreferences:SharedPreferences? = null
    private var editor:SharedPreferences.Editor? = null

    private var binding:ActivityFirstPageBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFirstPageBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        myPreferences = getSharedPreferences(Const.PREFERENCE_NAME, Context.MODE_PRIVATE)

        editor = myPreferences?.edit()

    }

    override fun onResume() {
        super.onResume()

        val isCodeSaved = myPreferences?.getBoolean(Const.IS_PREFERENCE_CODE_SAVED, false)

        if(isCodeSaved!!){
            val code = myPreferences?.getInt(Const.PREFERENCE_CODE, 0)
            Log.d(Const.PREFERENCE_CODE, code.toString())

            binding?.enterApp?.setOnClickListener {

                val newCode = binding?.firstPlace?.text.toString()+
                        binding?.secondPlace?.text.toString()+
                        binding?.thirdPlace?.text.toString()+
                        binding?.fourthPlace?.text.toString()

                if(newCode.toInt() == code){
                    enterTheApp()
                }
                else
                    Snackbar.make(it, "Wrong code code is $code",Snackbar.LENGTH_SHORT ).show()
            }
        }
        else
            enterTheApp()

    }

    override fun onDestroy() {
        super.onDestroy()

        binding = null
    }

    private fun enterTheApp(){
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}