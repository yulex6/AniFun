package com.example.anifun.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User (
    @PrimaryKey(autoGenerate = true)
    val uid: Int? = null,

    @ColumnInfo(name = "name")
    val name: String,


    @ColumnInfo(name = "password")
    val password: String,
)