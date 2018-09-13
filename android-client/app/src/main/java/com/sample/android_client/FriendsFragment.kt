package com.sample.android_client

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.text.Editable
import android.text.TextWatcher
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

const val EXTRA_ROOM_ID = "roomId"

class FriendsFragment : Fragment() {
    private val groupAdapter = GroupAdapter<ViewHolder>().apply {
        spanCount = 4
    }

    private lateinit var database: DBHelper

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

        val rooms = loadRooms()
        friends = rooms.filter { !(it.isGroup) }.map { it.toRoomItem() }
        groups = rooms.filter { it.isGroup }.map { it.toRoomItem() }

        displayGroupsAndFriends()

        search_friends_edittext_friends.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (p0.isNullOrEmpty()) {
                    displayGroupsAndFriends()
                    return
                }
                displaySearchedFriends(p0.toString())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        create_group_button_friends.setOnClickListener {
            val intent = Intent(activity, CreateGroupActivity::class.java)
            startActivity(intent)
        }

        delete_button_friends.setOnClickListener {
            search_friends_edittext_friends.text.clear()
        }

        groupAdapter.setOnItemClickListener { item, _ ->
            Log.d("FriendsFragment", item.toString())

            val roomId = (item as RoomItem).roomId
            val intent = Intent(activity, TalkActivity::class.java)
            intent.putExtra(EXTRA_ROOM_ID, roomId)

            startActivity(intent)
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
    }

    private fun loadRooms(): List<Room> {
        return database.use {
            this.select(ROOMS_TABLE_NAME).exec {
                val parser = rowParser { id: Int, serverId: Int, iconId: Int, name: String, isGroup: Int ->
                    Room(id, serverId, iconId, name, isGroup == 1)
                }
                parseList(parser)
            }
        }
    }
}