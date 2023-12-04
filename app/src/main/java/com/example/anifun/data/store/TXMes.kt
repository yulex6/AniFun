package com.example.anifun.data.store

data class TXMes(
    val code: Long,
    val msg: String,
    val result: TXResult,
)
data class TXResult(
    val reply: String,
    val datatype: String,
)

