package com.constantlearningdad.f23timetracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import com.constantlearningdad.f23timetracker.databinding.ActivityLogTimeBinding
import com.google.firebase.auth.FirebaseAuth

class LogTimeActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLogTimeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogTimeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get the documentID from the intent
        var documentID = intent.getStringExtra("documentID")
        Log.i("documentID",documentID.toString())

        //create a list of Projects to populate the Project Spinner
        var projects : MutableList<Project> = ArrayList()

        //use an adapter to connect the list of projects with the Spinner
        val adapter = ArrayAdapter(applicationContext, R.layout.item_spinner, projects)
        binding.projectSpinner.adapter=adapter

        val userID = FirebaseAuth.getInstance().currentUser!!.uid

        //if someone managed to get here without being authenticated
        //send them to the sign in screen
        if (userID == null)
        {
            finish()
            startActivity(Intent(this,SigninActivity::class.java))
        }

        var projectSelected = Project()

        val viewModel : ProjectViewModel by viewModels()
        viewModel.getProjects().observe(this, {
            projects.clear()
            projects.addAll(it)

            //find the project that was selected from the createProjectActivity
            // and set it as the project selected in the Spinner
            documentID?.let {
                for (project in projects)
                {
                    var projectIdentifier = project!!.projectName+"-"+project.uID
                    if (projectIdentifier.equals(documentID))
                        projectSelected = project
                }
                binding.projectSpinner.setSelection(projects.indexOf(projectSelected))
            }
            adapter.notifyDataSetChanged()
        })
    }
}