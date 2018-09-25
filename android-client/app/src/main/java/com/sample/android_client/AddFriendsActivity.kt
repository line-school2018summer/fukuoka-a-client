package com.sample.android_client

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.widget.Toast
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_add_friends.*
import kotlinx.android.synthetic.main.item_friend_friends.*
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.parseList
import org.jetbrains.anko.db.rowParser
import org.jetbrains.anko.db.select
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class AddFriendsActivity : AppCompatActivity() {
    private val groupAdapter = GroupAdapter<ViewHolder>().apply {
        spanCount = 4
    }
    private val selectedUsers = mutableListOf<SelectableUserItem>()  // 現在アプリ上で選択されているユーザ
    private val localUsers by lazy { loadLocalUsers() }
    private val database by lazy { DBHelper.getInstance(this).writableDatabase }

    private val myId = 1

    private val serverAPI by lazy {
        Retrofit.Builder()
                .baseUrl("http://ec2-13-114-207-18.ap-northeast-1.compute.amazonaws.com")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ServerAPI::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_friends)

        recycler_view_add_friends.apply {
            layoutManager = GridLayoutManager(this@AddFriendsActivity, groupAdapter.spanCount).apply {
                spanSizeLookup = groupAdapter.spanSizeLookup
            }
            adapter = groupAdapter
            itemAnimator = null
        }

        supportActionBar?.title = "友達を追加"

        search_button_add_friends.setOnClickListener {
            val keyword = search_box_add_friends.text.toString()

            if (keyword.isBlank()) {
                return@setOnClickListener
            }

            selectedUsers.clear()

            Log.d("AddFriendsActivity", "文字列${keyword}が含まれるユーザを検索")

            if (isAlreadyFriend(keyword)) {
                Toast.makeText(this, "検索したユーザは既に友達です", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            displaySearchedUser(keyword)
        }

        add_friends_fab.setOnClickListener {
            if (selectedUsers.isEmpty()) {
                Toast.makeText(this, "友達が選択されていません", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // TODO: 選んだユーザを友達に追加する処理
            val numberSelected = selectedUsers.size
            Toast.makeText(this, "${numberSelected}人を友達に追加しました！", Toast.LENGTH_LONG).show()
            addFriend()

            finish()
        }

        delete_button_add_friends.setOnClickListener {
            search_box_add_friends.text.clear()
        }

        groupAdapter.setOnItemClickListener { item, _ ->
            val sItem = item as SelectableUserItem
            if (sItem.isSelected) {
                selectedUsers.remove(sItem)
                sItem.isSelected = false
            } else {
                selectedUsers.add(sItem)
                sItem.isSelected = true
            }
            sItem.notifyChanged()
            updateGuideTextView()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        database.close()
    }

    private fun isAlreadyFriend(keyword: String): Boolean {
        return localUsers.any { it.namedId == keyword && it.isFriend }
    }

    private fun updateGuideTextView() {
        val numberSelected = selectedUsers.size
        if (numberSelected > 0) {
            guide_textview_add_friends.text = "友達になりたい人を選んでください(${numberSelected}人選択中)"
        } else {
            guide_textview_add_friends.text = "友達になりたい人を選んでください"
        }
    }

    // 友達を検索するときは、ユーザが決める一意なIDで検索させる
    // 悪用されないように完全一致にする
    // 一致するユーザが見つからないときはnullを返す
    private fun fetchSearchedUsers(keyword: String): SelectableUserItem? {

        var receiver = serverAPI.fetchUser(keyword)
                .subscribeOn(Schedulers.io())
                .blockingGet()

        return if (receiver.isEmpty()) {
            null
        } else
            SelectableUserItem(receiver.first().toUser())
    }

    private fun displaySearchedUser(keyword: String) {
        val item = fetchSearchedUsers(keyword)

        if (item == null) {
            Toast.makeText(this, "一致するユーザが見つかりません", Toast.LENGTH_LONG).show()
            return
        }

        groupAdapter.clear()
        groupAdapter.add(item)
        updateGuideTextView()
    }

    private fun loadLocalUsers(): List<User> {
        return database.select(USERS_TABLE_NAME,
                "server_id",
                "named_id",
                "name",
                "icon_id",
                "is_friend")
                .exec {
                    val parser = rowParser { serverId: Int, namedId: String, name: String, iconId: Int, isFriend: Int ->
                        User(serverId, name, namedId, iconId, isFriend == 1)
                    }
                    parseList(parser)
                }
    }

    private fun addFriend() {
        Observable.fromIterable(selectedUsers)
                .subscribeOn(Schedulers.io())
                .flatMap { serverAPI.fetchUser(it.userNamedId).toObservable() }
                .subscribe {
                    val user = it.first().toUser()
                    createRoom(user)
                    println(user)
                    database.insert(USERS_TABLE_NAME,
                            "server_id" to user.serverId,
                            "named_id" to user.namedId,
                            "name" to user.name,
                            "icon_id" to user.iconId,
                            "is_friend" to 1)
                }
    }

    private fun createRoom(user: User) {
        var existRoom = false

        fun insertRoomToServer() {
            serverAPI.postRoom(" ", false)
                    .doOnNext {
                        serverAPI.postRoomInfo(user.serverId, it.id)
                                .subscribeOn(Schedulers.io())
                                .subscribe()
                        serverAPI.postRoomInfo(myId, it.id)
                                .subscribeOn(Schedulers.io())
                                .subscribe()
                    }
                    .subscribeOn(Schedulers.io())
                    .subscribe()
        }

        serverAPI.fetchRoomIdInclude(user.serverId)
                .subscribeOn(Schedulers.io())
                .flatMap { Observable.fromIterable(it) }
                .flatMap { serverAPI.fetchUserIdBelong(it) }
                .flatMap { Observable.fromIterable(it) }
                .filter { it.id == myId }
                .subscribeBy(
                        onNext = {
                            existRoom = true
                        },
                        onComplete = {
                            if (!existRoom) {
                                insertRoomToServer()
                            }
                        }
                )
    }

    inner class SelectableUserItem(val userNamedId: String,
                                   val userName: String,    // Userが表示したい名前
                                   val userIconId: Int,
                                   var isSelected: Boolean = false) : Item() {
        constructor(user: User) : this(user.namedId, user.name, user.iconId)

        override fun bind(viewHolder: com.xwray.groupie.kotlinandroidextensions.ViewHolder, position: Int) {
            viewHolder.user_name_textview_friends.text = userName
            viewHolder.itemView.alpha = if (isSelected) 1f else 0.6f
            // TODO : 友達のアイコンを表示する

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

        // デバッグ用
        override fun toString(): String {
            return userName
        }
    }
}