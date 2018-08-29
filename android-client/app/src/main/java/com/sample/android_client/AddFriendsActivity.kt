package com.sample.android_client

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_add_friends.*

class AddFriendsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_friends)

        supportActionBar?.title = "友だちを検索"

        search_button_add_friends.setOnClickListener {
            val pattern = search_box_add_friends.text.toString()
            Log.d("AddFriendsActivity", "文字列${pattern}が含まれるユーザを検索")
        }
    }
}
