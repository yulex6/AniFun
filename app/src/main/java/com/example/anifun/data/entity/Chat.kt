package com.example.anifun.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "Chat")
data class Chat(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id :Long? = null,

    @ColumnInfo(name = "contactId")
    val contactId : Int,

    @ColumnInfo(name = "contactName")
    val contactName : String? = null,

    @ColumnInfo(name = "msg")
    val msg: String,

    @ColumnInfo(name = "type")
    val type: Int,

    @ColumnInfo(name = "time")
    val time: Date?,
)
