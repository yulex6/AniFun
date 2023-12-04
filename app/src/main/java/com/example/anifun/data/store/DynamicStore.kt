package com.example.anifun.data.store

import com.google.gson.annotations.SerializedName

data class DynamicStore(
    val code: Long,
    val message: String,
    val ttl: Long,
    val data: DynamicData,
)
data class DynamicData(
    @SerializedName("has_more")
    val hasMore: Boolean,
    val items: List<DynamicItem>,
    val offset: String,
)

data class DynamicItem(
    val modules: Modules,
)


data class Modules(
    @SerializedName("module_dynamic")
    val moduleDynamic: ModuleDynamic,
)



data class ModuleDynamic(
    val major: Major?,
)


data class Major(
    val draw: Draw?,
    val type: String,
    val archive: DynamicArchive?,
)

data class Draw(
    val id: Long,
    val items: List<Item2>,
)

data class Item2(
    val height: Long,
    val size: Double,
    val src: String,
    val tags: List<Any?>,
    val width: Long,
)

data class DynamicArchive(
    val aid: String,
    val badge: Badge,
    val bvid: String,
    val cover: String,
    val desc: String,
    @SerializedName("disable_preview")
    val disablePreview: Long,
    @SerializedName("duration_text")
    val durationText: String,
    @SerializedName("jump_url")
    val jumpUrl: String,
    val stat: DynamicStat,
    val title: String,
    val type: Long,
)

data class Badge(
    @SerializedName("bg_color")
    val bgColor: String,
    val color: String,
    @SerializedName("icon_url")
    val iconUrl: Any?,
    val text: String,
)

data class DynamicStat(
    val danmaku: String,
    val play: String,
)
