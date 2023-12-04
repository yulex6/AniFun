package com.example.anifun.data.api


import com.example.anifun.data.store.Music
import com.example.anifun.data.store.MusicMes
import com.example.anifun.data.store.*
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query
import java.time.LocalDate


/**
 * api服务
 * @author yu
 * @date 2023/07/03
 * @constructor 创建[ApiService]
 */
interface ApiService {

    /**
     * 获取热门视频列表
     * @return [VideoStore]
     */
    @GET("/x/web-interface/ranking/v2")
    suspend fun getVideoList(
    ): VideoStore


    /**
     * 获取搜索视频列表
     * @param [cookie] cookie
     * @param [itemStart] 开始
     * @param [pageSize] 页面大小
     * @param [keyword] 关键字
     * @return [SearchVideoStore]
     */
    @Headers(
        "user-Agent:Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36 Edg/113.0.1774.50",
    )
    @GET("/x/web-interface/search/all/v2")
    suspend fun getSearchVideoList(
        @Header("cookie") cookie: String,
        @Query("page") itemStart: Int = 1,
        @Query("page_size") pageSize: Int = 20,
        @Query("keyword") keyword: String = "",
    ): SearchVideoStore

    /**
     * 获得真实视频链接
     * @param [bvid] bvid
     * @param [cid] cid
     * @param [qn] qn
     * @param [fnval] fnval
     * @return [RealVideoLinkStore]
     */
    @GET("/x/player/playurl")
    suspend fun getRealVideoLink(
        @Query("bvid") bvid : String,
        @Query("cid") cid : String,
        @Query("qn") qn :String = "112",
        @Query("fnval") fnval :String = "0"
    ): RealVideoLinkStore

    /**
     * 获取视频信息
     * @param [bvid] bvid
     * @return [VideoMessageStore]
     */
    @GET("/x/web-interface/view")
    suspend fun getVideoMess(
        @Query("bvid") bvid : String,
    ): VideoMessageStore

    /**
     * 获取动态
     * @param [cookie] cookie
     * @param [offset] offset
     * @param [host_mid] 主页mid
     * @return [DynamicStore]
     */
    @Headers(
        "User-Agent:Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36 Edg/113.0.1774.50",
    )
    @GET("/x/polymer/web-dynamic/v1/feed/space")
    suspend fun getDynamic(
        @Header("cookie") cookie: String,
        @Query("offset") offset : String,
        @Query("host_mid") host_mid : String,
    ): DynamicStore

    /**
     * 得到推荐视频列表
     * @param [cookie] cookie
     * @param [fresh_idx_1h] fresh_idx_1h
     * @param [fresh_idx] fresh_idx
     * @param [brush] brush
     * @param [last_showlist] last_showlist
     * @param [y_num] y num
     * @param [fresh_type] fresh_type
     * @param [feed_version] feed_version
     * @param [fetch_row] fetch_row
     * @param [homepage_ver] homepage_ver
     * @param [ps] ps
     * @param [last_y_num] last_y_num
     * @param [screen] screen
     * @param [outside_trigger] outside_trigger
     * @param [uniq_id] uniq_id
     * @param [w_rid] w_rid
     * @param [wts] wts
     * @param [web_location] 网络位置
     * @return [RecommendVideoListStore]
     */
    @Headers(
        "User-Agent: PostmanRuntime/7.32.2")
    @GET("/x/web-interface/wbi/index/top/feed/rcmd")
    suspend fun getRecommendVideoList(
        @Header("cookie") cookie: String,
        @Query("fresh_idx_1h") fresh_idx_1h: String ,
        @Query("fresh_idx") fresh_idx: String,
        @Query("brush") brush: String ,
        @Query("last_showlist") last_showlist: String,
        @Query("y_num") y_num: String = "5",
        @Query("fresh_type") fresh_type: String = "3",
        @Query("feed_version") feed_version: String = "V8",
        @Query("fetch_row") fetch_row: String = "1",
        @Query("homepage_ver") homepage_ver: String = "1",
        @Query("ps") ps: String = "10",
        @Query("last_y_num") last_y_num: String = "5",
        @Query("screen") screen: String = "1426-423",
        @Query("outside_trigger") outside_trigger: String = "",
        @Query("uniq_id") uniq_id: String = "1599354787752",
        @Query("w_rid") w_rid: String = "196154b2e81ee25851c740227c17f8ab",
        @Query("wts") wts: String = "1688359221",
        @Query("web_location")web_location:String = "1430650"
    ): RecommendVideoListStore

    /**
     * 得到随机音乐
     * @param [format] 格式
     * @return [Music]
     */
    @GET("https://api.uomg.com/api/rand.music")
    suspend fun getRandomMusic(
        @Query("format") format:String = "json"
    ): Music

    /**
     * 获取音乐信息
     * @param [id] id
     * @return [MusicMes]
     */
    @GET("https://www.yuelx.top/song/url")
    suspend fun getMusicMes(
        @Query("id") id:String
    ): MusicMes

    /**
     * 获取番剧时间线
     * @param [types] 类型
     * @param [before] 之前
     * @param [after] 后
     * @return [TimeLine]
     */
    @GET("/pgc/web/timeline")
    suspend fun getTimeLine(
        @Query("types") types:String = "1",
        @Query("before") before:String = (LocalDate.now().dayOfWeek.value - 1).toString(),
        @Query("after") after:String = (7-LocalDate.now().dayOfWeek.value).toString()
    ): TimeLine

    /**
     * 发送到txrobot
     * @param [key] 关键
     * @param [mode] 模式
     * @param [question] 问题
     * @return [TXMes]
     */
    @GET("https://apis.tianapi.com/robot/index")
    suspend fun sendToTXRobot(
        @Query("key") key: String = "00c8b0e4f2c99cc59a40cbbb073b33f0",
        @Query("mode")mode:String = "1",
        @Query("question") question:String
    ):TXMes

    /**
     * 发送到OwnThink
     * @param [appid] appid
     * @param [userid] 用户标识
     * @param [spoken] 口语
     * @return [OwnThinkMes]
     */
    @GET("https://api.ownthink.com/bot")
    suspend fun sendToOwnThink(
        @Query("appid") appid: String = "bce3ed9c6a5b7ee3c97a4bf7b50c757e",
        @Query("userid")userid:String = "1",
        @Query("spoken") spoken:String
    ):OwnThinkMes

    /**
     * 获取弹幕
     * @param [type] 类型
     * @param [oid] oid
     * @param [pid] pid
     * @param [segment_index] 段指数
     * @return [ResponseBody]
     */
    @GET("https://api.bilibili.com/x/v2/dm/list/seg.so")
    suspend fun getDm(
        @Query("type") type:String = "1",
        @Query("oid") oid:String,
        @Query("pid") pid:String,
        @Query("segment_index") segment_index:String = "1",
    ):ResponseBody

}