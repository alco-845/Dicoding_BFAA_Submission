package com.alcorp.githubuser.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.alcorp.githubuser.database.DatabaseContract.FavColumns.Companion.TABLE_NAME
import com.alcorp.githubuser.database.DatabaseContract.FavColumns.Companion.USERNAME
import com.alcorp.githubuser.database.DatabaseContract.FavColumns.Companion._ID
import java.sql.SQLException

class FavHelper(context: Context) {
    private var databaseHelper: DatabaseHelper = DatabaseHelper(context)
    private lateinit var database: SQLiteDatabase

    companion object {
        private var INSTANCE: FavHelper? = null

        fun getInstance(context: Context): FavHelper =
            INSTANCE ?: synchronized(this){
                INSTANCE ?: FavHelper(context)
            }
    }

    @Throws(SQLException::class)
    fun open(){
        database = databaseHelper.writableDatabase
    }

    fun close(){
        databaseHelper.close()

        if (database.isOpen)
            database.close()
    }

    fun queryAll(): Cursor {
        return database.query(
            TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            "$_ID ASC"
        )
    }

    fun queryByUsr(username: String?): Cursor{
        return database.query(
            TABLE_NAME,
            null,
            "$USERNAME LIKE '%$username%'",
            null,
            null,
            null,
            null,
            null
        )
    }

    fun insert(values: ContentValues?): Long{
        return database.insert(TABLE_NAME, null, values)
    }

    fun deleteByUsr(name: String?): Int{
        return database.delete(TABLE_NAME, "$USERNAME = '$name'", null)
    }
}