package com.alcorp.consumergithubuser.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.alcorp.consumergithubuser.R
import com.alcorp.consumergithubuser.database.DatabaseContract.FavColumns.Companion.CONTENT_URI
import com.alcorp.consumergithubuser.model.Fav
import com.alcorp.consumergithubuser.view.FavActivity
import com.alcorp.consumergithubuser.viewmodel.UserViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.item_list.view.*

class FavAdapter : RecyclerView.Adapter<FavAdapter.FavViewHolder>() {
    var listFav = ArrayList<Fav>()

    fun setData(items: ArrayList<Fav>) {
        listFav.clear()
        listFav.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        return FavViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listFav.size
    }

    override fun onBindViewHolder(holder: FavViewHolder, position: Int) {
        holder.bind(listFav[position])
    }

    class FavViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(fav: Fav){
            with(itemView) {
                tv_item_name.text = fav.login
                if(checkNetwork(context)){
                    Glide.with(itemView.context)
                        .load(fav.avatar_url)
                        .apply(RequestOptions().override(55, 55))
                        .into(item_img)
                } else {
                    Glide.with(itemView.context)
                        .load(resources.getDrawable(R.drawable.def_img))
                        .apply(RequestOptions().override(55, 55))
                        .into(item_img)
                }
            }
        }

        private fun checkNetwork(context: Context?): Boolean{
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
}