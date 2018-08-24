package com.sample.android_client

import android.util.Log
import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.ExpandableItem
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_expandable_header_friends.*
import kotlinx.android.synthetic.main.item_expandable_header_friends.view.*

data class ExpandableHeaderItem(private val groupName: String) : Item(), ExpandableItem {
    private lateinit var expandableGroup: ExpandableGroup

    override fun getLayout(): Int = R.layout.item_expandable_header_friends

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.group_name_textview_expandable_header_item.text = groupName

        if (groupName == "友だち") {
            viewHolder.group_icon_imageview_expandable_header_item.setImageResource(R.drawable.ic_people_black_24dp)
        }
        else {
            // TODO: グループのアイコンを表示させる
        }

        viewHolder.expand_icon_imageview_expandable_header_item.setImageResource(getRotatedIconResId())

        viewHolder.expand_icon_imageview_expandable_header_item.setOnClickListener {
            expandableGroup.onToggleExpanded()
            viewHolder.itemView.expand_icon_imageview_expandable_header_item.setImageResource(getRotatedIconResId())
        }

        viewHolder.item_expandable_header_root_friends.setOnClickListener {
            Log.d("FriendsFragment", "グループ名${groupName}のトーク画面に遷移")
        }
    }

    override fun setExpandableGroup(onToggleListener: ExpandableGroup) {
        expandableGroup = onToggleListener
    }

    private fun getRotatedIconResId(): Int {
        if (expandableGroup.isExpanded) {
            return R.drawable.ic_expand_less_black_24dp
        } else {
            return R.drawable.ic_expand_more_black_24dp
        }
    }
}