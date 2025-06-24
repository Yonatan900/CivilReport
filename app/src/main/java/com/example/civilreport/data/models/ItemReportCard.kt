package com.example.civilreport.data.models

enum class CardSize {
    SMALL,
    MEDIUM,
    FULL
}
data class ItemReportCard(
    val uri: String,
    val caption: String,
    var size: CardSize = CardSize.MEDIUM
)
