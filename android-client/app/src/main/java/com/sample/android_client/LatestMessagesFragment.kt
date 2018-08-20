package com.sample.android_client


import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.fragment_latest_messages.*
import kotlinx.android.synthetic.main.message_row_latest_messages.*
import java.util.*


class LatestMessagesFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_latest_messages, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        displayLatestMessages()
    }

    private fun displayLatestMessages() {
        val adapter = GroupAdapter<ViewHolder>()

        val section = Section()

        section.removeHeader() // ヘッダーは不要

        val dummyRowItems = generateDummyMessageRowItems(12)

        // 新しいメッセージが上に来るようにソートする
        dummyRowItems.sortBy { it.secondsAgo }

        section.addAll(dummyRowItems)
        adapter.add(section)

        adapter.setOnItemClickListener { item, view ->
            Log.d("LatestMessagesFragment", item.toString())
            // 選択した相手とのTalk画面に遷移する
        }

        recycler_view_latest_messages.adapter = adapter
    }

    // ダミーアイテムの作成
    private fun generateDummyMessageRowItems(number: Int) : MutableList<MessageRowItem> {
        val rnd = Random()
        return MutableList(number) {
            MessageRowItem("user" + rnd.nextInt(256), "hoge", "message", rnd.nextInt(1024).toLong())
        }
    }
}

data class MessageRowItem(val userName: String,
                         val userIconURI: String,
                         val message: String,
                         val secondsAgo: Long)
    : Item() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.user_name_message_row.text = userName
        viewHolder.text_view_message_row.text = message
        // TODO : 長すぎるメッセージを省略する処理が必要
        viewHolder.seconds_ago_message_row.text = secondsAgo.toString() + "秒前"
        // TODO : 「秒前」をsecondsAgoに応じて「分前」「時間前」「日前」などに変化させる
    }

    override fun getLayout(): Int = R.layout.message_row_latest_messages
}