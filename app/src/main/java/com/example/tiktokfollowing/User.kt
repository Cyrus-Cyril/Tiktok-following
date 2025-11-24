package com.example.tiktokfollowing

data class User(
    val id: Int,
    var name: String,
    var douyinId: String,
    var avatarResId: Int,
    var isFollowed: Boolean = true,
    var isSpecial: Boolean = false,
    var remark: String = ""
)
