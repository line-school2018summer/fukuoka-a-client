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
    val selectedUser = mutableListOf<SelectableUserItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_friends)

        recycler_view_add_friends.apply {
            layoutManager = GridLayoutManager(this@AddFriendsActivity, groupAdapter.spanCount).apply {
                spanSizeLookup = groupAdapter.spanSizeLookup
            }
            adapter = groupAdapter
            itemAnimator = null
        }

        supportActionBar?.title = "友だちを追加"

        // 最初は全ユーザを表示するようにする
        displaySearchedUsers("")

        search_button_add_friends.setOnClickListener {
            val keyword = search_box_add_friends.text.toString()
            Log.d("AddFriendsActivity", "文字列${keyword}が含まれるユーザを検索")
            displaySearchedUsers(keyword)
        }

        add_friends_fab.setOnClickListener {
            // TODO: 選んだユーザを友だちに追加する処理
            Toast.makeText(this, "友だちに追加しました！", Toast.LENGTH_SHORT).show()
            finish()
        }


        groupAdapter.setOnItemClickListener { item, view ->
            val sItem = item as SelectableUserItem
            if (sItem.isSelected) {
                selectedUser.remove(sItem)
                sItem.isSelected = false
            } else {
                selectedUser.add(sItem)
                sItem.isSelected = true
            }
            sItem.notifyChanged()
        }

    }

    private fun fetchSearchedUsers(keyword: String): MutableList<SelectableUserItem> {
        val allUsers = fetchAllUsers()
        val searchedUsers = mutableListOf<SelectableUserItem>()

        for (user in allUsers) {
            if (user.userName.indexOf(keyword) >= 0) {
                searchedUsers.add(user)
            }
        }

        return searchedUsers
    }

    private fun fetchAllUsers(): MutableList<SelectableUserItem> {
        // とりあえずダミーでユーザ全体のリストを作っている
        // TODO: データベースに登録されているユーザ全体を取ってくる処理を書く
        val allUsers = generateDummyUsersItems()
        return allUsers
    }

    private fun displaySearchedUsers(keyword: String) {
        groupAdapter.clear()
        selectedUser.clear()

        val items = fetchSearchedUsers(keyword)

        groupAdapter.addAll(items)
    }

    private fun generateDummyUsersItems(): MutableList<SelectableUserItem> {
        val dummyUsersItems = mutableListOf<SelectableUserItem>()

        dummyUsersItems.add(SelectableUserItem(0, "saito yuya", "", false))
        dummyUsersItems.add(SelectableUserItem(0, "suzuki yuto", "", false))
        dummyUsersItems.add(SelectableUserItem(0, "suzuki takuma", "", false))
        dummyUsersItems.add(SelectableUserItem(0, "honda keisuke", "", false))
        dummyUsersItems.add(SelectableUserItem(0, "kawasaki tomoya", "", false))
        dummyUsersItems.add(SelectableUserItem(0, "yamaha tarou", "", false))

        return dummyUsersItems
    }

    inner class SelectableUserItem(val userId: Long,
                                   val userName: String,
                                   val userIconURI: String,
                                   var isSelected: Boolean) : Item() {
        override fun bind(viewHolder: com.xwray.groupie.kotlinandroidextensions.ViewHolder, position: Int) {
            viewHolder.user_name_textview_friends.text = userName
            viewHolder.itemView.alpha = if (isSelected) 1f else 0.5f

            /*
            viewHolder.itemView.setOnClickListener {
                if (isSelected) {
                    selectedUser.remove(this)
                    isSelected = false
                    viewHolder.itemView.alpha = 0.5f
                } else {
                    selectedUser.add(this)
                    isSelected = true
                    viewHolder.itemView.alpha = 1f
                }

                // デバッグ用
                Log.d("AddFriendsActivity", selectedUser.toString())
            }
            */

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
