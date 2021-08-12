package com.alcorp.githubuser.view

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.alcorp.githubuser.R
import com.alcorp.githubuser.adapter.PagerAdapter
import com.alcorp.githubuser.database.DatabaseContract
import com.alcorp.githubuser.database.DatabaseContract.FavColumns.Companion.CONTENT_URI
import com.alcorp.githubuser.model.User
import com.alcorp.githubuser.viewModel.UserViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener,
    View.OnClickListener {

    companion object {
        const val EXTRA_USER = "user"
    }

    private lateinit var userViewModel: UserViewModel

    var name: String = "name"
    var company: String = "company"
    var location: String = "location"
    private lateinit var user: User
    private lateinit var prefVal: String
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        setToolbar()

        user = intent.getParcelableExtra(EXTRA_USER) as User

        sharedPreferences = this.getSharedPreferences("fav", Context.MODE_PRIVATE)
        prefVal = sharedPreferences.getString(user.login + "fav", "unfav").toString()
        if (prefVal.equals("unfav")){
            Glide.with(this@ProfileActivity)
                .load(resources.getDrawable(R.drawable.ic_baseline_favorite_border_24))
                .into(btn_fav)
        } else {
            Glide.with(this@ProfileActivity)
                .load(resources.getDrawable(R.drawable.ic_baseline_favorite_24))
                .into(btn_fav)
        }

        val sectionsPagerAdapter = PagerAdapter(this, supportFragmentManager, user.login)
        view_pager.adapter = sectionsPagerAdapter
        tabLayout.setupWithViewPager(view_pager)

        userViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(UserViewModel::class.java)

        if (MainActivity.checkNetwork(this)) {
            userViewModel.setUserDetail(user.login, resources.getString(R.string.value_place))
        } else {
            progBar_profile.visibility = View.GONE
            Toast.makeText(this@ProfileActivity, resources.getString(R.string.txt_toast_network), Toast.LENGTH_SHORT).show()
        }

        userViewModel.getUserDetail().observe(this, Observer { usr ->
            if (usr != null) {
                userViewModel.setUserDetail(user.login, resources.getString(R.string.value_place))

                name = usr[0].name.toString()
                company = usr[0].company.toString()
                location = usr[0].location.toString()

                tvUsername.text = usr[0].login
                tvLocation.text = location
                tvName.text = name
                tvCompany.text = company
                tvRepository.text = usr[0].public_repos
                tvFollowers.text = usr[0].followers
                tvFollowing.text = usr[0].following

                Glide.with(this@ProfileActivity)
                    .load(usr[0].avatar_url)
                    .apply(RequestOptions().override(100, 100))
                    .into(profile_img)

                progBar_profile.visibility = View.GONE
            }
        })

        refresh_profile.setOnRefreshListener(this)
        btn_fav.setOnClickListener(this)
    }

    private fun setToolbar() {
        supportActionBar?.title = resources.getString(R.string.txt_profile)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.elevation = 0F
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        menu.findItem(R.id.menu_search).isVisible = false
        menu.findItem(R.id.menu_fav).isVisible = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        user = intent.getParcelableExtra(EXTRA_USER) as User

        if (item.itemId == android.R.id.home) {
            finish()
        } else if (item.itemId == R.id.menu_share) {
            if (MainActivity.checkNetwork(this)) {
                val share = Intent(Intent.ACTION_SEND)
                share.type = "text/plain"
                share.putExtra(
                    Intent.EXTRA_TEXT,
                    "${resources.getString(R.string.txt_name)} : $name" +
                            "\n${resources.getString(R.string.txt_username)} : ${user.login}" +
                            "\n${resources.getString(R.string.txt_location)} : $location" +
                            "\n${resources.getString(R.string.txt_company)} : $company"
                )
                startActivity(Intent.createChooser(share, resources.getString(R.string.txt_share_profile)))
            } else {
                Toast.makeText(
                    this@ProfileActivity,
                    resources.getString(R.string.txt_toast_network),
                    Toast.LENGTH_SHORT
                ).show()
            }

        } else if (item.itemId == R.id.menu_setting) {
            startActivity(Intent(this, SettingActivity::class.java))
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onRefresh() {
        progBar_profile.visibility = View.VISIBLE
        user = intent.getParcelableExtra(EXTRA_USER) as User
        if (MainActivity.checkNetwork(this)) {
            progBar_profile.visibility = View.GONE

            userViewModel.setUserDetail(user.login, resources.getString(R.string.value_place))

            val sectionsPagerAdapter = PagerAdapter(this, supportFragmentManager, user.login)
            view_pager.adapter = sectionsPagerAdapter
            tabLayout.setupWithViewPager(view_pager)
        } else {
            progBar_profile.visibility = View.GONE
            Toast.makeText(this@ProfileActivity, resources.getString(R.string.txt_toast_network), Toast.LENGTH_SHORT).show()
        }
        refresh_profile.isRefreshing = false
    }

    override fun onClick(view: View) {
        user = intent.getParcelableExtra(EXTRA_USER) as User

        val values = ContentValues()
        values.put(DatabaseContract.FavColumns.USERNAME, user.login)
        values.put(DatabaseContract.FavColumns.AVATAR_URL, user.avatar_url)

        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        if (MainActivity.checkNetwork(this)) {
            if (prefVal.equals("unfav")){
                editor.putString(user.login + "fav", "fav")

                contentResolver.insert(CONTENT_URI, values)

                Glide.with(this@ProfileActivity)
                    .load(resources.getDrawable(R.drawable.ic_baseline_favorite_24))
                    .into(btn_fav)
                Toast.makeText(this@ProfileActivity, resources.getString(R.string.txt_toast_fav), Toast.LENGTH_SHORT).show()
                reload()
            } else {
                editor.putString(user.login + "fav", "unfav")

                val uri = Uri.parse(CONTENT_URI.toString() + "/" + user.login)
                contentResolver.delete(uri, null, null)

                Glide.with(this@ProfileActivity)
                    .load(resources.getDrawable(R.drawable.ic_baseline_favorite_border_24))
                    .into(btn_fav)

                Toast.makeText(this@ProfileActivity, resources.getString(R.string.txt_toast_unfav), Toast.LENGTH_SHORT).show()
                reload()
            }
        } else {
            Toast.makeText(this@ProfileActivity, resources.getString(R.string.txt_toast_network), Toast.LENGTH_SHORT).show()
        }
        editor.apply()
        editor.commit()
    }

    private fun reload(){
        finish()
        overridePendingTransition(0, 0)
        startActivity(getIntent())
        overridePendingTransition(0, 0)
    }

}
