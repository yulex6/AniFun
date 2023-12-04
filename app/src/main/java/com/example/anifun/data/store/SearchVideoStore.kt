package com.example.anifun.data.store

import com.google.gson.annotations.SerializedName

data class SearchVideoStore(
    val code: Long,
    val message: String,
    val ttl: Long,
    val data: SearchData,
)

data class SearchData (

    val result: List<Result>,

    @SerializedName("is_search_page_grayed")
    val isSearchPageGrayed: Long
)

data class Result (
    @SerializedName("result_type")
    val resultType: String,
    val data: List<SearchListElement>
)


data class SearchListElement(
    val type: String,
    val id: Long,
    val author: String,
    val mid: Long,
    val typeid: String,
    val typename: String,
    val arcurl: String,
    val aid: Long,
    val bvid: String,
    val title: String,
    val description: String,
    val arcrank: String,
    val pic: String,
    val play: Long,

    @SerializedName("video_review")
    val videoReview: Long,

    val favorites: Long,
    val tag: String,
    val review: Long,
    val pubdate: Long,
    val senddate: Long,
    val duration: String,
    val badgepay: Boolean,

    @SerializedName("hit_columns")
    val hitColumns: List<String>,

    @SerializedName("view_type")
    val viewType: String,

    @SerializedName("is_pay")
    val isPay: Long,

    @SerializedName("is_union_video")
    val isUnionVideo: Long,

    @SerializedName("rec_tags")
    val recTags: Any? = null,

    @SerializedName("new_rec_tags")
    val newRecTags: List<Any?>,

    @SerializedName("rank_score")
    val rankScore: Long,

    val like: Long,
    val upic: String,
    val corner: String,
    val cover: String,
    val desc: String,
    val url: String,

    @SerializedName("rec_reason")
    val recReason: String,

    val danmaku: Long,

    @SerializedName("biz_data")
    val bizData: Any? = null
)
