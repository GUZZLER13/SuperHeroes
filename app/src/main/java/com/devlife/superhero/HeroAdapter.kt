package com.devlife.superhero

import android.content.Intent
import android.graphics.Color
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
        val id = results[position].id
        val txtFullName = results[position].biography.fullName
        val txtAlignment = results[position].biography.alignment
        holder.bind(item, txtName, txtFullName, txtAlignment)

        if (results[position].biography.alignment == "good") {
            holder.itemView.setBackgroundColor(Color.parseColor("#6aa84f"))
        } else if (results[position].biography.alignment == "bad") {
            holder.itemView.setBackgroundColor(Color.parseColor("#e06666"))
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, MainActivity::class.java)
            intent.putExtra("hisId", id)
            intent.putExtra("hisName", txtName)
            intent.putExtra("hisAlignment", txtAlignment)

            holder.itemView.context.startActivity(intent)
        }
    }


    override fun getItemCount(): Int = results.size


}