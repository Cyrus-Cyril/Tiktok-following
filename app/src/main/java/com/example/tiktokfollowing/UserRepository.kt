package com.example.tiktokfollowing

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class UserRepository(private val context: Context) {

    private val spName = "follow_users_sp"
    private val keyUsers = "users"
    private val gson = Gson()

    fun loadUsers(): MutableList<User> {
        val sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE)
        val json = sp.getString(keyUsers, null)
        return if (json.isNullOrEmpty()) {
            val initList = createMockUsers()
            saveUsers(initList)
            initList
        } else {
            val type = object : TypeToken<MutableList<User>>() {}.type
            gson.fromJson(json, type)
        }
    }

    fun saveUsers(users: List<User>) {
        val sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE)
        val editor = sp.edit()
        val json = gson.toJson(users)
        editor.putString(keyUsers, json)
        editor.apply()
    }

    private fun createMockUsers(): MutableList<User> {
        return mutableListOf(
            User(1, "王心凌",   "CyndiWang905", R.drawable.avatar_1),
            User(2, "张韶涵",   "AngelaZhang01", R.drawable.avatar_2),
            User(3, "刘些宁",   "LiuXN777",      R.drawable.avatar_3),
            User(4, "蔡徐坤",   "KunCaiOfficial",R.drawable.avatar_4),
            User(5, "时代少年团", "TNTGroup",     R.drawable.avatar_5)
        )
    }

}
