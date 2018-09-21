package com.sample.android_client

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.widget.Toast
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_add_friends.*
import kotlinx.android.synthetic.main.item_friend_friends.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class AddFriendsActivity : AppCompatActivity() {
    private val groupAdapter = GroupAdapter<ViewHolder>().apply {
        spanCount = 4
    }
    private val selectedUsers = mutableListOf<SelectableUserItem>()  // 現在アプリ上で選択されているユーザ

    private val serverAPI = Retrofit.Builder()
            .baseUrl("http://ec2-13-114-207-18.ap-northeast-1.compute.amazonaws.com")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ServerAPI::class.java)

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

    private fun isAlreadyFriend(keyword: String): Boolean {
        // TODO: keywordをuserIdに持つユーザが存在し、かつその人と既に友達であればtrueを返す処理
        return false
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

        return SelectableUserItem(receiver.toUser())
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

    inner class SelectableUserItem(val userId: String,      // Userに登録させる一意なID
                                   val userName: String,    // Userが表示したい名前
                                   val userIconId: Int,
                                   var isSelected: Boolean = false) : Item() {
        constructor(user: User) : this(user.namedId, user.name, user.iconId, user.isFriend)

        override fun bind(viewHolder: com.xwray.groupie.kotlinandroidextensions.ViewHolder, position: Int) {
            viewHolder.user_name_textview_friends.text = userName
            viewHolder.itemView.alpha = if (isSelected) 1f else 0.6f
            // TODO : 友達のアイコンを表示する
        }

        override fun getLayout(): Int = R.layout.item_friend_friends

        override fun getSpanSize(spanCount: Int, position: Int): Int = spanCount / 4

        // デバッグ用
        override fun toString(): String {
            return userName
        }
    }
}
