package com.constantlearningdad.f23timetracker

class Project (
    var projectName : String? = null,
    var description : String? = null,
    var uID : String? = null,
    var timeRecords : ArrayList<TimeRecord>? = null
){

    fun addTimeRecord( timeRecord: TimeRecord)
    {
        if (timeRecords == null)
            timeRecords = ArrayList()

        timeRecords!!.add(timeRecord)
    }

    override fun toString() : String{
       if (projectName != null)
           return projectName!!
        else
            return "undefined"
    }
}