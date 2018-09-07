package com.sample.android_client

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
import java.util.*

class FriendsFragment : Fragment() {
    private val groupAdapter = GroupAdapter<ViewHolder>().apply {
        spanCount = 4
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_friends, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
        }

        create_group_button_friends.setOnClickListener {
            val intent = Intent(activity, CreateGroupActivity::class.java)
            startActivity(intent)
        }
    }

    private fun displayGroupsAndFriends() {
        val groups = generateDummyGroupsItems(3)
        val friends = generateDummyFriendsItems(18)

        recycler_view_friends.apply {
            layoutManager = GridLayoutManager(activity, groupAdapter.spanCount).apply {
                spanSizeLookup = groupAdapter.spanSizeLookup
            }
            adapter = groupAdapter
        }

        ExpandableGroup(ExpandableHeaderItem("グループトーク"), true).apply {
            add(Section(groups))
            groupAdapter.add(this)
        }

        ExpandableGroup(ExpandableHeaderItem("個人間トーク"), true).apply {
            add(Section(friends))
            groupAdapter.add(this)
        }

        Log.d("FriendsFragment", groupAdapter.getItem(0).toString())

        groupAdapter.setOnItemClickListener { item, view ->
            Log.d("FriendsFragment", item.toString())
            // TODO: 選択したルームでのトークに遷移する
        }

    }

    // ダミーのグループリストを作成
    private fun generateDummyGroupsItems(number: Int): MutableList<RoomItem> {
        val rnd = Random()
        return MutableList(number) {
            RoomItem(0,"group" + rnd.nextInt(256).toString(), "hoge")
        }
    }

    // ダミーの友だちリストを作成
    private fun generateDummyFriendsItems(number: Int): MutableList<RoomItem> {
        val rnd = Random()
        return MutableList(number) {
            RoomItem(0,"user" + rnd.nextInt(256).toString(), "hoge")
        }
    }
}