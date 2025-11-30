package com.example.tiktokfollowing

import android.os.Handler
import android.os.Looper
import kotlin.math.min

object MockServer {

    private const val TOTAL_COUNT = 1000
    private const val PAGE_SIZE = 10

    // 用几张本地头像轮流使用
    private val avatarResIds = listOf(
        R.drawable.avatar_1,
        R.drawable.avatar_2,
        R.drawable.avatar_3,
        R.drawable.avatar_4,
        R.drawable.avatar_5
    )

    // 简单缓存一下全量数据，模拟服务端数据库
    private val allUsers: List<User> by lazy {
        val list = mutableListOf<User>()
        for (i in 1..TOTAL_COUNT) {
            val avatar = avatarResIds[(i - 1) % avatarResIds.size]
            list.add(
                User(
                    id = i,
                    name = "用户$i",
                    douyinId = "douyin_$i",
                    avatarResId = avatar,
                    isFollowed = true,
                    isSpecial = false,
                    remark = ""
                )
            )
        }
        list
    }

    interface Callback {
        fun onResult(users: List<User>)
    }

    /**
     * 模拟分页接口：
     * page 从 0 开始，每页 pageSize 条
     * 使用 postDelayed 模拟 150ms 网络延迟
     */
    fun fetchUsersPage(page: Int, pageSize: Int = PAGE_SIZE, callback: Callback) {
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            val fromIndex = page * pageSize
            if (fromIndex >= TOTAL_COUNT) {
                callback.onResult(emptyList())
                return@postDelayed
            }
            val toIndex = min(fromIndex + pageSize, TOTAL_COUNT)
            val subList = allUsers.subList(fromIndex, toIndex)
            callback.onResult(subList)
        }, 150)  // 模拟一个比较快的网络响应
    }
}
