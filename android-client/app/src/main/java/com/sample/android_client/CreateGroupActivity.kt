package com.sample.android_client

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
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
    val selectedUsers = mutableListOf<SelectableUserItem>()     // 選択されたユーザのリスト

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_group)

        supportActionBar?.title = "新しいグループを作る"

        recycler_view_create_group.apply {
            layoutManager = GridLayoutManager(this@CreateGroupActivity, groupAdapter.spanCount).apply {
                spanSizeLookup = groupAdapter.spanSizeLookup
            }
            adapter = groupAdapter
            itemAnimator = null
        }

        search_button_create_group.setOnClickListener {
            val keyword = search_box_create_group.text.toString()
            Log.d("CreateGroupActivity", "文字列${keyword}が名前に含まれるユーザを検索")

            // TODO: ローカルDBからkeywordを名前に含むユーザを引っ張ってきてリストを作成する
            displaySearchedUser(keyword)
        }

        fab_create_group.setOnClickListener {
            // TODO: 新しくグループを作ってサーバのDBに登録する処理
            // TODO: ローカルDBに登録する処理
        }

        delete_button_create_group.setOnClickListener {
            search_box_create_group.text.clear()
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
        val searchedUsers = generateDummyUsersItems().filter { item -> item.userName.indexOf(keyword) >= 0 }
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
            // TODO : 友達のアイコンを表示する
        }

        override fun getLayout(): Int = R.layout.item_friend_friends

        override fun getSpanSize(spanCount: Int, position: Int): Int = spanCount / 4

        // デバッグ用
        override fun toString(): String {
            return userName
        }
    }
}
