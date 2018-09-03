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
        db.createTable("Users", true,
                "ID" to INTEGER + PRIMARY_KEY + UNIQUE,
                "Name" to TEXT,
                "Email" to TEXT,
                "IconID" to INTEGER,
                "IconURL" to TEXT,
                "isFriend" to INTEGER)
    }

    override fun onUpgrade(db: SQLiteDatabase?, newVersion: Int, oldVersion: Int) {

    }
    
    fun saveUserData(userData: List<User>) {
        this.writableDatabase.transaction {
            userData.forEach {
                this.insert("Users",
                        "ID" to it.ID,
                        "Name" to it.name,
                        "Email" to it.email,
                        "IconID" to it.iconID,
                        "IconURL" to it.iconURL,
                        "isFriend" to if (it.isFriend) 1 else 0)
            }
        }
    }

}