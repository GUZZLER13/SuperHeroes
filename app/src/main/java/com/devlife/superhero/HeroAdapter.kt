package com.devlife.superhero

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devlife.superhero.data.Result

//RecyclerView Adapter
class HeroAdapter(private val results: List<Result>) : RecyclerView.Adapter<HeroViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeroViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return HeroViewHolder(layoutInflater.inflate(R.layout.item_hero, parent, false))
    }

    override fun onBindViewHolder(holder: HeroViewHolder, position: Int) {
        val item = results[position].image.url
        val txtName = results[position].name
        val txtFullName = results[position].biography.fullName
        holder.bind(item, txtName, txtFullName)
    }

    override fun getItemCount(): Int = results.size

}