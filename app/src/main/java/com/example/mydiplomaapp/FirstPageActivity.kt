package com.example.mydiplomaapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.EditorInfo
import com.example.mydiplomaapp.databinding.ActivityFirstPageBinding
import com.google.android.material.snackbar.Snackbar

class FirstPageActivity : AppCompatActivity() {

    private var myPreferences:SharedPreferences? = null
    private var editor:SharedPreferences.Editor? = null

    private var binding:ActivityFirstPageBinding? = null

    var text:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFirstPageBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        myPreferences = getSharedPreferences(Const.PREFERENCE_NAME, Context.MODE_PRIVATE)

        editor = myPreferences?.edit()


        var info = intent.extras
        if(info!=null){
            text = info.getString(Const.PREFERENCE_CODE)
            Log.d("CheckingAdd", text.toString())
        }


    }

    override fun onResume() {
        super.onResume()

        val isCodeSaved = myPreferences?.getBoolean(Const.IS_PREFERENCE_CODE_SAVED, false)

        binding?.firstPlace?.addTextChangedListener(object:TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding?.secondPlace?.requestFocus()
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
        binding?.secondPlace?.addTextChangedListener(object:TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding?.thirdPlace?.requestFocus()
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
        binding?.thirdPlace?.addTextChangedListener(object:TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding?.fourthPlace?.requestFocus()
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        if(isCodeSaved!!){
            val code = myPreferences?.getInt(Const.PREFERENCE_CODE, 0)
            Log.d(Const.PREFERENCE_CODE, code.toString())

            binding?.enterApp?.setOnClickListener {

                val newCode = binding?.firstPlace?.text.toString()+
                        binding?.secondPlace?.text.toString()+
                        binding?.thirdPlace?.text.toString()+
                        binding?.fourthPlace?.text.toString()

                if(newCode.toInt() == code){
                    if(text.isNullOrEmpty())
                        enterTheApp()
                    else{
                        val intent = Intent(this, PinCodeActivity::class.java)
                        intent.putExtra("Required", true)
                        editor?.putBoolean(Const.IS_CODE_REQUIRED, true)
                        editor?.apply()
                        startActivity(intent)
                        finish()
                    }

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