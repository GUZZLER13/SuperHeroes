package com.devlife.superhero

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.devlife.superhero.databinding.ItemHeroBinding

class HeroViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemHeroBinding.bind(view)

    fun bind(image: String, textName: String, textFullName: String) {
        Glide.with(itemView).load(image).into(binding.rvImageHero)
        binding.rvTxtName.text = textName
        binding.rvTxtFullName.text = textFullName

    }
}