package com.example.anifun.ui.screen

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.anifun.data.AppDatabase
import com.example.anifun.data.entity.Session
import com.example.anifun.data.entity.User
import java.util.UUID

class MainViewModel : ViewModel() {

    private val userDao = db.userDao()
    private val sessionDao = db.sessionDao()
    var  session : Session? =null
    val isLogin = mutableStateOf(false)

    companion object{
        lateinit var db:AppDatabase
        fun initDb(database: AppDatabase){
            db = database
        }
    }

    fun checkIsLogin(): String?{
       session =   sessionDao.getAll()
        return if (session == null){
            isLogin.value = false
            null
        }else{
            isLogin.value = true
            session!!.curName
        }
    }
    fun login(name: String, password: String): Boolean{
        val user =  userDao.findByNameAndPassword(name,password)
        return if(user == null) false
        else{
            session = Session(sessionId = UUID.randomUUID().toString(), curName = user.name)
            sessionDao.insert(session!!)
            true
        }

    }

    fun register(name: String, password: String){
        val user = User(name = name, password = password)
        userDao.insertAll(user)
    }

    fun logout(): String{
        return if (session == null) "failed"
        else{
            session?.let { sessionDao.delete(it) }
            session = null
            checkIsLogin()
            "success"
        }

    }

}