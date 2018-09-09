package com.sample.android_client

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*

const val DATABASE_NAME = "local_data.db"
const val DATABASE_VERSION = 1
const val USERS_TABLE_NAME = "users"
const val ROOMS_TABLE_NAME = "rooms"

class DBHelper(context: Context) : ManagedSQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private var instance: DBHelper? = null

        @Synchronized
        fun getInstance(context: Context): DBHelper {
            if (instance == null) {
                instance = DBHelper(context.applicationContext)
            }

            return instance!!
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.createTable(USERS_TABLE_NAME, true,
                "id" to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
                "server_id" to INTEGER + UNIQUE + NOT_NULL,
                "user_id" to INTEGER + UNIQUE + NOT_NULL,
                "name" to TEXT + NOT_NULL,
                "icon_id" to INTEGER,
                "is_friend" to INTEGER + NOT_NULL)

        db.createTable(ROOMS_TABLE_NAME, true,
                "id" to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
                "server_id" to INTEGER + UNIQUE + NOT_NULL,
                "icon_id" to INTEGER  + NOT_NULL,
                "name" to TEXT + NOT_NULL,
                "is_group" to INTEGER + NOT_NULL)

        // デバッグ用データを追加
        // TODO 今後削除する
        db.insert(ROOMS_TABLE_NAME,
                "server_id" to 0,
                "icon_id" to 1,
                "name" to "sample1",
                "is_group" to 1)
        db.insert(ROOMS_TABLE_NAME,
                "server_id" to 1,
                "icon_id" to 1,
                "name" to "sample2",
                "is_group" to 0)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    }
}