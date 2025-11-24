package com.example.tiktokfollowing

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class MainPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    private val titles = arrayOf("互关", "关注", "粉丝", "朋友")

    override fun getItemCount(): Int = titles.size

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            1 -> FollowFragment()
            0 -> SimpleTextFragment.newInstance("暂无互关")
            2 -> SimpleTextFragment.newInstance("暂无粉丝")
            3 -> SimpleTextFragment.newInstance("暂无朋友")
            else -> SimpleTextFragment.newInstance("")
        }
    }

    fun getPageTitle(position: Int): String = titles[position]
}
