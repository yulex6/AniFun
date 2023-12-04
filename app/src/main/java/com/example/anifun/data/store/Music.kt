package com.example.anifun.data.store


data class Music (
        val code: Long,
        val data: MusicData
)
data class MusicData(
        val name: String,
        var url: String,
        val picurl: String,
        val artistsname: String
)