package com.constantlearningdad.f23timetracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.constantlearningdad.f23timetracker.databinding.ActivityCreateProjectBinding
import com.google.firebase.firestore.FirebaseFirestore

class CreateProjectActivity : AppCompatActivity() {
    private lateinit var binding : ActivityCreateProjectBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateProjectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.createProjectButton.setOnClickListener {
            var projectName = binding.projectNameEditText.text.toString()
            var description = binding.descriptionEditText.text.toString()

            if (projectName.isNotEmpty() && description.isNotEmpty())
            {
                //create an instance of a Project
                var project = Project(projectName, description)

                //connect to Firebase-Firestore database
                val db = FirebaseFirestore.getInstance().collection("projects")

                //get a unique ID from Firestore
                project.id = db.document().getId()

                //save our new Project object to the DB using a unique ID
                db.document().set(project)
                    .addOnSuccessListener { Toast.makeText(this,"DB Updated", Toast.LENGTH_LONG).show() }
                    .addOnFailureListener {exception ->
                        Toast.makeText(this,"Error writing to DB",Toast.LENGTH_LONG).show()
                        Log.w("DB_issue",exception.localizedMessage)
                    }
            }
            else
                Toast.makeText(this, "Both name & description must have values",Toast.LENGTH_LONG).show()
        }
    }
}