package com.constantlearningdad.f23timetracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
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