package com.devlife.superhero

import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.devlife.superhero.databinding.ItemHeroBinding

class HeroViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemHeroBinding.bind(view)


    fun bind(image: String, textName: String, textFullName: String, textAlignment: String) {
        Glide.with(itemView).load(image).into(binding.rvImageHero)
        binding.rvTxtName.text = textName
        binding.rvTxtFullName.text = textFullName
        binding.rvTxtAlignment.text = textAlignment

        itemView.setOnClickListener {
            Toast.makeText(
                itemView.context,
                "tu as sélectionné ${binding.rvTxtName.text}",
                Toast.LENGTH_LONG
            ).show()

        }


    }


}