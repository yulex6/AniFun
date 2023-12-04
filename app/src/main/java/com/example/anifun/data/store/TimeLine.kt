package com.example.anifun.data.store

import com.google.gson.annotations.SerializedName

data class TimeLine(
    val code: Long,
    val message: String,
    val result: List<TimeLineResult>,
)

data class TimeLineResult(
    val date: String,
    @SerializedName("date_ts")
    val dateTs: Long,
    @SerializedName("day_of_week")
    val dayOfWeek: Long,
    val episodes: List<Episode>,
    @SerializedName("is_today")
    val isToday: Long,
)

data class Episode(
    val cover: String,
    val delay: Long,
    @SerializedName("delay_id")
    val delayId: Long,
    @SerializedName("delay_index")
    val delayIndex: String,
    @SerializedName("delay_reason")
    val delayReason: String,
    @SerializedName("ep_cover")
    val epCover: String,
    @SerializedName("episode_id")
    val episodeId: Long,
    val follows: String,
    val plays: String,
    @SerializedName("pub_index")
    val pubIndex: String,
    @SerializedName("pub_time")
    val pubTime: String,
    @SerializedName("pub_ts")
    val pubTs: Long,
    val published: Long,
    @SerializedName("season_id")
    val seasonId: Long,
    @SerializedName("square_cover")
    val squareCover: String,
    val title: String,
)