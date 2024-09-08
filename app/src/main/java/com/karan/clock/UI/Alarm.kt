package com.karan.clock.UI


data class Alarm(
    val id : Int,
    var hour : Int,
    var minutes : Int,
    var label : String = "",
    var isActive : Int
)