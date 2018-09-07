package com.sample.android_client

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.Toast
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.activity_create_group.*
import kotlinx.android.synthetic.main.item_friend_friends.*

class CreateGroupActivity : AppCompatActivity() {
    val groupAdapter = GroupAdapter<ViewHolder>().apply {
        spanCount = 4
    }
    val hGroupAdapter = GroupAdapter<ViewHolder>()
    lateinit var dummyUserItems: MutableList<SelectableUserItem>
    val selectedUsers = mutableListOf<SelectableUserItem>()     // 選択されたユーザのリスト

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_group)

        supportActionBar?.title = "新しいグループを作る"

        generateDummyUsersItems()

        recycler_view_create_group.apply {
            layoutManager = GridLayoutManager(this@CreateGroupActivity, groupAdapter.spanCount).apply {
                spanSizeLookup = groupAdapter.spanSizeLookup
            }
            adapter = groupAdapter
            itemAnimator = null
        }


        horizontal_recycler_view_create_group.apply {
            layoutManager = LinearLayoutManager(this@CreateGroupActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = hGroupAdapter
        }

        search_button_create_group.setOnClickListener {
            val keyword = search_box_create_group.text.toString()
            Log.d("CreateGroupActivity", "文字列${keyword}が名前に含まれるユーザを検索")
            displaySearchedUser(keyword)
        }

        fab_create_group.setOnClickListener {
            // TODO: 新しくグループを作ってサーバのDBに登録する処理
            // TODO: ローカルDBに登録する処理
        }

        delete_button_create_group.setOnClickListener {
            search_box_create_group.text.clear()
        }

        groupAdapter.setOnItemClickListener { item, view ->
            val sItem = item as SelectableUserItem
            if (sItem.isSelected) {
                selectedUsers.remove(sItem)
                sItem.isSelected = false
            } else {
                selectedUsers.add(sItem)
                sItem.isSelected = true
            }
            sItem.notifyChanged()
            updateGuideTextview()
            updateScrollView()
        }
    }

    private fun updateScrollView() {
        hGroupAdapter.clear()
        for (user in selectedUsers) {
            hGroupAdapter.add(ScrollUserItem(user.userId, user.userName, user.userIconId))
        }
    }

    private fun updateGuideTextview() {
        val numberSelected = selectedUsers.size
        if (numberSelected > 0) {
            guide_textview_create_group.text = "グループに追加したい人を選んでください(${numberSelected}人選択中)"
        } else {
            guide_textview_create_group.text = "グループに追加したい人を選んでください"
        }
    }

    // グループ追加で検索するときは、自分の友達一覧から検索する
    // ユーザが決める一意なIDではなく、表示している名前の部分一致で検索する
    private fun fetchSearchedUsers(keyword: String): List<SelectableUserItem> {
        // TODO: ダミーデータじゃなくて、ローカルDBを検索して同様のことをする
        val searchedUsers = dummyUserItems.filter { item -> item.userName.indexOf(keyword) >= 0 }
        return searchedUsers
    }

    private fun displaySearchedUser(keyword: String) {
        val items = fetchSearchedUsers(keyword)
        if (items.isEmpty()) {
            Toast.makeText(this, "一致するユーザが見つかりません", Toast.LENGTH_LONG).show()
            return
        }
        groupAdapter.clear()
        groupAdapter.addAll(items)
        updateGuideTextview()
    }

    private fun generateDummyUsersItems() {
        dummyUserItems = mutableListOf<SelectableUserItem>(
                SelectableUserItem("a", "saito yuya", 0),
                SelectableUserItem("b", "suzuki yuto", 0),
                SelectableUserItem("c", "suzuki takuma", 0),
                SelectableUserItem("d", "honda keisuke", 0),
                SelectableUserItem("e", "kawasaki tomoya", 0),
                SelectableUserItem("f", "yamaha tarou", 0)
        )
        for (i in 1..20)
            dummyUserItems.add(SelectableUserItem("b", "suzuki yuto", 0))
    }

    inner class SelectableUserItem(val userId: String,      // Userに登録させる一意なID
                                   val userName: String,    // Userが表示したい名前
                                   val userIconId: Int,
                                   var isSelected: Boolean = false) : Item() {
        override fun bind(viewHolder: com.xwray.groupie.kotlinandroidextensions.ViewHolder, position: Int) {
            viewHolder.user_name_textview_scroll.text = userName
            viewHolder.itemView.alpha = if (isSelected) 1f else 0.6f
            // TODO : 友達のアイコンを表示する
        }

        override fun getLayout(): Int = R.layout.item_friend_friends

        override fun getSpanSize(spanCount: Int, position: Int): Int = spanCount / 4

        // デバッグ用
        override fun toString(): String {
            return userName
        }
    }

    inner class ScrollUserItem(val userId: String,
                               val userName: String,
                               val userIconId: Int) : Item() {
        override fun bind(viewHolder: ViewHolder, position: Int) {
            val limitLength = 10
            val displayName = if (userName.length <= limitLength) userName else userName.substring(0 until limitLength - 1) + "..."
            viewHolder.user_name_textview_scroll.text = displayName
        }

        override fun getLayout(): Int = R.layout.item_user_scroll
    }
}
