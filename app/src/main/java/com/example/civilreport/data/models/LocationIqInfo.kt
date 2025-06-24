package com.example.civilreport.data.models


data class LocationIqInfo(
    val place_id : String,
    val display_name : String,
    val lat : String,
    val lon : String,
    val address : Address?
)


data class Address(
    val road : String?,
    val suburb : String?,
    val city : String?,
    val state : String?,
    val country : String?,
    val postcode : String?
)