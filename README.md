# Tiktok-following

本项目是使用 **Android Studio + Kotlin** 实现的一个简易抖音关注列表页面 Demo，重点还原抖音「关注」页的基础交互与数据管理，用于课程作业与 Android 入门练习。

---
Tiktok-following
## 功能概述

### 1. 多页签顶部导航

- 使用 **TabLayout + ViewPager2** 构建顶部导航。
- 共 4 个标签页：`互关 / 关注 / 粉丝 / 朋友`。
- 支持左右滑动切换页面。
- 当前只实现「关注」页的完整功能，其余页面仅显示居中文本：
  - 互关：`暂无互关`
  - 粉丝：`暂无粉丝`
  - 朋友：`暂无朋友`

### 2. 关注列表（关注页）

- 使用 `RecyclerView` 纵向展示关注用户列表。
- 每一行包含：
  - 用户头像
  - 用户昵称/备注
  - 「关注/已关注」按钮（未关注为红底白字，已关注为灰底）
  - 右侧 `...` 更多按钮，点击弹出底部操作面板。
- 支持下拉刷新（`SwipeRefreshLayout`），重新从本地数据加载列表。

### 3. 底部操作面板（BottomSheet）

点击某用户右侧 `...` 按钮后弹出 BottomSheet，内容包含：

1. **头部信息**
   - 大标题：用户昵称（如果设置了备注，则显示备注）
   - 副标题：`抖音号：xxxxxx`（从用户数据字段中读取）

2. **特别关注**
   - 左侧文字：「特别关注」+ 描述文本（“作品优先推荐，更新及时提示”）
   - 右侧为滑动开关 `SwitchCompat`，样式模拟抖音绿色/灰色开关。
   - 支持：
     - 直接切换开关
     - 点击整行「特别关注」也会切换开关
   - 状态变化会实时写回本地，并刷新列表显示（例如名称高亮等）。

3. **设置备注**
   - 左侧文字：「设置备注」
   - 右侧显示铅笔图标。
   - 点击整行后弹出 AlertDialog，输入备注：
     - 备注保存到用户的 `remark` 字段
     - 列表展示逻辑：
       - 若有备注：列表大字显示备注，小字显示原始昵称
       - 无备注：只显示原始昵称

4. **取消关注**
   - 底部一行红色文字「取消关注」。
   - 点击后将该用户 `isFollowed` 置为 `false`，刷新列表，并弹出 Toast 提示。

### 4. 数据结构

- 所有用户数据（含特别关注、备注、关注状态）通过 **SharedPreferences + Gson** 本地持久化。
- 启动时优先从 SharedPreferences 读取，如无数据则构造默认的模拟数据。
- 数据模型 `User`：

  ```kotlin
  data class User(
      val id: Int,
      var name: String,
      var douyinId: String?,      // 抖音号
      var avatarResId: Int,       // 头像资源 id
      var isFollowed: Boolean = true,
      var isSpecial: Boolean = false,
      var remark: String = ""     // 备注
  )
