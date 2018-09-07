package com.sample.android_client

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.activity_create_group.*

class CreateGroupActivity : AppCompatActivity() {
    val groupAdapter = GroupAdapter<ViewHolder>().apply {
        spanCount = 4
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_group)

        supportActionBar?.title = "新しいグループを作る"

        recycler_view_create_group.apply {
            layoutManager = GridLayoutManager(this@CreateGroupActivity, groupAdapter.spanCount).apply {
                spanSizeLookup = groupAdapter.spanSizeLookup
            }
            adapter = groupAdapter
            itemAnimator = null
        }

        search_button_create_group.setOnClickListener {
            val keyword = search_box_create_group.text.toString()
            Log.d("CreateGroupActivity", "文字列${keyword}が名前に含まれるユーザを検索")

            // TODO: ローカルDBからkeywordを名前に含むユーザを引っ張ってきてリストを作成する
        }

        fab_create_group.setOnClickListener {
            // TODO: 新しくグループを作ってサーバのDBに登録する処理
            // TODO: ローカルDBに登録する処理
        }

        delete_button_create_group.setOnClickListener {
            search_box_create_group.text.clear()
        }
    }
}
