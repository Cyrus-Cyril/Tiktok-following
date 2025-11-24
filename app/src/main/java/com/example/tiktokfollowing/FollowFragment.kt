package com.example.tiktokfollowing

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.bottomsheet.BottomSheetDialog

import androidx.appcompat.widget.SwitchCompat
import android.widget.TextView


class FollowFragment : Fragment(),
    UserAdapter.OnUserActionListener {

    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var rvUsers: RecyclerView
    private lateinit var adapter: UserAdapter
    private lateinit var repo: UserRepository
    private var userList: MutableList<User> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 直接使用 fragment_follow.xml
        return inflater.inflate(R.layout.fragment_follow, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeRefresh = view.findViewById(R.id.swipeRefresh)
        rvUsers = view.findViewById(R.id.rvUsers)

        rvUsers.layoutManager = LinearLayoutManager(requireContext())
        repo = UserRepository(requireContext())
        userList = repo.loadUsers()

        adapter = UserAdapter(userList, this)
        rvUsers.adapter = adapter

        swipeRefresh.setOnRefreshListener {
            userList = repo.loadUsers()
            adapter.updateData(userList)
            swipeRefresh.isRefreshing = false
        }
    }

    // 点击“关注/已关注”按钮
    override fun onFollowClick(user: User, position: Int) {
        if (user.isFollowed) {
            user.isFollowed = false
            Toast.makeText(requireContext(), "已取消关注", Toast.LENGTH_SHORT).show()
        } else {
            user.isFollowed = true
            Toast.makeText(requireContext(), "已关注", Toast.LENGTH_SHORT).show()
        }
        repo.saveUsers(userList)
        adapter.notifyItemChanged(position)
    }

    // 点击更多“···”按钮
    override fun onMoreClick(user: User, position: Int, anchorView: View) {
        showBottomSheet(user, position)
    }

    private fun showBottomSheet(user: User, position: Int) {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.bottom_user_actions, null)
        dialog.setContentView(view)

        // 头部信息：用户名 + 抖音号
        val tvUserName = view.findViewById<TextView>(R.id.tvUserName)
        val tvDouyinId = view.findViewById<TextView>(R.id.tvDouyinId)
        tvUserName.text = if (user.remark.isNotEmpty()) user.remark else user.name
        tvDouyinId.text = "抖音号：${user.douyinId}"

        // 特别关注
        val layoutSpecial = view.findViewById<View>(R.id.layoutSpecial)
        val switchSpecial = view.findViewById<SwitchCompat>(R.id.switchSpecial)

        // 设置备注
        val layoutRemark = view.findViewById<View>(R.id.layoutRemark)

        // 取消关注
        val tvUnfollow = view.findViewById<TextView>(R.id.tvUnfollow)

        // 初始化开关状态
        switchSpecial.isChecked = user.isSpecial

        // 切换特别关注（切换开关）
        switchSpecial.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked == user.isSpecial) return@setOnCheckedChangeListener

            user.isSpecial = isChecked
            repo.saveUsers(userList)
            adapter.notifyItemChanged(position)

            Toast.makeText(
                requireContext(),
                if (isChecked) "已设为特别关注" else "已取消特别关注",
                Toast.LENGTH_SHORT
            ).show()
        }

        // 点击整行“特别关注”也能切换
        layoutSpecial.setOnClickListener {
            switchSpecial.isChecked = !switchSpecial.isChecked
        }

        // 设置备注：点击整行
        layoutRemark.setOnClickListener {
            dialog.dismiss()
            showRemarkDialog(user, position)
        }

        // 取消关注
        tvUnfollow.setOnClickListener {
            user.isFollowed = false
            repo.saveUsers(userList)
            adapter.notifyItemChanged(position)
            Toast.makeText(requireContext(), "已取消关注", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialog.show()
    }




    private fun showRemarkDialog(user: User, position: Int) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_remark, null)
        val etRemark = dialogView.findViewById<EditText>(R.id.etRemark)
        etRemark.setText(user.remark)

        AlertDialog.Builder(requireContext())
            .setTitle("设置备注")
            .setView(dialogView)
            .setPositiveButton("确定") { _, _ ->
                user.remark = etRemark.text.toString()
                repo.saveUsers(userList)
                adapter.notifyItemChanged(position)
                Toast.makeText(requireContext(), "备注已更新", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("取消", null)
            .show()
    }
}
