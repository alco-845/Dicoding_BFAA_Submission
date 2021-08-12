package com.alcorp.githubuser.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alcorp.githubuser.R
import com.alcorp.githubuser.model.Fav
import com.alcorp.githubuser.model.User
import com.alcorp.githubuser.view.MainActivity
import com.alcorp.githubuser.view.ProfileActivity
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
            with(itemView){
                tv_item_name.text = fav.login
                if(MainActivity.checkNetwork(context)){
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

                val usr = User(
                    fav.login,
                    fav.avatar_url
                )

                itemView.setOnClickListener {
                    val intent = Intent(context, ProfileActivity::class.java)
                    intent.putExtra(ProfileActivity.EXTRA_USER, usr)
                    context?.startActivity(intent)
                }
            }
        }
    }
}