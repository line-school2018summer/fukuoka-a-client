package com.sample.android_client

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_friends.*
import org.jetbrains.anko.db.parseList
import org.jetbrains.anko.db.rowParser
import org.jetbrains.anko.db.select

class FriendsFragment : Fragment() {
    private lateinit var database: DBHelper
    private val groupAdapter = GroupAdapter<ViewHolder>().apply {
        spanCount = 4
    }
    private lateinit var rooms: List<Room>
    private lateinit var friends: List<RoomItem>
    private lateinit var groups: List<RoomItem>

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        database = DBHelper.getInstance(activity!!.applicationContext)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_friends, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler_view_friends.apply {
            layoutManager = GridLayoutManager(activity, groupAdapter.spanCount).apply {
                spanSizeLookup = groupAdapter.spanSizeLookup
            }
            adapter = groupAdapter
        }

        // 友達画面に遷移してきたときに一回だけやればOKのはず
        getRooms()
        createFriendItems()
        createGroupItems()

        displayGroupsAndFriends()

        create_group_button_friends.setOnClickListener {
            Log.d("FriendsFragment", "新しいグループを作成する")
            // TODO: 新しいグループを作成する処理を書く
            // 新しいアクティビティ(CreateGroupActivity)を作ってそこに飛ぶか
        }

        search_friends_button_friends.setOnClickListener {
            val text = search_friends_edittext_friends.text.toString()
            Log.d("FriendsFragment", "文字列${text}を含むユーザーを検索する")
            // TODO: 文字列textを含むユーザーを検索してリストアップする処理を書く
            // アクティビティを分けて、そっちに遷移するようにするのが楽そうだけど
            // このページ内でシュッっと画面が切り替わるようにしたほうがかっこいい
            // 文字を入力するたびリアルタイムで検索していくとかもかっこいいけど難しそう
            displaySearchedFriends(text)
        }

        create_group_button_friends.setOnClickListener {
            val intent = Intent(activity, CreateGroupActivity::class.java)
            startActivity(intent)
        }

        groupAdapter.setOnItemClickListener { item, view ->
            Log.d("FriendsFragment", item.toString())
            // TODO: 選択したルームでのトークに遷移する
        }
    }

    private fun displaySearchedFriends(keyword: String) {
        if (keyword.isEmpty()) {
            displayGroupsAndFriends()
            return
        }

        groupAdapter.clear()

        val searchedFriends = friends.filter { it.roomName.indexOf(keyword) >= 0 }

        ExpandableGroup(ExpandableHeaderItem("個人間トーク"), true).apply {
            add(Section(searchedFriends))
            groupAdapter.add(this)
        }
    }

    private fun displayGroupsAndFriends() {
        groupAdapter.clear()

        ExpandableGroup(ExpandableHeaderItem("グループトーク"), true).apply {
            add(Section(groups))
            groupAdapter.add(this)
        }

        ExpandableGroup(ExpandableHeaderItem("個人間トーク"), true).apply {
            add(Section(friends))
            groupAdapter.add(this)
        }

        Log.d("FriendsFragment", groupAdapter.getItem(0).toString())
    }

    private fun getRooms() {
        database.use {
            rooms = this.select(ROOMS_TABLE_NAME).exec {
                val parser = rowParser { id: Int, serverId: Int, iconId: Int, name: String, isGroup: Int ->
                    Room(id, serverId, iconId, name, isGroup == 1)
                }
                parseList(parser)
            }
        }
        Log.d("FriendsFragment", rooms.size.toString())
    }

    private fun createGroupItems() {
        groups = rooms.filter { it.isGroup }
                .map { it -> RoomItem(it.id, it.name, it.iconId) }
    }

    private fun createFriendItems() {
        friends = rooms.filter { !(it.isGroup) }
                .map { it -> RoomItem(it.id, it.name, it.iconId) }
    }
}