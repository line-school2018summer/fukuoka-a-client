package com.sample.android_client

import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.ExpandableItem
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_expandable_header_friends.view.*

data class ExpandableHeaderItem(private val title: String) : Item(), ExpandableItem {
    private lateinit var expandableGroup: ExpandableGroup

    override fun getLayout(): Int = R.layout.item_expandable_header_friends

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.item_expandable_header_title_friends.text = title
        viewHolder.itemView.item_expandable_header_icon_friends.setImageResource(getRotatedIconResId())

        viewHolder.itemView.item_expandable_header_root_friends.setOnClickListener {
            expandableGroup.onToggleExpanded()
            viewHolder.itemView.item_expandable_header_icon_friends.setImageResource(getRotatedIconResId())
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