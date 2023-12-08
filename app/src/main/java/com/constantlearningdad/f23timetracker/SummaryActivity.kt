package com.constantlearningdad.f23timetracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.constantlearningdad.f23timetracker.databinding.ActivitySummaryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

class SummaryActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySummaryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //ensure that we have a valid user
        val user = FirebaseAuth.getInstance().currentUser

        //if somehow a user got to this activity without being logged in, force them to log in
        if (user == null)
        {
            finish()
            startActivity(Intent(this,SigninActivity::class.java))
        }

        //Create a mutable list of projects - these will be displayed in the spinner
        val projects : MutableList<Project?> = ArrayList()

        //attach a Spinner Adapter to the list of projects
        val adapter = ArrayAdapter(applicationContext, R.layout.item_spinner,projects)
        binding.projectSelectedSpinner.adapter=adapter

        //query firestore with the userID and a list of projects
        val db = FirebaseFirestore.getInstance().collection("projects")
        db.whereEqualTo("uid",user!!.uid)
            .orderBy("projectName")
            .get()
            .addOnSuccessListener {
               projects.add(Project(projectName="Choose a Project"))

               //loop over the documents and create Project objects
               for (document in it)
               {
                   projects.add(document.toObject(Project::class.java))
               }

                //update the adapter
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {Log.i("DBFail", "${it.localizedMessage}") }

        binding.projectSelectedSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{

            //p2 -> this is the project selected - it is the index of the project selected
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                //create a Log element to watch this in action
                Log.i("Spinner","position = $p2, which is project ${projects.get(p2)}")

                val projectSelected = projects.get(p2)

                projectSelected!!.timeRecords?.let{
                    val research = it.filter{timeRecord -> timeRecord.activity.equals("Research")}
                                    .map{timeRecord -> timeRecord.getDuration() }
                                    .sum()
                    binding.researchTextView.text=research.toString()

                    val design = it.filter{timeRecord -> timeRecord.activity.equals("Design")}
                                    .map{timeRecord -> timeRecord.getDuration() }
                                    .sum()
                    binding.designTextView.text=design.toString()

                    val development = it.filter{timeRecord -> timeRecord.activity.equals("Development")}
                        .map{timeRecord -> timeRecord.getDuration() }
                        .sum()
                    binding.developmentTextView.text=development.toString()

                    val testing = it.filter{timeRecord -> timeRecord.activity.equals("Testing")}
                        .map{timeRecord -> timeRecord.getDuration() }
                        .sum()
                    binding.testingTextView.text=testing.toString()

                    val other = it.filter{timeRecord -> timeRecord.activity.equals("Other")}
                        .map{timeRecord -> timeRecord.getDuration() }
                        .sum()
                    binding.otherTextView.text=other.toString()

                    val totalMinutes = research+design+development+testing+other
                    binding.totalMinutesTextView.text = totalMinutes.toString()

                    binding.totalHoursTextView.text = String.format("Total Hours: %.1f",totalMinutes/60.0)
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        setSupportActionBar(binding.mainToolBar.toolbar)
    }

    /**
     * Add the menu to the toolbar
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_add_project -> {
                startActivity(Intent(applicationContext, CreateProjectActivity::class.java))
                return true
            }
            R.id.action_log_time -> {
                startActivity(Intent(applicationContext, LogTimeActivity::class.java))
                return true
            }
            R.id.action_edit_profile -> {
                startActivity(Intent(applicationContext, ProfileActivity::class.java))
                return true
            }
            R.id.action_view_summary -> {
//                startActivity(Intent(applicationContext, SummaryActivity::class.java))
                return true
            }
            R.id.action_selfie -> {
                startActivity(Intent(applicationContext, CameraXExperimentActivity::class.java))
                return true
            }


        }
        return super.onOptionsItemSelected(item)
    }
}