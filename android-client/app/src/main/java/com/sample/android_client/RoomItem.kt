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

        // TODO : 相手のアイコンまたはグループアイコンを表示する
        Picasso.get()
                .load("https://firebasestorage.googleapis.com/v0/b/fukuoka-a-client.appspot.com/o/image%2F44f40371-93f2-4fcc-a698-329c565896ab?alt=media&token=d2971410-fad5-4c31-9f00-ef9f87ee9b61")
                .into(viewHolder.circular_imageview_item_friend)
    }

    override fun getLayout(): Int = R.layout.item_friend_friends

    override fun getSpanSize(spanCount: Int, position: Int): Int = spanCount / 4

}