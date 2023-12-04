package com.example.anifun.data.store

import com.google.gson.annotations.SerializedName

data class RealVideoLinkStore(
    val code: Long,
    val message: String,
    val ttl: Long,
    val data: RealVideoLinkData
)

data class RealVideoLinkData (
    val durl: List<Durl>,
)

data class Durl (
    val order: Long,
    val length: Long,
    val size: Long,
    val ahead: String,
    val vhead: String,
    val url: String,

    @SerializedName("backup_url")
    val backupURL: List<String>
)
