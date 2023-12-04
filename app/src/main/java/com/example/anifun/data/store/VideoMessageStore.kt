package com.example.anifun.data.store


data class VideoMessageStore(
    val code: Long,
    val message: String,
    val ttl: Long,
    val data: VideoMessageData
)

data class VideoMessageData (
    val bvid: String,
    val cid: Long,
)