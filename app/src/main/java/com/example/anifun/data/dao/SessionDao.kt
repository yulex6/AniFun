package com.example.anifun.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.anifun.data.entity.Session

/**
 * SessionDao
 * @author yu
 * @date 2023/07/03
 * @constructor 创建[SessionDao]
 */
@Dao
interface SessionDao {
    @Query("SELECT * FROM mySession")
    fun getAll(): Session?

    @Delete
    fun delete(session: Session)

    @Insert
    fun insert(session: Session)
}