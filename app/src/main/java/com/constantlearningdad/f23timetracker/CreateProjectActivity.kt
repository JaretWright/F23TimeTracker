package com.constantlearningdad.f23timetracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.constantlearningdad.f23timetracker.databinding.ActivityCreateProjectBinding

class CreateProjectActivity : AppCompatActivity() {
    private lateinit var binding : ActivityCreateProjectBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateProjectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.createProjectButton.setOnClickListener {
            if (binding.projectNameEditText.text.toString().isNotEmpty() &&
                binding.descriptionEditText.text.toString().isNotEmpty())
            {

            }
            else
                Toast.makeText(this, "Both name & description must have values",Toast.LENGTH_LONG).show()
        }
    }
}