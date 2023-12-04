package com.example.anifun.data.store

data class OwnThinkMes(
    val message: String,
    val data: OwnThinkData,
)
data class OwnThinkData(
    val type: Long,
    val info: Info,
)
data class Info(
    val text: String,
)
