package com.sample.android_client

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*

const val DATABASE_NAME = "local_data.db"
const val DATABASE_VERSION = 1
const val USERS_TABLE_NAME = "users"
const val ROOMS_TABLE_NAME = "rooms"
const val MESSAGES_TABLE_NAME = "messages"

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
                "named_id" to TEXT + UNIQUE + NOT_NULL,
                "name" to TEXT + NOT_NULL,
                "icon_id" to INTEGER,
                "is_friend" to INTEGER + NOT_NULL)

        db.createTable(ROOMS_TABLE_NAME, true,
                "id" to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
                "server_id" to INTEGER + UNIQUE + NOT_NULL,
                "icon_id" to INTEGER + NOT_NULL,
                "name" to TEXT + NOT_NULL,
                "is_group" to INTEGER + NOT_NULL)

        db.createTable(MESSAGES_TABLE_NAME, true,
                "id" to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
                "server_id" to INTEGER + NOT_NULL + UNIQUE,
                "room_id" to INTEGER + NOT_NULL,
                "user_id" to INTEGER + NOT_NULL,
                "body" to TEXT + NOT_NULL,
                "posted_at" to TEXT + NOT_NULL)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    }
}