package com.example.anifun


import com.example.anifun.repository.Repository
import com.example.anifun.ui.screen.scope
import com.google.protobuf.util.JsonFormat
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.junit.Test


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @OptIn(DelicateCoroutinesApi::class)
    @Test
    fun addition_isCorrect() {
        GlobalScope.launch{
            val responseBody = Repository.getDm()
            val parseFrom = Dm.DmSegMobileReply.parseFrom(responseBody.bytes())
            val json = JsonFormat.printer().print(parseFrom)
            println(json)
        }



    }
}