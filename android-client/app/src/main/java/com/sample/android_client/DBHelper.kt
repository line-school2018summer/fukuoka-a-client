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
        this.use {
            this.transaction {
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
}

class RoomsTableHelper(content: Context) : ManagedSQLiteOpenHelper(content, "localDate.db", null, 1) {
    companion object {
        private var instance: RoomsTableHelper? = null
        @Synchronized
        fun getInstance(context: Context): RoomsTableHelper {
            if (instance == null) {
                instance = RoomsTableHelper(context.applicationContext)
            }
            return instance!!
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.createTable("rooms", true,
                "id" to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
                "server_id" to INTEGER + UNIQUE + NOT_NULL,
                "icon_id" to INTEGER + UNIQUE + NOT_NULL,
                "name" to TEXT + NOT_NULL,
                "is_group" to INTEGER + NOT_NULL)
    }

    override fun onUpgrade(db: SQLiteDatabase?, newVersion: Int, oldVersion: Int) {

    }

    fun insertRoom(newRoom: Room) {
        this.use {
            this.insert("rooms",
                    "server_id" to newRoom.serverId,
                    "icon_id" to newRoom.iconId,
                    "name" to newRoom.name,
                    "is_group" to if (newRoom.isGroup) 1 else 0)
        }
    }
}

data class Room(val serverId: Int, val iconId: Int, val name: String, val isGroup: Boolean)