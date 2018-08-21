package com.sample.android_client

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
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
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_friends, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        displayGroupsAndFriends()
    }

    private fun displayGroupsAndFriends() {
        val group1 = generateDummyFriendsItems(3)
        val group2 = generateDummyFriendsItems(9)
        val allFriends = generateDummyFriendsItems(18)

        val groupAdapter = GroupAdapter<ViewHolder>().apply {
            spanCount = 4
        }

        recycler_view_friends.apply {
            layoutManager = GridLayoutManager(activity, groupAdapter.spanCount).apply {
                spanSizeLookup = groupAdapter.spanSizeLookup
            }
            adapter = groupAdapter
        }

        ExpandableGroup(ExpandableHeaderItem("Group 1"), true).apply {
            add(Section(group1))
            groupAdapter.add(this)
        }

        ExpandableGroup(ExpandableHeaderItem("Group 2"), true).apply {
            add(Section(group2))
            groupAdapter.add(this)
        }

        ExpandableGroup(ExpandableHeaderItem("All your friends"), true).apply {
            add(Section(allFriends))
            groupAdapter.add(this)
        }

    }


    private fun generateDummyFriendsItems(number: Int): MutableList<FriendItem> {
        val rnd = Random()
        return MutableList(number) {
            FriendItem("user" + rnd.nextInt(256).toString(), "hoge")
        }
    }
}