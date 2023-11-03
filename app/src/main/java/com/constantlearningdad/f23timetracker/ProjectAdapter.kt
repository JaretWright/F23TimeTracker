package com.constantlearningdad.f23timetracker

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler

class ProjectAdapter  (val context : Context,
                       val projects : List<Project>,
                        val itemListener : ProjectItemListener) : RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder>(){

    /**
     * This class is used to allow us to connect/acces the elements in the item_project layout file
     */
    inner class ProjectViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)
    {
        val projectTextView = itemView.findViewById<TextView>(R.id.projectTextView)
        val descriptionTextView = itemView.findViewById<TextView>(R.id.descriptionTextView)
    }

    /**
     * This connects (aka inflates) the individual ViewHolder (which is the link to the item_project.xml
     * with the RecyclerView
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val inflater = LayoutInflater.from (parent.context)
        val view = inflater.inflate(R.layout.item_project, parent, false)
        return ProjectViewHolder(view)
    }

    /**
     * This method returns the number of projects in the recycler view
     */
    override fun getItemCount(): Int {
        return projects.size
    }

    /**
     * This method will bind the viewHolder with a specific instance of a Project object
     */
    override fun onBindViewHolder(viewHolder: ProjectViewHolder, position: Int) {
        val project = projects[position]
        with(viewHolder){
            projectTextView.text = project.projectName
            descriptionTextView.text = project.description
            itemView.setOnClickListener {
                itemListener.projectSelected(project)
            }
        }
    }

    interface ProjectItemListener{
        fun projectSelected(project : Project)
    }
}
