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
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class TalkActivity : RxActivity() {
    private val Context.database: DBHelper
        get() = DBHelper.getInstance(applicationContext)

    private val serverAPI = Retrofit.Builder()
            .baseUrl("http://ec2-13-114-207-18.ap-northeast-1.compute.amazonaws.com")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ServerAPI::class.java)

    // TODO Activity起動時に代入するように変更する
    private var roomId = 1
    private val newMessages = mutableListOf<Message>()
    private val talkAdapter = MessageRecyclerViewAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_talk)

        talk_recycler_view.adapter = talkAdapter
        talk_recycler_view.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        send_button_talk.setOnClickListener {
            val message = input_message_box_talk.text.toString()
            Log.d("TalkActivity", "文字列${message}を送信する")
            // TODO: message送信処理
            // 入力ボックスを空にする

            // TODO デバッグ用、今後削除
            val dummyMessage = createDummyMessage()
            Log.d("TalkActivity", "userId = ${dummyMessage.userId} message=${dummyMessage.postedAt}")
            newMessages.add(dummyMessage)

            input_message_box_talk.text.clear()
        }
    }

    override fun onResume() {
        super.onResume()

        newMessages.clear()

        val pastMessages = loadPastMessages()
        talkAdapter.setMessages(pastMessages)
        talk_recycler_view.scrollToPosition(talkAdapter.itemCount - 1)

        fetchNewMessages()
                .observeOn(AndroidSchedulers.mainThread())
                .retryWhen {
                    it.flatMap {
                        Toast.makeText(applicationContext, "Can't fetch new message...", Toast.LENGTH_SHORT).show()
                        Observable.timer(3, TimeUnit.SECONDS)
                    }

                }
                .subscribeBy(
                        onNext = { fetchedMessages ->
                            newMessages.addAll(fetchedMessages)
                            talkAdapter.insertNewMessages(fetchedMessages)

                            talk_recycler_view.scrollToPosition(talkAdapter.itemCount - 1)
                        }
                )
    }

    override fun onPause() {
        super.onPause()

        insertNewMessages()?.subscribe()
    }

    private fun insertNewMessages(): Completable? {
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
        }.toCompletable().subscribeOn(Schedulers.io()).observeOn(Schedulers.io())

    }

    private fun fetchNewMessages(): Observable<Sequence<Message>> {
        return Observable.interval(1, TimeUnit.SECONDS)
                .bindUntilEvent(this, ActivityEvent.PAUSE)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
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

    //TODO デバッグ用、今後削除
    private fun createDummyMessage(): Message {
        val timeStamp = Timestamp(Date().time)
        val userId = if (Random().nextBoolean()) 1 else 2

        return Message(1, roomId, userId, timeStamp.toString(), timeStamp)
    }
}
