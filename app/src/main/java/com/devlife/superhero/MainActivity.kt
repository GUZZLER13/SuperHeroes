package com.devlife.superhero

import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.devlife.superhero.data.Result
import com.devlife.superhero.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: HeroAdapter
    private val heroesResults = mutableListOf<Result>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //On donne le comportement a la searchView
        binding.svHero.setOnQueryTextListener(this)

        //On cache tous les éléments de la vue
        binding.imHero.isVisible = false
        binding.tvFullName.isVisible = false
        binding.tvAlignment.isVisible = false
        binding.tvTitleAlignment.isVisible = false
        binding.tvTitleFullName.isVisible = false
        binding.tvTitleNameHero.isVisible = false
        binding.tvNameHero.isVisible = false
        binding.rvHeroes.isVisible = false

        var queryIntent = intent.getStringExtra("hisId")
        if (queryIntent != null) {
            searchById(queryIntent)
            queryIntent = null
        } else {
            searchById("${(1..731).random()}")
        }

        hideKeyboard()
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
    fun searchByName(query: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val call = getRetrofit().create(ApiService::class.java).getHeroeByName("search/$query/")
            val response = call.body()
            //Retour sur le thrad principal car c'est là où s'exécute le UI
            runOnUiThread {
                if (call.isSuccessful) {
                    //il n'y a qu'un seul résultat -> affichage détail
                    when {
                        response?.results?.size!! == 1 -> {
                            //on cache la recyclerView et on affiche les détails
                            binding.rvHeroes.isVisible = false
                            binding.imHero.isVisible = true
                            binding.tvFullName.isVisible = true
                            binding.tvAlignment.isVisible = true
                            binding.tvTitleAlignment.isVisible = true
                            binding.tvTitleFullName.isVisible = true
                            binding.tvTitleNameHero.isVisible = true
                            binding.tvNameHero.isVisible = true
                            //Récupération des données qui nous intéressent
                            val urlImage = response.results[0].image.url
                            val txtName = response.results[0].name
                            val fullName = response.results[0].biography.fullName
                            val alignment = response.results[0].biography.alignment
                            //On donne les valeurs à la vue
                            binding.tvNameHero.text = txtName
                            binding.tvFullName.text = fullName
                            binding.tvAlignment.text = alignment
                            Glide.with(applicationContext).load(urlImage).centerCrop()
                                .into(binding.imageHero)


                        }

                        //il y a plus d'un résultat -> affichage de la liste
                        response.results.size > 1 -> {

                            //on cache tout sauf la recyclerview
                            binding.rvHeroes.isVisible = true
                            binding.imHero.isVisible = false
                            binding.tvFullName.isVisible = false
                            binding.tvAlignment.isVisible = false
                            binding.tvTitleAlignment.isVisible = false
                            binding.tvTitleFullName.isVisible = false
                            binding.tvTitleNameHero.isVisible = false
                            binding.tvNameHero.isVisible = false

                            //Initialisation de la RecyclerView
                            initRecyclerView()
                            //on efface la liste des résultats
                            heroesResults.clear()
                            //on met dans cette liste tous les résultats
                            heroesResults.addAll(response.results)
                            //On prévient la recyclerView que les données ont changé
                            adapter.notifyDataSetChanged()
                        }

                        //pas de résultat -> affichage d'un toast avec message
                        else -> {
                            showError("Pas de superhéro trouvé avec ce nom ... ")
                        }
                    }

                    // Pas de réponse de la requête
                } else {
                    showError("pas de réponse du serveur...")
                }
                //On cache le clavier
                hideKeyboard()
            }
        }
    }


    private fun searchById(id : String) {
        CoroutineScope(Dispatchers.IO).launch {
            val call = getRetrofit().create(ApiService::class.java)
                .getHeroById(id)
            val response = call.body()
            //Retour sur le thrad principal car c'est là où s'exécute le UI
            runOnUiThread {
                if (call.isSuccessful) {
                    //il n'y a qu'un seul résultat -> affichage détail

                    //on cache la recyclerView et on affiche les détails
                    binding.rvHeroes.isVisible = false
                    binding.imHero.isVisible = true
                    binding.tvFullName.isVisible = true
                    binding.tvAlignment.isVisible = true
                    binding.tvTitleAlignment.isVisible = true
                    binding.tvTitleFullName.isVisible = true
                    binding.tvTitleNameHero.isVisible = true
                    binding.tvNameHero.isVisible = true
                    //Récupération des données qui nous intéressent
                    val urlImage = response?.image?.url
                    val txtName = response?.name
                    val fullName = response?.biography?.fullName
                    val alignment = response?.biography?.alignment
                    //On donne les valeurs à la vue
                    binding.tvNameHero.text = txtName
                    binding.tvFullName.text = fullName
                    binding.tvAlignment.text = alignment
                    Glide.with(applicationContext).load(urlImage).centerCrop()
                        .into(binding.imageHero)

                }

                // Pas de réponse de la requête
                else {
                    showError("pas de réponse du serveur")
                }

                //On cache le clavier
                hideKeyboard()
            }
        }
    }


    //On valide le texte de la saisie
    override fun onQueryTextSubmit(query: String?): Boolean {
        if (!query.isNullOrEmpty()) {
            searchByName(query.lowercase())
//            searchRandom()
        }
        return true
    }

    //On modifie le texte de la saisie
    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }

    private fun initRecyclerView() {
        adapter = HeroAdapter(heroesResults)
        binding.rvHeroes.layoutManager = LinearLayoutManager(this)
        binding.rvHeroes.adapter = adapter
    }

    private fun hideKeyboard() {
        val imm: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.viewRoot.windowToken, 0)
    }

    private fun showError(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }


}

