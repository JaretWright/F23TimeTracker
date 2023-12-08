package com.constantlearningdad.f23timetracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import com.constantlearningdad.f23timetracker.databinding.ActivityCreateProjectBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

//IN Java this would be public class CReateProjectActivity extends AppCompatActivity implements ProjectAdapter.ProjectItemListener
class CreateProjectActivity : AppCompatActivity(), ProjectAdapter.ProjectItemListener {
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
        viewModel.getProjects().observe(this, {
            binding.projectRecyclerView.adapter = ProjectAdapter(this, it, this )
        })

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
//                startActivity(Intent(applicationContext, CreateProjectActivity::class.java))
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
                startActivity(Intent(applicationContext, SummaryActivity::class.java))
                return true
            }
            R.id.action_selfie -> {
                startActivity(Intent(applicationContext, CameraXExperimentActivity::class.java))
                return true
            }


        }
        return super.onOptionsItemSelected(item)
    }

    override fun projectSelected(project: Project) {
//        Toast.makeText(this,"Project Selected: $project", Toast.LENGTH_LONG).show()
        var intent = Intent(this, LogTimeActivity::class.java)
        intent.putExtra("documentID", project.projectName +"-"+project.uID)
        startActivity(intent)
    }
}