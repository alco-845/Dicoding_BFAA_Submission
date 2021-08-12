package com.alcorp.consumergithubuser.helper

import android.database.Cursor
import com.alcorp.consumergithubuser.database.DatabaseContract
import com.alcorp.consumergithubuser.model.Fav
import com.alcorp.consumergithubuser.database.DatabaseContract.FavColumns.Companion._ID

object MappingHelper {
    fun mapCursorToArrayList(favCursor: Cursor?): ArrayList<Fav>{
        val favList = ArrayList<Fav>()

        favCursor?.apply {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(_ID))
                val username = getString(getColumnIndexOrThrow(DatabaseContract.FavColumns.USERNAME))
                val avatar_url = getString(getColumnIndexOrThrow(DatabaseContract.FavColumns.AVATAR_URL))

                favList.add(Fav(id, username, avatar_url))
            }
        }
        return favList
    }

    fun mapCursorToObject(favCursor: Cursor?): Fav {
        var fav = Fav()
        favCursor?.apply {
            moveToFirst()
            val id = getInt(getColumnIndexOrThrow(_ID))
            val username = getString(getColumnIndexOrThrow(DatabaseContract.FavColumns.USERNAME))
            val avatar_url = getString(getColumnIndexOrThrow(DatabaseContract.FavColumns.AVATAR_URL))

            fav = Fav(id, username, avatar_url)
        }
        return fav
    }
}