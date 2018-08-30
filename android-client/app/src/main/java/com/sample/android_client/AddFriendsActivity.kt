package com.sample.android_client

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.widget.Toast
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_add_friends.*

class AddFriendsActivity : AppCompatActivity() {
    val groupAdapter = GroupAdapter<ViewHolder>().apply {
        spanCount = 4
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_friends)

        recycler_view_add_friends.adapter = groupAdapter

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
    }

    private fun fetchSearchedUsers(keyword: String): MutableList<RoomItem> {
        val allUsers = fetchAllUsers()
        val searchedUsers = mutableListOf<RoomItem>()

        for (user in allUsers) {
            if (user.roomName.indexOf(keyword) >= 0) {
                searchedUsers.add(user)
            }
        }

        return searchedUsers
    }

    private fun fetchAllUsers(): MutableList<RoomItem> {
        // とりあえずダミーでユーザ全体のリストを作っている
        // TODO: データベースに登録されているユーザ全体を取ってくる処理を書く
        val allUsers = generateDummyUsersItems()
        return allUsers
    }

    private fun displaySearchedUsers(keyword: String) {
        recycler_view_add_friends.apply {
            layoutManager = GridLayoutManager(this@AddFriendsActivity, groupAdapter.spanCount).apply {
                spanSizeLookup = groupAdapter.spanSizeLookup
            }
            adapter = groupAdapter
        }
        groupAdapter.clear()

        val section = Section()

        section.removeHeader()

        val items = fetchSearchedUsers(keyword)
        section.addAll(items)

        groupAdapter.add(section)
    }

    private fun generateDummyUsersItems(): MutableList<RoomItem> {
        val dummyUsersItems = mutableListOf<RoomItem>()

        dummyUsersItems.add(RoomItem(0, "saito yuya", ""))
        dummyUsersItems.add(RoomItem(0, "suzuki yuto", ""))
        dummyUsersItems.add(RoomItem(0, "suzuki takuma", ""))
        dummyUsersItems.add(RoomItem(0, "honda keisuke", ""))
        dummyUsersItems.add(RoomItem(0, "kawasaki tomoya", ""))
        dummyUsersItems.add(RoomItem(0, "yamaha tarou", ""))

        return dummyUsersItems
    }
}
