package com.sample.android_client

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*

class UsersDBHelper(context: Context) : ManagedSQLiteOpenHelper(context, "localData.db", null, 1) {
    companion object {
        private var instance: UsersDBHelper? = null
        @Synchronized
        fun getInstance(context: Context): UsersDBHelper {
            if (instance == null) {
                instance = UsersDBHelper(context.applicationContext)
            }
            return instance!!
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.createTable("users", true,
                "id" to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
                "server_id" to INTEGER + UNIQUE + NOT_NULL,
                "user_id" to INTEGER + UNIQUE + NOT_NULL,
                "name" to TEXT + NOT_NULL,
                "icon_id" to INTEGER + UNIQUE,
                "is_friend" to INTEGER + NOT_NULL)
    }

    override fun onUpgrade(db: SQLiteDatabase?, newVersion: Int, oldVersion: Int) {

    }

    fun saveUserData(userData: List<User>) {
        this.writableDatabase.transaction {
            userData.forEach {
                this.insert("users",
                        "id" to it.id,
                        "server_id" to it.serverId,
                        "user_id" to it.userId,
                        "name" to it.name,
                        "icon_id" to it.iconId,
                        "is_friend" to if (it.isFriend) 1 else 0)
            }
        }
    }

}