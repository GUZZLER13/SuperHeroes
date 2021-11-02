package com.devlife.superhero

import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.devlife.superhero.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.svHero.setOnQueryTextListener(this)
        binding.imHero.isVisible = false
        binding.tvFullName.isVisible = false
        binding.tvAlignment.isVisible = false
        binding.tvTitleAlignment.isVisible = false
        binding.tvTitleFullName.isVisible = false
        binding.tvTitleNameHero.isVisible = false


    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (!query.isNullOrEmpty()) {
            searchByName(query.lowercase())
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }


    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            //IMPORTANT : toujours terminer cette adresse par le slash
            .baseUrl("https://superheroapi.com/api/10227012922849582/")
            // GsonConverterFactory --> librairie ajoutée dans le gradle qui convertit le json pour notre réponse
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    }

    //Utilisation Coroutines pour requete asynchrone
    private fun searchByName(query: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val call = getRetrofit().create(ApiService::class.java).getHeroeByName("search/$query/")
            val response = call.body()
            //Retour sur le thrad principal car c'est là où s'exécute le UI
            runOnUiThread {
                if (call.isSuccessful) {
                    binding.imHero.isVisible = true
                    binding.tvFullName.isVisible = true
                    binding.tvAlignment.isVisible = true
                    binding.tvTitleAlignment.isVisible = true
                    binding.tvTitleFullName.isVisible = true
                    binding.tvTitleNameHero.isVisible = true
                    val urlImage = response?.results?.get(0)?.image?.url
                    val txtName = response?.results?.get(0)?.name
                    val fullName = response?.results?.get(0)?.biography?.fullName
                    val alignment = response?.results?.get(0)?.biography?.alignment
                    binding.tvNameHero.text = txtName
                    binding.tvFullName.text = fullName
                    binding.tvAlignment.text = alignment
                    Glide.with(applicationContext).load(urlImage).centerCrop().into(binding.imageHero)


                } else {
                    //show error
                    showError()
                }

                hideKeyboard()
            }

        }
    }

    private fun hideKeyboard() {
        val imm: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.viewRoot.windowToken, 0)
    }

    private fun showError() {
        Toast.makeText(this, "pas d'image...", Toast.LENGTH_SHORT).show()
    }


}

