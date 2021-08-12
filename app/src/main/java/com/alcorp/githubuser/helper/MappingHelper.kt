package com.alcorp.githubuser.helper

import android.database.Cursor
import com.alcorp.githubuser.database.DatabaseContract
import com.alcorp.githubuser.database.DatabaseContract.FavColumns.Companion._ID
import com.alcorp.githubuser.model.Fav

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
}