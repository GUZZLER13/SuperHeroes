package com.devlife.superhero.recyclerView

import android.graphics.Color
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.devlife.superhero.databinding.ItemHeroBinding

class HeroViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemHeroBinding.bind(view)

    fun bind(image: String, textName: String, publisher: String) {
        Glide.with(itemView).load(image).into(binding.rvImageHero)
        binding.rvTxtName.text = textName
        binding.rvPubli.text = publisher

        if (binding.rvPubli.text == "null") {
            binding.rvPubli.isVisible = false
        }

        binding.rvTxtName.setTextColor(Color.parseColor("#eeeeee"))
        binding.rvPubli.setTextColor(Color.parseColor("#eeeeee"))


//        itemView.setOnClickListener {
//            Toast.makeText(
//                itemView.context,
//                "tu as sélectionné ${binding.rvTxtName.text}",
//                Toast.LENGTH_LONG
//            ).show()
//
//        }
    }
}