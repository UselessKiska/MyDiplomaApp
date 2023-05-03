package com.example.mydiplomaapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d("First Commit", "First commit from another branch")
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()

        if(isFinishing)
            return
        finishAffinity()
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.d("onDestroy", "App Destroyed")
    }
}