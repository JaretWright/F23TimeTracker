package com.constantlearningdad.f23timetracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.constantlearningdad.f23timetracker.databinding.ActivityLogTimeBinding

class LogTimeActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLogTimeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogTimeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get the documentID from the intent
        var documentID = intent.getStringExtra("documentID")
        Log.i("documentID",documentID.toString())

    }
}