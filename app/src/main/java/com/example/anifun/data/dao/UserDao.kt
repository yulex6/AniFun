package com.example.anifun.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.anifun.data.entity.User

/**
 * UserDao
 * @author yu
 * @date 2023/07/03
 * @constructor 创建[UserDao]
 */
@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getAll(): List<User>

    @Query("SELECT * FROM users WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<User>

    @Query("SELECT * FROM users WHERE name LIKE :name")
    fun findByName(name: String): User

    @Query("SELECT * FROM users WHERE name = :name AND password = :password")
    fun findByNameAndPassword(name: String, password: String): User?

    @Insert
    fun insertAll(vararg users: User)

    @Delete
    fun delete(user: User)
}