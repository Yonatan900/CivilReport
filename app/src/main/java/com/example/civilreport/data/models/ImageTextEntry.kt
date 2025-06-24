package com.example.civilreport.data.models

data class ImageTextEntry(
    val imageUri: String,
    val imageDesc: String,
    val imageSize: CardSize = CardSize.MEDIUM
)
