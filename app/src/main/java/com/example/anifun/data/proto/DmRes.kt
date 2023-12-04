package com.example.anifun.data.proto

data class DmRes(
    var elems: List<Elem>,
)

data class Elem(
    val id: String,
    val progress: Long,
    val mode: Long,
    val fontsize: Long,
    val color: Long,
    val midHash: String,
    val content: String,
    val ctime: String,
    val weight: Long,
    val idStr: String,
    val attr: Long?,
)