package com.example.anifun.data.store


data class RecommendVideoListStore(
    val code: Long,
    val message: String,
    val ttl: Long,
    val data: RecommendVideoListData
)

data class RecommendVideoListData (
    val item: MutableList<Item>,
)
data class Item (
    val id: Long,
    val bvid: String,
    val cid: Long,
    val pic: String,
    val title: String,
    val duration: Long,
    val pubdate: Long,
    val owner: RecommendOwner? = null,
    val stat: RecommendStat? = null,

)
data class RecommendOwner (
    val mid: Long,
    val name: String,
    val face: String
)
data class RecommendStat (
    val view: Long,
    val like: Long,
    val danmaku: Long
)
