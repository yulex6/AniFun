package com.example.anifun.data.store

data class MusicMes(
    val data: List<Daum>,
    val code: Long,
)

data class Daum(
    val url: String,
    val time: Long,
)

