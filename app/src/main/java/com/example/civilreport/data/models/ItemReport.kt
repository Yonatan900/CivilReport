package com.example.civilreport.data.models

data class ItemReport(
    val id: String = "",

    val title: String,

    val costumerName: String,

    val location: String,

    val mainImageUri: String,

    val timestamp: Long,

    val entries: List<ImageTextEntry> = emptyList()
){

}