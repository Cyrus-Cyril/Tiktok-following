package com.example.tiktokfollowing

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import android.content.res.ColorStateList
import android.graphics.Color


class UserAdapter(
    private val users: MutableList<User>,
    private val listener: OnUserActionListener
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    interface OnUserActionListener {
        fun onFollowClick(user: User, position: Int)
        fun onMoreClick(user: User, position: Int, anchorView: View)
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivAvatar: ImageView = itemView.findViewById(R.id.ivAvatar)
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvRemark: TextView = itemView.findViewById(R.id.tvRemark)
        val btnFollow: Button = itemView.findViewById(R.id.btnFollow)
        val btnMore: ImageButton = itemView.findViewById(R.id.btnMore)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun getItemCount(): Int = users.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.ivAvatar.setImageResource(user.avatarResId)

        // 显示逻辑：
        // 有备注：大字显示备注，小字显示原始 name
        // 无备注：大字显示 name，小字隐藏
        if (user.remark.isNotEmpty()) {
            holder.tvName.text = user.remark          // 大字 = 备注
            holder.tvRemark.visibility = View.VISIBLE
            holder.tvRemark.text = user.name          // 小字 = 原始 id/name
        } else {
            holder.tvName.text = user.name            // 只有原始 id
            holder.tvRemark.visibility = View.GONE
            holder.tvRemark.text = ""                 // 防止复用时残留
        }

        val context = holder.btnFollow.context

        if (user.isFollowed) {
            holder.btnFollow.text = "已关注"
            // 已关注：灰底黑字
            holder.btnFollow.setTextColor(Color.BLACK)
            holder.btnFollow.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#EEEEEE"))
        } else {
            holder.btnFollow.text = "关注"
            // 未关注：红底白字
            holder.btnFollow.setTextColor(Color.WHITE)
            holder.btnFollow.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#FF3B30"))
        }


        // 特别关注高亮逻辑保持不变
        if (user.isSpecial) {
            holder.tvName.setTextColor(0xFFE91E63.toInt())
        } else {
            holder.tvName.setTextColor(0xFF000000.toInt())
        }

        holder.btnFollow.setOnClickListener {
            listener.onFollowClick(user, position)
        }

        holder.btnMore.setOnClickListener {
            listener.onMoreClick(user, position, holder.btnMore)
        }
    }


    fun updateData(newUsers: MutableList<User>) {
        users.clear()
        users.addAll(newUsers)
        notifyDataSetChanged()
    }

    fun removeAt(position: Int) {
        users.removeAt(position)
        notifyItemRemoved(position)
    }
}
