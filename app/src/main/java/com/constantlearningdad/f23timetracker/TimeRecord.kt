package com.constantlearningdad.f23timetracker

import com.google.firebase.Timestamp

class TimeRecord (
    var activity : String ?= null,
    var startTime : Timestamp?= null,
    var endTime : Timestamp?= null
){
    /**
     * This calculates the duration of the TimeRecord in minutes
     */
    fun getDuration() : Long{
        if (startTime!=null && endTime!=null)
        {
            //the calculates the time in 1000's of a second
            val difference =endTime!!.toDate().time-startTime!!.toDate().time

            //convert to minutes
            return difference/1000/60
        }
        return 0
    }

}