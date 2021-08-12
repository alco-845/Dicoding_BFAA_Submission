package com.alcorp.githubuser.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import android.util.Log
import com.alcorp.githubuser.database.DatabaseContract.AUTHORITY
import com.alcorp.githubuser.database.DatabaseContract.FavColumns.Companion.CONTENT_URI
import com.alcorp.githubuser.database.DatabaseContract.FavColumns.Companion.TABLE_NAME
import com.alcorp.githubuser.database.FavHelper

class FavProvider : ContentProvider() {

    companion object {
        private const val FAV = 1

        private lateinit var favHelper: FavHelper

        private val sUriMatcher = UriMatcher(UriMatcher.NO_MATCH)

        init {
            sUriMatcher.addURI(AUTHORITY, TABLE_NAME, FAV)
        }
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val deleted: Int = favHelper.deleteByUsr(uri.lastPathSegment.toString())

        context?.contentResolver?.notifyChange(CONTENT_URI, null)

        Log.d("Delete", "Delete Uri : $uri")

        return deleted
    }



    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val added: Long = when(FAV){
            sUriMatcher.match(uri) -> favHelper.insert(values)
            else -> 0
        }

        context?.contentResolver?.notifyChange(CONTENT_URI, null)

        Log.d("Insert", "Insert Uri : $uri")

        return Uri.parse("$CONTENT_URI/$added")
    }

    override fun onCreate(): Boolean {
        favHelper = FavHelper.getInstance(context as Context)
        favHelper.open()
        return true
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        val cursor: Cursor?
        when(FAV){
            sUriMatcher.match(uri) -> cursor = favHelper.queryAll()
            else -> cursor = favHelper.queryByUsr(uri.lastPathSegment.toString())
        }

        Log.d("Show", "Show Data Uri : $uri")
        return cursor
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        return 0
    }
}
