package com.sample.android_client

import com.squareup.picasso.Picasso
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_friend_friends.*

const val LIMIT_DISPLAY_NAME_LENGTH = 13

data class RoomItem(val roomId: Int,
                    val roomName: String,
                    val roomIconId: Int) : Item() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.user_name_textview_friends.text = if (roomName.length <= LIMIT_DISPLAY_NAME_LENGTH) {
            roomName
        } else {
            roomName.substring(0..10) + ".."
        }

        // TODO: 友達またはグループのアイコンのURLを取得する

        val defaultIconURL = "https://firebasestorage.googleapis.com/v0/b/fukuoka-a-client.appspot.com/o/image%2Fdman.png?alt=media&token=ca1deb08-e413-4b37-b925-e07b39232e35"
        
        Picasso.get()
                .load(defaultIconURL)
                .fit()
                .centerCrop()
                .into(viewHolder.circular_imageview_item_friend)
    }

    override fun getLayout(): Int = R.layout.item_friend_friends

    override fun getSpanSize(spanCount: Int, position: Int): Int = spanCount / 4

}