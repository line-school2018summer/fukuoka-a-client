package com.sample.android_client

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_friend_friends.*


data class RoomItem(val roomId: Int,
                    val roomName: String,
                    val roomIconId: Int) : Item() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.user_name_textview_friends.text = roomName
        // TODO : 相手のアイコンまたはグループアイコンを表示する
    }

    override fun getLayout(): Int = R.layout.item_friend_friends

    override fun getSpanSize(spanCount: Int, position: Int): Int = spanCount / 4

}