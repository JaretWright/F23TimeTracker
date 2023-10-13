package com.constantlearningdad.f23timetracker

class Project (
    var projectName : String? = null,
    var description : String? = null,
    var uID : String? = null,
    var timeRecords : ArrayList<TimeRecord>? = null
)