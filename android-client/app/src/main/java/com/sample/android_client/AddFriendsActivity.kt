package com.sample.android_client

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_add_friends.*
import kotlinx.android.synthetic.main.fragment_friends.*
import java.util.*

class AddFriendsActivity : AppCompatActivity() {
    val groupAdapter = GroupAdapter<ViewHolder>().apply {
        spanCount = 4
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_friends)

        recycler_view_add_friends.adapter = groupAdapter

        supportActionBar?.title = "友だちを検索"

        search_button_add_friends.setOnClickListener {
            val pattern = search_box_add_friends.text.toString()
            Log.d("AddFriendsActivity", "文字列${pattern}が含まれるユーザを検索")
            displaySearchedUsers()
        }
    }

    private fun displaySearchedUsers() {
        recycler_view_add_friends.apply {
            layoutManager = GridLayoutManager(this@AddFriendsActivity, groupAdapter.spanCount).apply {
                spanSizeLookup = groupAdapter.spanSizeLookup
            }
            adapter = groupAdapter
        }

        val section = Section()

        section.removeHeader()
        section.addAll(generateDummyUsersItems(12))

        groupAdapter.add(section)
    }

    private fun generateDummyUsersItems(number: Int): MutableList<RoomItem> {
        val rnd = Random()
        return MutableList(number) {
            RoomItem(0,"user" + rnd.nextInt(256).toString(), "hoge")
        }
    }
}
