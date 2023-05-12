package com.example.mydiplomaapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import com.example.mydiplomaapp.databinding.ActivityPinCodeBinding

class PinCodeActivity : AppCompatActivity() {

    private var myPreferences: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null

    private var binding:ActivityPinCodeBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPinCodeBinding.inflate(layoutInflater)
        setContentView(binding?.root)


        myPreferences = getSharedPreferences(Const.PREFERENCE_NAME, Context.MODE_PRIVATE)

        editor = myPreferences?.edit()
    }

    override fun onResume() {
        super.onResume()

        val isCodeSaved = myPreferences?.getBoolean(Const.IS_PREFERENCE_CODE_SAVED, false)

        if(isCodeSaved!!){
            startActivity(Intent(this, FirstPageActivity::class.java))
        }
        else {
            binding?.also {bind->
                bind.saveCode.setOnClickListener {
                    checkValues()

                    editor?.putBoolean(Const.IS_PREFERENCE_CODE_SAVED, true)
                    val code = binding?.firstPlace?.text.toString()+
                            binding?.secondPlace?.text.toString()+
                            binding?.thirdPlace?.text.toString()+
                            binding?.fourthPlace?.text.toString()

                    editor?.putInt(Const.PREFERENCE_CODE, code.toInt())
                    editor?.apply()
                    finish()
                }
            }
        }

    }

    private fun checkValues() {

    }

    override fun onDestroy() {
        super.onDestroy()

        binding = null
    }
}