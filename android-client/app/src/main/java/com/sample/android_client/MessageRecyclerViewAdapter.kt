package com.sample.android_client

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

class MessageRecyclerViewAdapter(private val messageList: MutableList<Message>)
    : RecyclerView.Adapter<MessageRecyclerViewAdapter.BaseViewHolder>() {

    private val messages: MutableList<Message> = messageList

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun getItemViewType(position: Int): Int {

        //TODO 送信者が自分以外であるかを判定するようにする
        return when (messageList[position].userId) {
            1 ->
                R.layout.message_text_left
            2 ->
                R.layout.message_text_right
            else ->
                throw RuntimeException()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)

        return when (viewType) {
            R.layout.message_text_right -> {
                BaseViewHolder(view)
            }

            R.layout.message_text_left -> {
                LeftViewHolder(view)
            }
            else -> throw RuntimeException()
        }
    }

    open inner class BaseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val message: TextView = view.findViewById(R.id.message_text)
        private val sendTime: TextView = view.findViewById(R.id.send_time)

        open fun bind(position: Int) {
            message.text = messages[position].body
            sendTime.text = messages[position].postedAt.toString()
        }
    }

    inner class LeftViewHolder(view: View) : BaseViewHolder(view) {
        private val senderIcon: ImageView = view.findViewById(R.id.sender_icon)

        override fun bind(position: Int) {
            super.bind(position)
            senderIcon.setImageResource(R.mipmap.ic_launcher)
        }
    }
}