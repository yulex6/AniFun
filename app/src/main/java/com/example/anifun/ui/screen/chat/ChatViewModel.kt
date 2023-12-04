package com.example.anifun.ui.screen.chat

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.anifun.data.entity.Chat
import com.example.anifun.repository.Repository
import com.example.anifun.ui.screen.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class ChatViewModel:ViewModel() {

    private val chatDao = MainViewModel.db.chatDao()
    val curContact = mutableStateOf(0)
    var currentBottomSheet = mutableStateOf<MultiBottomSheet.Intent?>(null)
    var historyList = mutableListOf<Chat>()
    val flow = Pager(
        PagingConfig(pageSize = 10)
    ) {
        chatDao.loadAllForPaging(curContact.value)
    }.flow
        .cachedIn(viewModelScope)

    fun setCurContact(id : Int){
        curContact.value = id
    }

    fun sendToRobot(input: String ,name: String){
        val send = Chat(contactId = curContact.value, msg = input, time = Date() , type = 1, contactName = null)
        when(curContact.value){
            0 ->{
                chatDao.insertChat(send)
                viewModelScope.launch {
                    withContext(Dispatchers.IO){
                        val txMsg = Repository.sendToTXRobot(input)
                        val rev = Chat(contactId = curContact.value, msg = txMsg.result.reply, time = Date() , type = 0, contactName = name)
                        chatDao.insertChat(rev)
                    }
                }
            }
            1 ->{
                chatDao.insertChat(send)
                viewModelScope.launch {
                    withContext(Dispatchers.IO){
                        val txMsg = Repository.sendToOwnThink(input)
                        val rev = Chat(contactId = curContact.value, msg = txMsg.data.info.text, time = Date() , type = 0, contactName = name)
                        chatDao.insertChat(rev)
                    }
                }
            }
        }

    }
    fun deleteAll(){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                chatDao.deleteByContactId(curContact.value)
            }
        }
    }

    fun findHistoryList(){
       historyList = chatDao.findChatsGroupByContactId() as MutableList<Chat>
    }
}