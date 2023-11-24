package com.constantlearningdad.f23timetracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.Menu
import android.view.MenuItem
import com.constantlearningdad.f23timetracker.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding : ActivityProfileBinding
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //connect the scrollbars so they work
        binding.termsTextView.movementMethod=ScrollingMovementMethod()

        //get the user information

        if (auth.currentUser == null)
        {
            logout()
        }
        else
        {
            auth.currentUser?.let {user ->
                binding.userNameTextView.text = user.displayName
                binding.emailTextView.text = user.email
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
//                startActivity(Intent(applicationContext, ProfileActivity::class.java))
                return true
            }
            R.id.action_view_summary -> {
//                startActivity(Intent(applicationContext, SummaryActivity::class.java))
                return true
            }


        }
        return super.onOptionsItemSelected(item)
    }

    private fun logout()
    {
        auth.signOut()
        finish()
        startActivity(Intent(this, SigninActivity::class.java))
    }

}