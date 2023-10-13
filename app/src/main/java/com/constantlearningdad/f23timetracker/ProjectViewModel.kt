package com.constantlearningdad.f23timetracker

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class ProjectViewModel : ViewModel() {
    private val projects = MutableLiveData<List<Project>>()

    /**
     * init is called after the constructor runs and can be used to setup our
     * live data connection to Firestore
     */
    init{
        val userID = Firebase.auth.currentUser?.uid

        //query the DB to get all of the projects for a specific user
        val db = FirebaseFirestore.getInstance().collection("projects")
            .whereEqualTo("uid", userID)
            .orderBy("projectName")
            .addSnapshotListener{ documents, exception ->
                if (exception != null)
                {
                    Log.w("DB_Response","Listen Failed ${exception.localizedMessage}")
                    return@addSnapshotListener
                }

                //if there wasn't an exception, we should receive a list of documents
                //we can loop over the documents and create Project objects
                //documents?.let{} means that the document list is not null
                documents?.let{
                    for (document in documents)
                    {
                        Log.i("DB_Response","${document.data}")
                    }
                }
            }

    }

    fun getProjects() : LiveData<List<Project>>
    {
        return projects
    }
}