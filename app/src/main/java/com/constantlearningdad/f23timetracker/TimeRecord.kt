package com.constantlearningdad.f23timetracker

import com.google.firebase.Timestamp

class TimeRecord (
    var activity : String,
    var startTime : Timestamp,
    var endTime : Timestamp
)