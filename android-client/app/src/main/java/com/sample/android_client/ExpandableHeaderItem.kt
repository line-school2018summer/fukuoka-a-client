package com.sample.android_client

import android.util.Log
import android.view.View
import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.ExpandableItem
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_expandable_header_friends.*
import kotlinx.android.synthetic.main.item_expandable_header_friends.view.*

data class ExpandableHeaderItem(private val title: String
) : Item(), ExpandableItem {
    private lateinit var expandableGroup: ExpandableGroup

    override fun getLayout(): Int = R.layout.item_expandable_header_friends

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.group_name_textview_expandable_header_item.text = title

        viewHolder.expand_icon_imageview_expandable_header_item.setImageResource(getRotatedIconResId())

        viewHolder.item_expandable_header_root_friends.setOnClickListener {
            expandableGroup.onToggleExpanded()
            viewHolder.itemView.expand_icon_imageview_expandable_header_item.setImageResource(getRotatedIconResId())
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