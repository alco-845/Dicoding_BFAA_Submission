package com.alcorp.consumergithubuser.viewmodel

import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alcorp.consumergithubuser.database.DatabaseContract.FavColumns.Companion.CONTENT_URI
import com.alcorp.consumergithubuser.helper.MappingHelper
import com.alcorp.consumergithubuser.model.Fav
import kotlin.collections.ArrayList

class UserViewModel : ViewModel() {

    val listFav = MutableLiveData<ArrayList<Fav>>()

    fun setFav(contentResolver: ContentResolver) {
        try {
            val cursor = contentResolver.query(CONTENT_URI, null, null, null, null)
            val value = MappingHelper.mapCursorToArrayList(cursor)
            listFav.postValue(value)
        } catch (e: Exception){
            Log.d("Exception", e.message.toString())
        }
    }

    fun searchFav(query: String, contentResolver: ContentResolver){
        try {
            val uri = Uri.parse(CONTENT_URI.toString() + "/" + query)
            val cursor = contentResolver.query(uri, null, null, null, null)
            val value = MappingHelper.mapCursorToArrayList(cursor)
            listFav.postValue(value)
        } catch (e: Exception){
            Log.d("Exception", e.message.toString())
        }
    }

    fun getFav(): LiveData<ArrayList<Fav>>{
        return listFav
    }
}