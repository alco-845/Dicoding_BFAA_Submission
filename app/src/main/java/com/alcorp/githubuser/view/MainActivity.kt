package com.alcorp.githubuser.view

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.alcorp.githubuser.R
import com.alcorp.githubuser.adapter.UserAdapter
import com.alcorp.githubuser.helper.AlarmReceiver
import com.alcorp.githubuser.viewModel.UserViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    companion object {
        fun checkNetwork(context: Context?): Boolean{
            val cm: ConnectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network =
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    cm.getNetworkCapabilities(cm.activeNetwork)
                } else {
                    cm.activeNetworkInfo
                }
            return network != null
        }
    }

    private lateinit var userViewModel: UserViewModel
    private lateinit var userAdapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        refresh_main.setOnRefreshListener(this)

        userAdapter = UserAdapter()
        userAdapter.notifyDataSetChanged()

        rec_user.setHasFixedSize(true)
        rec_user.layoutManager = LinearLayoutManager(this)
        rec_user.adapter = userAdapter

        userViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(UserViewModel::class.java)

        if (checkNetwork(this)){
            userViewModel.setUser()
        } else {
            progBar_main.visibility = View.GONE
            Toast.makeText(this@MainActivity, resources.getString(R.string.txt_toast_network), Toast.LENGTH_SHORT).show()
        }

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val alarmReceiver = AlarmReceiver()
        if(sharedPreferences.getBoolean(resources.getString(R.string.key_reminder), true)){
            alarmReceiver.setNotification(this, "09:00")
        } else {
            alarmReceiver.cancelNotification(this)
        }

        userViewModel.getUser().observe(this, Observer { user ->
            if (user != null){
                userAdapter.setData(user)
                progBar_main.visibility = View.GONE
            }
        })
    }

    override fun onRefresh() {
        progBar_main.visibility = View.VISIBLE
        if (checkNetwork(this)){
            progBar_main.visibility = View.GONE
            userViewModel.list.clear()
            userViewModel.setUser()
        } else {
            progBar_main.visibility = View.GONE
            userViewModel.list.clear()
            Toast.makeText(this@MainActivity, resources.getString(R.string.txt_toast_network), Toast.LENGTH_SHORT).show()
        }
        refresh_main.isRefreshing = false
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.menu_toolbar, menu)
        menu.findItem(R.id.menu_share).isVisible = false

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.menu_search).actionView as SearchView

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = resources.getString(R.string.hint_search)
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String): Boolean {
                progBar_main.visibility = View.VISIBLE
                if (checkNetwork(this@MainActivity)){
                    progBar_main.visibility = View.GONE
                    userViewModel.searchUser(query)
                } else {
                    progBar_main.visibility = View.GONE
                    Toast.makeText(this@MainActivity, resources.getString(R.string.txt_toast_search), Toast.LENGTH_SHORT).show()
                }

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menu_setting){
            startActivity(Intent(this, SettingActivity::class.java))
        } else if (item.itemId == R.id.menu_fav){
            startActivity(Intent(this, FavActivity::class.java))
        }

        return super.onOptionsItemSelected(item)
    }

}
