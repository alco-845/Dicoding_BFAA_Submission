package com.alcorp.githubuser.view

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.alcorp.githubuser.R
import com.alcorp.githubuser.adapter.FavAdapter
import com.alcorp.githubuser.viewModel.UserViewModel
import kotlinx.android.synthetic.main.activity_fav.*

class FavActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var favAdapter: FavAdapter
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fav)

        setToolbar()

        refresh_fav.setOnRefreshListener(this)

        favAdapter = FavAdapter()

        rec_fav.setHasFixedSize(true)
        rec_fav.layoutManager = LinearLayoutManager(this)
        rec_fav.adapter = favAdapter

        userViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(UserViewModel::class.java)
        userViewModel.setFav(contentResolver)

        userViewModel.getFav().observe(this, Observer { fav ->
            if (fav != null){
                favAdapter.setData(fav)
                progBar_fav.visibility = View.GONE
            }
        })
    }

    override fun onRefresh() {
        userViewModel.setFav(contentResolver)

        refresh_fav.isRefreshing = false
    }

    private fun setToolbar() {
        supportActionBar?.title = resources.getString(R.string.txt_fav)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.menu_toolbar, menu)
        menu.findItem(R.id.menu_share).isVisible = false
        menu.findItem(R.id.menu_fav).isVisible = false

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.menu_search).actionView as SearchView

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = resources.getString(R.string.hint_search)
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String): Boolean {
                progBar_fav.visibility = View.VISIBLE

                progBar_fav.visibility = View.GONE
                userViewModel.searchFav(query, contentResolver)

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })

        return true
    }

    override fun onResume() {
        super.onResume()
        userViewModel.setFav(contentResolver)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        } else if(item.itemId == R.id.menu_setting){
            startActivity(Intent(this, SettingActivity::class.java))
        }

        return super.onOptionsItemSelected(item)
    }
}