package com.sample.android_client

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.activity_create_group.*
import kotlinx.android.synthetic.main.item_friend_friends.*
import kotlinx.android.synthetic.main.item_user_scroll.*

class CreateGroupActivity : AppCompatActivity() {
    var selectedPhotoUri: Uri? = null
    val groupAdapter = GroupAdapter<ViewHolder>().apply {
        spanCount = 4
    }
    val hGroupAdapter = GroupAdapter<ViewHolder>()
    lateinit var dummyUserItems: MutableList<SelectableUserItem>
    val selectedUsers = mutableListOf<SelectableUserItem>()     // 選択されたユーザのリスト

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_group)

        supportActionBar?.title = "新しいグループを作る"

        // ダミーデータの作成
        generateDummyUsersItems()

        // 最初は全ての友達を表示する
        displaySearchedUser()

        // 検索したユーザを表示するためのrecyclerviewの設定
        recycler_view_create_group.apply {
            layoutManager = GridLayoutManager(this@CreateGroupActivity, groupAdapter.spanCount).apply {
                spanSizeLookup = groupAdapter.spanSizeLookup
            }
            adapter = groupAdapter
            itemAnimator = null
        }

        // 選択したユーザを画面下部に横スクロールで表示するためのrecyclerviewの設定
        horizontal_recycler_view_create_group.apply {
            layoutManager = LinearLayoutManager(this@CreateGroupActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = hGroupAdapter
        }

        search_box_create_group.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                displaySearchedUser(p0.toString())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        fab_create_group.setOnClickListener {
            val groupName = group_name_edittext_create_group.text.toString()

            if (groupName.isEmpty()) {
                Toast.makeText(this, "グループ名を入力してください", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (selectedUsers.isEmpty()) {
                Toast.makeText(this, "選択されたユーザが存在しません", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (selectedPhotoUri == null) {
                Log.d("CreateGroupActivity", "アイコンが選択されていない")
                // TODO: アイコンが選ばれていないときにデフォルトアイコンを設定する？
            }

            // TODO: 選択されたユーザのリストで新しくグループを作ってサーバのDBに登録する処理
            // TODO: ローカルDBに登録する処理

            Toast.makeText(this, "グループを作成しました！", Toast.LENGTH_LONG).show()
            finish()
        }

        delete_button_create_group.setOnClickListener {
            search_box_create_group.text.clear()
        }

        groupAdapter.setOnItemClickListener { item, view ->
            val sItem = item as SelectableUserItem
            if (sItem.isSelected) {
                selectedUsers.remove(sItem)
                sItem.isSelected = false
            } else {
                selectedUsers.add(sItem)
                sItem.isSelected = true
            }
            sItem.notifyChanged()
            updateGuideTextview()
            updateScrollView()
        }

        select_photo_button_create_group.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // 画像選択に成功した場合
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            // 選択した画像を円形に表示する(最初に表示されていた丸は邪魔になるので透明にしている)
            select_photo_button_create_group.alpha = 0f
            circular_imageview_create_group.setImageBitmap(bitmap)
        }
    }

    private fun updateScrollView() {
        hGroupAdapter.clear()
        for (user in selectedUsers) {
            hGroupAdapter.add(ScrollUserItem(user.userId, user.userName, user.userIconId))
        }
    }

    private fun updateGuideTextview() {
        val numberSelected = selectedUsers.size
        if (numberSelected > 0) {
            select_user_guide_create_group.text = "選択中のユーザ(${numberSelected})"
        } else {
            select_user_guide_create_group.text = "選択中のユーザ"
        }
    }

    // グループ追加で検索するときは、自分の友達一覧から検索する
    // ユーザが決める一意なIDではなく、表示している名前の部分一致で検索する
    private fun fetchSearchedUsers(keyword: String): List<SelectableUserItem> {
        // TODO: ダミーデータじゃなくて、ローカルDBを検索して同様のことをする
        val searchedUsers = dummyUserItems.filter { item -> item.userName.indexOf(keyword) >= 0 }
        return searchedUsers
    }

    private fun displaySearchedUser(keyword: String = "") {
        val items = fetchSearchedUsers(keyword)
        groupAdapter.clear()
        groupAdapter.addAll(items)
        updateGuideTextview()
    }

    private fun generateDummyUsersItems() {
        dummyUserItems = mutableListOf<SelectableUserItem>(
                SelectableUserItem("a", "saito yuya", 0),
                SelectableUserItem("b", "suzuki yuto", 0),
                SelectableUserItem("c", "suzuki takuma", 0),
                SelectableUserItem("d", "honda keisuke", 0),
                SelectableUserItem("e", "kawasaki tomoya", 0),
                SelectableUserItem("f", "yamaha tarou", 0)
        )
        for (i in 1..20)
            dummyUserItems.add(SelectableUserItem("b", "suzuki yuto", 0))
    }

    inner class SelectableUserItem(val userId: String,      // Userに登録させる一意なID
                                   val userName: String,    // Userが表示したい名前
                                   val userIconId: Int,
                                   var isSelected: Boolean = false) : Item() {
        override fun bind(viewHolder: com.xwray.groupie.kotlinandroidextensions.ViewHolder, position: Int) {
            viewHolder.user_name_textview_friends.text = userName
            viewHolder.itemView.alpha = if (isSelected) 1f else 0.6f

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

    inner class ScrollUserItem(val userId: String,
                               val userName: String,
                               val userIconId: Int) : Item() {
        override fun bind(viewHolder: ViewHolder, position: Int) {
            val limitLength = 10
            val displayName = if (userName.length <= limitLength) userName else userName.substring(0 until limitLength - 1) + "..."
            viewHolder.user_name_textview_scroll.text = displayName

            // TODO: 友達またはグループのアイコンのURLを取得する

            val defaultIconURL = "https://firebasestorage.googleapis.com/v0/b/fukuoka-a-client.appspot.com/o/image%2Fdman.png?alt=media&token=ca1deb08-e413-4b37-b925-e07b39232e35"

            Picasso.get()
                    .load(defaultIconURL)
                    .fit()
                    .centerCrop()
                    .into(viewHolder.user_icon_circular_imageview_scroll)
        }

        override fun getLayout(): Int = R.layout.item_user_scroll
    }
}
