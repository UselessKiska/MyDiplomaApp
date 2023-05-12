package com.example.mydiplomaapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import com.example.mydiplomaapp.databinding.ActivityPinCodeBinding

class PinCodeActivity : AppCompatActivity() {

    private var myPreferences: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null

    private var binding:ActivityPinCodeBinding? = null

    private var isChecked:Boolean? = null

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
        val isRequired = myPreferences?.getBoolean(Const.IS_CODE_REQUIRED, false)

        Log.d(Const.IS_CODE_REQUIRED,isRequired.toString())
//
//        if(isCodeSaved!! && !isRequired!!){
//            if(intent.extras!=null) {
//                isChecked = intent.extras?.getBoolean("Required")
//                setValues()
//            }
//            else{
//                val intent = Intent(this, FirstPageActivity::class.java)
//                intent.putExtra(Const.PREFERENCE_CODE, "exists")
//                startActivity(intent)
//            }
//        }
//        else {
//            setValues()
//        }

        setValues()
    }

    private fun setValues() {
        binding?.also {bind->
            bind.saveCode.setOnClickListener {

                editor?.putBoolean(Const.IS_PREFERENCE_CODE_SAVED, true)
                val code = binding?.firstPlace?.text.toString()+
                        binding?.secondPlace?.text.toString()+
                        binding?.thirdPlace?.text.toString()+
                        binding?.fourthPlace?.text.toString()

                editor?.putInt(Const.PREFERENCE_CODE, code.toInt())
                editor?.putBoolean(Const.IS_CODE_REQUIRED, false)
                editor?.apply()
                this.finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        binding = null
    }
}