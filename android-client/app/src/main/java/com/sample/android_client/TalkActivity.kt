package com.sample.android_client

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.Toast
import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.components.RxActivity
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.toCompletable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_talk.*
import org.jetbrains.anko.db.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.net.SocketTimeoutException
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

class TalkActivity : RxActivity() {
    private val Context.database: DBHelper
        get() = DBHelper.getInstance(applicationContext)

    // TODO Activity起動時に代入するように変更する
    private var roomId = -1
    private val newMessages = mutableListOf<Message>()
    private val talkAdapter = MessageRecyclerViewAdapter()

    private val serverAPI = Retrofit.Builder()
            .baseUrl("http://ec2-13-114-207-18.ap-northeast-1.compute.amazonaws.com")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ServerAPI::class.java)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_talk)

        roomId = intent.getIntExtra(EXTRA_ROOM_ID, -1)
        if (roomId == -1) {
            RuntimeException("ルームIdが取得できませんでした")
        }

        talk_recycler_view.adapter = talkAdapter
        talk_recycler_view.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        send_button_talk.setOnClickListener {
            val message = input_message_box_talk.text.toString()
            // TODO: message送信処理

            if (message.isNotEmpty()) {
                serverAPI.postNewMessage(1, roomId, message)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeBy(
                                onNext = {
                                    input_message_box_talk.text.clear()
                                },
                                onError = {
                                    Log.d("TalkActivity", it.toString())
                                    if (it is SocketTimeoutException) {
                                        Toast.makeText(applicationContext, "メッセージの送信に失敗しました", Toast.LENGTH_LONG).show()
                                        Log.d("TalkActivity", "送信に失敗")
                                    } else {
                                        throw it
                                    }
                                }
                        )
            }
        }
    }

    override fun onResume() {
        super.onResume()

        Log.d("TalkActivity", "onResume")
        newMessages.clear()

        val pastMessages = loadPastMessages()
        talkAdapter.setMessages(pastMessages)
        talk_recycler_view.scrollToPosition(talkAdapter.itemCount - 1)

        fetchNewMessages()
                .observeOn(AndroidSchedulers.mainThread())
                .retryWhen {
                    it.flatMap {
                        Log.d("TalkActivity", "retry")
                        Toast.makeText(applicationContext, "Can't fetch new message...", Toast.LENGTH_SHORT).show()
                        Observable.timer(3, TimeUnit.SECONDS)
                                .bindUntilEvent(this@TalkActivity, ActivityEvent.PAUSE)
                    }

                }
                .subscribeBy(
                        onNext = { fetchedMessages ->
                            newMessages.addAll(fetchedMessages)
                            talkAdapter.insertNewMessages(fetchedMessages)

                            Log.d("TalkActivity", "onNext")
                            talk_recycler_view.scrollToPosition(talkAdapter.itemCount - 1)
                        }
                )
    }

    override fun onPause() {
        super.onPause()

        Log.d("TalkActivity", "onPause")
        saveNewMessages()?.subscribe()
    }

    private fun saveNewMessages(): Completable? {
        if (newMessages.size == 0) {
            return null
        }

        return {
            database.use {
                this.transaction {
                    newMessages.forEach {
                        this.insert(MESSAGES_TABLE_NAME,
                                "server_id" to it.serverId,
                                "room_id" to it.roomId,
                                "user_id" to it.userId,
                                "body" to it.body,
                                "posted_at" to it.postedAt.toString())
                    }
                }
            }
        }.toCompletable().subscribeOn(Schedulers.io())

    }

    private fun fetchNewMessages(): Observable<Sequence<Message>> {
        return Observable.interval(1, TimeUnit.SECONDS)
                .bindUntilEvent(this, ActivityEvent.PAUSE)
                .subscribeOn(Schedulers.io())
                .flatMap { serverAPI.fetchAllMessages() }
                .map {
                    it.asSequence()
                            .filter { it.roomId == roomId }
                            .drop(talkAdapter.itemCount)
                            .map { it.toMessage() }
                }
    }

    private fun loadPastMessages(): List<Message> {
        var pastMessages = listOf<Message>()

        database.use {
            pastMessages = this.select(MESSAGES_TABLE_NAME, "server_id", "room_id", "user_id", "body", "posted_at")
                    .whereArgs("room_id = {roomId}", "roomId" to roomId)
                    .exec {
                        var parser = rowParser { serverId: Int, roomId: Int, userId: Int, body: String, postedAt: String ->
                            Message(serverId, roomId, userId, body, toTimeStamp(postedAt))
                        }
                        parseList(parser)
                    }
        }
        return pastMessages
    }

    private fun toTimeStamp(time: String): Timestamp {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
        return Timestamp(dateFormat.parse(time).time)
    }
}
