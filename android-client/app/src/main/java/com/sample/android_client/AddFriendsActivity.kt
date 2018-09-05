package com.sample.android_client

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.widget.Toast
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.activity_add_friends.*
import kotlinx.android.synthetic.main.item_friend_friends.*

class AddFriendsActivity : AppCompatActivity() {
    val groupAdapter = GroupAdapter<ViewHolder>().apply {
        spanCount = 4
    }
    val selectedUsers = mutableListOf<SelectableUserItem>()  // 現在アプリ上で選択されているユーザ
    var allUsers = listOf<SelectableUserItem>()       // DBに登録されているユーザ全員

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_friends)

        // Activity生成時に一回だけやればよい
        fetchAllUsers()

        recycler_view_add_friends.apply {
            layoutManager = GridLayoutManager(this@AddFriendsActivity, groupAdapter.spanCount).apply {
                spanSizeLookup = groupAdapter.spanSizeLookup
            }
            adapter = groupAdapter
            itemAnimator = null
        }

        supportActionBar?.title = "友だちを追加"

        search_button_add_friends.setOnClickListener {
            val keyword = search_box_add_friends.text.toString()
            Log.d("AddFriendsActivity", "文字列${keyword}が含まれるユーザを検索")
            displaySearchedUser(keyword)
        }

        add_friends_fab.setOnClickListener {
            if (selectedUsers.isEmpty()) {
                Toast.makeText(this, "友だちが選択されていません", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // TODO: 選んだユーザを友だちに追加する処理
            val numberSelected = selectedUsers.size
            Toast.makeText(this, "${numberSelected}人を友だちに追加しました！", Toast.LENGTH_LONG).show()
            finish()
        }

        delete_button_add_friends.setOnClickListener {
            search_box_add_friends.text.clear()
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
        }

    }

    private fun updateGuideTextview() {
        val numberSelected = selectedUsers.size
        if (numberSelected > 0) {
            guide_textview_add_friends.text = "友だちになりたい人を選んでください(${numberSelected}人選択中)"
        } else {
            guide_textview_add_friends.text = "友だちになりたい人を選んでください"
        }
    }

    // 友だちを検索するときは、ユーザが決める一意なIDで検索させる
    // 悪用されないように完全一致にする
    // 一致するユーザが見つからないときはnullを返す
    private fun fetchSearchedUsers(keyword: String): SelectableUserItem? =
            allUsers.singleOrNull { it.userId == keyword }


    private fun fetchAllUsers() {
        // とりあえずダミーでユーザ全体のリストを作っている
        // TODO: データベースに登録されているユーザ全体を取ってくる処理を書く
        allUsers = generateDummyUsersItems()
    }

    private fun displaySearchedUser(keyword: String) {
        val item = fetchSearchedUsers(keyword)

        if (item == null) {
            Toast.makeText(this, "一致するユーザが見つかりません", Toast.LENGTH_LONG).show()
            return
        }

        groupAdapter.clear()
        groupAdapter.add(item)
        updateGuideTextview()
    }

    private fun generateDummyUsersItems(): List<SelectableUserItem> {
        val dummyUserItems = listOf<SelectableUserItem>(
                SelectableUserItem("a", "saito yuya", 0),
                SelectableUserItem("b", "suzuki yuto", 0),
                SelectableUserItem("c", "suzuki takuma", 0),
                SelectableUserItem("d", "honda keisuke", 0),
                SelectableUserItem("e", "kawasaki tomoya", 0),
                SelectableUserItem("f", "yamaha tarou", 0)
        )

        return dummyUserItems
    }

    inner class SelectableUserItem(val userId: String,      // Userに登録させる一意なID
                                   val userName: String,    // Userが表示したい名前
                                   val userIconId: Int,
                                   var isSelected: Boolean = false) : Item() {
        override fun bind(viewHolder: com.xwray.groupie.kotlinandroidextensions.ViewHolder, position: Int) {
            viewHolder.user_name_textview_friends.text = userName
            viewHolder.itemView.alpha = if (isSelected) 1f else 0.6f
            // TODO : 友だちのアイコンを表示する
        }

        override fun getLayout(): Int = R.layout.item_friend_friends

        override fun getSpanSize(spanCount: Int, position: Int): Int = spanCount / 4

        // デバッグ用
        override fun toString(): String {
            return userName
        }
    }
}
