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
    private var userList: MutableList<User> = mutableListOf()

    // 分页相关字段
    private var currentPage = 0          // 当前页，从 0 开始
    private var isLoading = false        // 是否正在加载，避免重复请求
    private var hasMore = true           // 是否还有更多数据

    // 标记用户是否真的手动滑动过，用来避免刚进页面就自动加载后续页
    private var userHasScrolled = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_follow, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeRefresh = view.findViewById(R.id.swipeRefresh)
        rvUsers = view.findViewById(R.id.rvUsers)

        rvUsers.layoutManager = LinearLayoutManager(requireContext())
        rvUsers.setHasFixedSize(true)

        adapter = UserAdapter(userList, this)
        rvUsers.adapter = adapter

        // 首次进入：链式加载两页，避免界面下方太空
        loadNextPage {
            loadNextPage()
        }

        // 下拉刷新：重置分页状态并重新从“服务端”加载
        swipeRefresh.setOnRefreshListener {
            resetAndReload()
        }

        // 监听滚动，实现滑动到底自动加载更多
        rvUsers.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            // 用户开始拖动时标记一下，避免首屏自动触发
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                val lm = recyclerView.layoutManager as? LinearLayoutManager ?: return

                when (newState) {
                    RecyclerView.SCROLL_STATE_DRAGGING -> {
                        // 用户手指真的开始滑动了
                        userHasScrolled = true
                    }
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        // 只有在用户滑动过，并且此时停止在底部，才加载下一页
                        if (!userHasScrolled || isLoading || !hasMore) return

                        val lastCompletelyVisible = lm.findLastCompletelyVisibleItemPosition()
                        val totalCount = lm.itemCount

                        if (totalCount > 0 && lastCompletelyVisible == totalCount - 1) {
                            loadNextPage()
                        }
                    }
                    else -> { /* 其它状态不用处理 */ }
                }
            }
        })
    }

    // 重置分页并重新加载（这里同样加载两页，体验和首屏一致）
    private fun resetAndReload() {
        currentPage = 0
        hasMore = true
        userHasScrolled = false
        userList.clear()
        adapter.updateData(userList)

        // 先拉第一页，再拉第二页，最后结束刷新动画
        loadNextPage {
            loadNextPage {
                swipeRefresh.isRefreshing = false
            }
        }
    }

    // 从“服务端”加载下一页数据（每页 10 条，MockServer 内部实现）
    private fun loadNextPage(onFinished: (() -> Unit)? = null) {
        if (isLoading || !hasMore) {
            onFinished?.invoke()
            return
        }
        isLoading = true

        MockServer.fetchUsersPage(currentPage, 10, object : MockServer.Callback {
            override fun onResult(users: List<User>) {
                if (users.isEmpty()) {
                    hasMore = false
                } else {
                    val start = userList.size
                    userList.addAll(users)
                    // 增量刷新，避免整列表重绘
                    adapter.notifyItemRangeInserted(start, users.size)
                    currentPage++
                }
                isLoading = false
                onFinished?.invoke()
            }
        })
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
        val douyin = user.douyinId ?: ""
        tvDouyinId.text = "抖音号：$douyin"

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
                adapter.notifyItemChanged(position)
                Toast.makeText(requireContext(), "备注已更新", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("取消", null)
            .show()
    }
}
