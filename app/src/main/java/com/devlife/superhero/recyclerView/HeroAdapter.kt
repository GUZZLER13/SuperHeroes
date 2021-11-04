package com.devlife.superhero.recyclerView

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devlife.superhero.MainActivity
import com.devlife.superhero.R
import com.devlife.superhero.data.Result

//RecyclerView Adapter
class HeroAdapter(private val results: List<Result>) : RecyclerView.Adapter<HeroViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeroViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return HeroViewHolder(layoutInflater.inflate(R.layout.item_hero, parent, false))
    }

    override fun onBindViewHolder(holder: HeroViewHolder, position: Int) {
        val image = results[position].image.url
        val txtName = results[position].name
        val id = results[position].id
        val publi = results[position].biography.publisher

        holder.bind(image, txtName, publi)

        when (results[position].biography.alignment) {
            "good" -> {
                holder.itemView.setBackgroundColor(Color.parseColor("#559900"))
            }
            "bad" -> {
                holder.itemView.setBackgroundColor(Color.parseColor("#cc181e"))
            }
            "neutral" -> {
                holder.itemView.setBackgroundColor(Color.parseColor("#2793e8"))
            }
            else -> {
                holder.itemView.setBackgroundColor(Color.parseColor("#000000"))
            }
        }


        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, MainActivity::class.java)
            intent.putExtra("hisId", id)
            intent.putExtra("hisName", txtName)

            holder.itemView.context.startActivity(intent)
        }
    }


    override fun getItemCount(): Int = results.size


}