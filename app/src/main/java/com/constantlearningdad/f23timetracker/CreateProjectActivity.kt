package com.constantlearningdad.f23timetracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.constantlearningdad.f23timetracker.databinding.ActivityCreateProjectBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import androidx.activity.viewModels

class CreateProjectActivity : AppCompatActivity() {
    private lateinit var binding : ActivityCreateProjectBinding
    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateProjectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.createProjectButton.setOnClickListener {
            var projectName = binding.projectNameEditText.text.toString()
            var description = binding.descriptionEditText.text.toString()

            if (projectName.isNotEmpty() && description.isNotEmpty())
            {
                //used !! to indicate that we know we have an authenticated user at this point
                //!! indicates that auth.currentUser can not be null
                var uID = auth.currentUser!!.uid

                //connect to Firebase-Firestore database
                val db = FirebaseFirestore.getInstance().collection("projects")

                //create a unique projectID
                var documentID = projectName+"-"+uID

                //create a project
                //In Java Project project = new Project(projectName, description, uID, new ArrayList());
                var project = Project(projectName, description, uID, ArrayList())

                //save our new Project object to the DB using a unique ID
                db.document(documentID).set(project)
                    .addOnSuccessListener {
                        Toast.makeText(this,"DB Updated", Toast.LENGTH_LONG).show()
                        binding.projectNameEditText.text.clear()
                        binding.descriptionEditText.text.clear()
                    }
                    .addOnFailureListener {exception ->
                        Toast.makeText(this,"Error writing to DB",Toast.LENGTH_LONG).show()
                        Log.w("DB_issue",exception.localizedMessage)
                    }
            }
            else
                Toast.makeText(this, "Both name & description must have values",Toast.LENGTH_LONG).show()
        }

        val viewModel : ProjectViewModel by viewModels()
        viewModel.getProjects().observe(this, { projects->
            binding.projectRecyclerView.adapter = ProjectAdapter(this, projects)
        })
    }
}