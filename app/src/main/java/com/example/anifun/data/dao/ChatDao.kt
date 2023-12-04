package com.example.anifun.data.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.anifun.data.entity.Chat


/**
 * chatDao
 * @author yu
 * @date 2023/07/03
 * @constructor 创建[ChatDao]
 */
@Dao
interface ChatDao {

    @Query("SELECT * FROM Chat WHERE contactId = :contactId ORDER BY time DESC")
    fun loadAllForPaging(contactId: Int): PagingSource<Int, Chat>

    @Insert
    fun insertChat(vararg chats: Chat)

    @Query("DELETE FROM Chat WHERE contactId = :contactId ")
    fun deleteByContactId(contactId: Int)
    //SELECT  *    FROM (SELECT  *    FROM Chat   GROUP BY contactId,time  ORDER BY time)  GROUP BY contactId ORDER BY time DESC
    @Query("SELECT  *    FROM (SELECT  *    FROM Chat   GROUP BY contactId,time  ORDER BY time DESC)  GROUP BY contactId")
    fun findChatsGroupByContactId():List<Chat>
}