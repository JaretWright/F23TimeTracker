package com.constantlearningdad.f23timetracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.constantlearningdad.f23timetracker.databinding.ActivityLogTimeBinding

class LogTimeActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLogTimeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogTimeBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}