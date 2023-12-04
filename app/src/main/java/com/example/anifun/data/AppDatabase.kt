package com.example.anifun.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.anifun.data.dao.ChatDao
import com.example.anifun.data.dao.SessionDao
import com.example.anifun.data.dao.UserDao
import com.example.anifun.data.entity.*

@Database(entities = [User::class,Session::class,Chat::class], version = 3)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun sessionDao(): SessionDao
    abstract fun chatDao(): ChatDao


    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "word_database"
                )//是否允许在主线程进行查询
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries().build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}