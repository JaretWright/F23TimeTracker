package com.constantlearningdad.f23timetracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import com.constantlearningdad.f23timetracker.databinding.ActivityLogTimeBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

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

        //create variables to hold the start and end times
        var startTime : Timestamp? = null
        var stopTime : Timestamp? = null
        var category : String? = null

        //click the start button - but only the first time
        binding.startButton.setOnClickListener {
            if (binding.startTextView.text.toString().isNullOrBlank())
            {
                startTime = Timestamp.now()
                binding.startTextView.text=startTime!!.toDate().toString()
            }
        }

        //ensure start was pressed and that a category was selected.
        //if those are both complete, then create a TimeRecord object and update the DB
        binding.stopButton.setOnClickListener {
            if (startTime!=null && binding.categorySpinner.selectedItemPosition>0)
            {
                stopTime = Timestamp.now()
                binding.stopTextView.text = stopTime!!.toDate().toString()
                category = binding.categorySpinner.selectedItem.toString()

                //create a TimeRecord object
                val timeRecord = TimeRecord(category, startTime!!, stopTime!!)
                projectSelected.addTimeRecord(timeRecord)

                binding.totalTimeTextView.text = getString(R.string.total_time_min) + timeRecord.getDuration()

                //update the DB
                val db = FirebaseFirestore.getInstance().collection("projects")

                projectSelected?.let{
                   documentID = projectSelected.projectName+"-"+userID

                   db.document(documentID!!).set(projectSelected)
                       .addOnSuccessListener { Toast.makeText(this,"DB Updated",Toast.LENGTH_LONG).show() }
                       .addOnFailureListener {
                           Toast.makeText(this,"DB write failed",Toast.LENGTH_LONG).show()
                           Log.w("DB Write Failure",it.localizedMessage)
                       }
                }
            }
            else
            {
                Toast.makeText(this,"Start time and Category required",Toast.LENGTH_LONG).show()
            }
        }

    }
}