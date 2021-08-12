package com.alcorp.githubuser.viewModel

import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alcorp.githubuser.database.DatabaseContract.FavColumns.Companion.CONTENT_URI
import com.alcorp.githubuser.helper.MappingHelper
import com.alcorp.githubuser.model.Fav
import com.alcorp.githubuser.model.User
import com.alcorp.githubuser.model.UserDetail
import com.alcorp.githubuser.view.ProfileActivity
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.TextHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONArray
import org.json.JSONObject
import kotlin.collections.ArrayList

class UserViewModel : ViewModel() {

    val list = arrayListOf<User>()
    val listDetail = arrayListOf<UserDetail>()
    val listFav = MutableLiveData<ArrayList<Fav>>()
    val listAllUser = MutableLiveData<ArrayList<User>>()
    val listAllUserDetail = MutableLiveData<ArrayList<UserDetail>>()

    private var profileActivity: ProfileActivity = ProfileActivity()

    fun setUser(){
        val client = AsyncHttpClient()
        val url = "https://api.github.com/users"
        client.addHeader("Authorization", "token 9f97f8786da1b80a55a457c22b3688244b124025")
        client.addHeader("User-Agent", "request")
        client.get(url, object: TextHttpResponseHandler(){
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: String?) {
                try {
                    val jsonArray = JSONArray(responseBody)
                    for (position in 0 until jsonArray.length()){
                        val jsonObject = jsonArray.getJSONObject(position)
                        val user = User(
                            jsonObject.getString("login"),
                            jsonObject.getString("avatar_url")
                        )
                        list.add(user)
                    }
                    listAllUser.postValue(list)
                } catch (e: Exception){
                    Log.d("Exception", e.message.toString())
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: String?, error: Throwable?) {
                Log.d("onFailure", error?.message.toString())
            }

        })
    }

    fun searchUser(query: String){
        val client = AsyncHttpClient()
        val url = "https://api.github.com/search/users?q=$query"
        client.addHeader("Authorization", "token 9f97f8786da1b80a55a457c22b3688244b124025")
        client.addHeader("User-Agent", "request")
        client.get(url, object: TextHttpResponseHandler(){
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseString: String) {
                try {
                    list.clear()

                    val responObject = JSONObject(responseString)
                    val listItems = responObject.getJSONArray("items")
                    for (position in 0 until listItems.length()){
                        val jsonObject = listItems.getJSONObject(position)
                        val user = User(
                            jsonObject.getString("login"),
                            jsonObject.getString("avatar_url")
                        )
                        list.add(user)
                    }
                    listAllUser.postValue(list)
                } catch (e: Exception){
                    Log.d("Exception", e.message.toString())
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseString: String?, throwable: Throwable?) {
                Log.d("onFailure", throwable?.message.toString())
            }
        })
    }

    fun setUserDetail(user: String?, place: String){
        val client = AsyncHttpClient()
        val url = "https://api.github.com/users/$user"
        client.addHeader("Authorization", "token 9f97f8786da1b80a55a457c22b3688244b124025")
        client.addHeader("User-Agent", "request")
        client.get(url, object: TextHttpResponseHandler(){
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseString: String) {
                try {
                    val jsonObject = JSONObject(responseString)
                    for (position in 0 until jsonObject.length()){

                        profileActivity.name = jsonObject.getString("name")
                        if (jsonObject.getString("location") == "null" && jsonObject.getString("location") == "null") {
                            profileActivity.location = place
                            profileActivity.company = place
                        } else if (jsonObject.getString("location") == "null"){
                            profileActivity.location = place
                            profileActivity.company = jsonObject.getString("company")
                        } else if (jsonObject.getString("company") == "null"){
                            profileActivity.location = jsonObject.getString("location")
                            profileActivity.company = place
                        } else {
                            profileActivity.location = jsonObject.getString("location")
                            profileActivity.company = jsonObject.getString("company")
                        }

                        val userDetail = UserDetail(
                            jsonObject.getString("login"),
                            profileActivity.name,
                            profileActivity.location,
                            profileActivity.company,
                            jsonObject.getString("public_repos"),
                            jsonObject.getString("followers"),
                            jsonObject.getString("following"),
                            jsonObject.getString("avatar_url")
                        )

                        listDetail.add(userDetail)
                    }
                    listAllUserDetail.postValue(listDetail)
                } catch (e: Exception){
                    Log.d("Exception", e.message.toString())
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseString: String?, throwable: Throwable?) {
                Log.d("onFailure", throwable?.message.toString())
            }

        })
    }

    fun setFollowers(value: String){
        val client = AsyncHttpClient()
        val url = "https://api.github.com/users/$value/followers"

        client.addHeader("Authorization", "token 9f97f8786da1b80a55a457c22b3688244b124025")
        client.addHeader("User-Agent", "request")
        client.get(url, object: TextHttpResponseHandler(){
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseString: String?) {
                try {
                    val jsonArray = JSONArray(responseString)
                    for (position in 0 until jsonArray.length()){
                        val jsonObject = jsonArray.getJSONObject(position)
                        val user = User(
                            jsonObject.getString("login"),
                            jsonObject.getString("avatar_url")
                        )
                        list.add(user)
                    }
                    listAllUser.postValue(list)
                } catch (e: Exception){
                    Log.d("Exception", e.message.toString())
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseString: String?, throwable: Throwable?) {
                Log.d("onFailure", throwable?.message.toString())
            }

        })
    }

    fun setFollowing(value: String){
        val client = AsyncHttpClient()
        val url = "https://api.github.com/users/$value/following"

        client.addHeader("Authorization", "token 9f97f8786da1b80a55a457c22b3688244b124025")
        client.addHeader("User-Agent", "request")
        client.get(url, object: TextHttpResponseHandler(){
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseString: String?) {
                try {
                    val jsonArray = JSONArray(responseString)
                    for (position in 0 until jsonArray.length()){
                        val jsonObject = jsonArray.getJSONObject(position)
                        val user = User(
                            jsonObject.getString("login"),
                            jsonObject.getString("avatar_url")
                        )
                        list.add(user)
                    }
                    listAllUser.postValue(list)
                } catch (e: Exception){
                    Log.d("Exception", e.message.toString())
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseString: String?, throwable: Throwable?) {
                Log.d("onFailure", throwable?.message.toString())
            }

        })
    }

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

    fun getUser(): LiveData<ArrayList<User>> {
        return listAllUser
    }

    fun getFav(): LiveData<ArrayList<Fav>>{
        return listFav
    }

    fun getUserDetail(): LiveData<ArrayList<UserDetail>> {
        return listAllUserDetail
    }
}