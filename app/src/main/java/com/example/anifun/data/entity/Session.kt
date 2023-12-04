package com.example.anifun.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mySession")
data class Session(
    @PrimaryKey(autoGenerate = true)
    val uid: Int? = null,

    @ColumnInfo(name = "sessionId")
    val sessionId : String,

    @ColumnInfo(name = "curName")
    val curName: String,

)
