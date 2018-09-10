package com.sample.android_client

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_friends -> {
                supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout_main, FriendsFragment())
                        .commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_latest_messages -> {
                supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout_main, LatestMessagesFragment())
                        .commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_settings -> {
                supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout_main, SettingsFragment())
                        .commit()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        supportFragmentManager.beginTransaction()
                .replace(R.id.frame_layout_main, FriendsFragment())
                .commit()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_friends, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        // 「友だちを追加」をタップしたときの動作
        val intent = Intent(this, AddFriendsActivity::class.java)
        startActivity(intent)
        return super.onOptionsItemSelected(item)
    }
}
