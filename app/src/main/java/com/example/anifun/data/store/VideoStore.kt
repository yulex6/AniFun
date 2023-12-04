package com.example.anifun.data.store

import com.google.gson.annotations.SerializedName

data class VideoStore(
    val code: Long,
    val message: String,
    val ttl: Long,
    val data: Data
)

data class Data (
    val list: List<ListElement>,
    @SerializedName("no_more")
    val noMore: Boolean
)


data class ListElement (
    val aid: Long,
    val videos: Long,
    val tid: Long,
    val tname: String,
    val copyright: Long,
    val pic: String,
    val title: String,
    val pubdate: Long,
    val ctime: Long,
    val desc: String,
    val state: Long,
    val duration: Long,

    @SerializedName("mission_id")
    val missionID: Long,

    val rights: Map<String, Long>,
    val owner: Owner,
    val stat: Map<String, Long>,
    val dynamic: String,
    val cid: Long,
    val dimension: Dimension,

    @SerializedName("short_link_v2")
    val shortLinkV2: String,

    @SerializedName("first_frame")
    val firstFrame: String,

    @SerializedName("pub_location")
    val pubLocation: String,

    val bvid: String,

    @SerializedName("season_type")
    val seasonType: Long,

    @SerializedName("is_ogv")
    val isOgv: Boolean,

    @SerializedName("ogv_info")
    val ogvInfo: Any? = null,

    @SerializedName("rcmd_reason")
    val rcmdReason: RcmdReason
)

data class Dimension (
    val width: Long,
    val height: Long,
    val rotate: Long
)

data class Owner (
    val mid: Long,
    val name: String,
    val face: String
)

data class RcmdReason (
    val content: String,

    @SerializedName("corner_mark")
    val cornerMark: Long
)



