package com.alcorp.githubuser.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.alcorp.githubuser.database.DatabaseContract.FavColumns
import com.alcorp.githubuser.database.DatabaseContract.FavColumns.Companion.TABLE_NAME

internal class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "db_github"
        private const val DATABASE_VERSION = 1

        private val SQL_CREATE_TABLE_FAV = "CREATE TABLE $TABLE_NAME " +
                "(${FavColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "${FavColumns.USERNAME} TEXT NOT NULL, " +
                "${FavColumns.AVATAR_URL} TEXT NOT NULL);"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_TABLE_FAV)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

}