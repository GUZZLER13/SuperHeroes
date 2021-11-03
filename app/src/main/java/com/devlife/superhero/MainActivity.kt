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
        binding.rvHeroes.isVisible = false
        binding.weight.isVisible = false
        binding.combat.isVisible = false
        binding.alias.isVisible = false
        binding.align.isVisible = false
        binding.base.isVisible = false
        binding.durability.isVisible = false
        binding.eye.isVisible = false
        binding.race.isVisible = false
        binding.height.isVisible = false
        binding.intelligence.isVisible = false
        binding.gender.isVisible = false
        binding.speed.isVisible = false
        binding.power.isVisible = false
        binding.hair.isVisible = false
        binding.fullName.isVisible = false
        binding.placeBirth.isVisible = false
        binding.firstApp.isVisible = false
        binding.publisher.isVisible = false
        binding.occupation.isVisible = false
        binding.group.isVisible = false
        binding.relatives.isVisible = false
        binding.strength.isVisible = false

        val queryIntentId = intent.getStringExtra("hisId")
        val queryIntentName = intent.getStringExtra("hisName")
        val queryIntentAlign = intent.getStringExtra("hisAlignment")

        if (queryIntentId != null) {
            searchById(queryIntentId)
            binding.svHero.isVisible = false


        } else {
            searchById("${(1..731).random()}")
            binding.svHero.isVisible = true

//            Titre de la toolbar
//            title = "Search Your Super Hero"
        }

        hideKeyboard()

    }

    override fun onBackPressed() {
        super.onBackPressed()
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
    private fun searchByName(query: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val call = getRetrofit().create(ApiService::class.java).getHeroeByName("search/$query/")
            val response = call.body()
            //Retour sur le thrad principal car c'est là où s'exécute le UI
            runOnUiThread {
                if (call.isSuccessful) {
                    //il n'y a qu'un seul résultat -> affichage détail

                    if (response?.response == "success") {
                        when {
                            response.results.size == 1 -> {
                                //on cache la recyclerView et on affiche les détails
                                binding.rvHeroes.isVisible = false
                                binding.imHero.isVisible = true
                                binding.rvHeroes.isVisible = true
                                binding.weight.isVisible = true
                                binding.combat.isVisible = true
                                binding.alias.isVisible = true
                                binding.align.isVisible = true
                                binding.base.isVisible = true
                                binding.durability.isVisible = true
                                binding.eye.isVisible = true
                                binding.race.isVisible = true
                                binding.height.isVisible = true
                                binding.intelligence.isVisible = true
                                binding.gender.isVisible = true
                                binding.speed.isVisible = true
                                binding.power.isVisible = true
                                binding.hair.isVisible = true
                                binding.fullName.isVisible = true
                                binding.placeBirth.isVisible = true
                                binding.firstApp.isVisible = true
                                binding.publisher.isVisible = true
                                binding.occupation.isVisible = true
                                binding.group.isVisible = true
                                binding.relatives.isVisible = true
                                binding.strength.isVisible = true
                                //Récupération des données qui nous intéressent
                                val urlImage = response.results[0].image.url
                                val txtName = response.results[0].name
                                val fullName = response.results[0].biography.fullName
                                val alignment = response.results[0].biography.alignment
                                //On donne les valeurs à la vue
//                                binding.tvNameHero.text = txtName
//                                binding.tvAlignment.text = alignment
                                Glide.with(applicationContext).load(urlImage).centerCrop()
                                    .into(binding.imageHero)


                            }

                            //il y a plus d'un résultat -> affichage de la liste
                            response.results.size > 1 -> {

                                //on cache tout sauf la recyclerview
                                binding.rvHeroes.isVisible = true
                                binding.weight.isVisible = false
                                binding.combat.isVisible = false
                                binding.alias.isVisible = false
                                binding.align.isVisible = false
                                binding.base.isVisible = false
                                binding.durability.isVisible = false
                                binding.eye.isVisible = false
                                binding.race.isVisible = false
                                binding.height.isVisible = false
                                binding.intelligence.isVisible = false
                                binding.gender.isVisible = false
                                binding.speed.isVisible = false
                                binding.power.isVisible = false
                                binding.hair.isVisible = false
                                binding.fullName.isVisible = false
                                binding.placeBirth.isVisible = false
                                binding.firstApp.isVisible = false
                                binding.publisher.isVisible = false
                                binding.occupation.isVisible = false
                                binding.group.isVisible = false
                                binding.relatives.isVisible = false
                                binding.strength.isVisible = false
                                binding.imHero.isVisible = false
                                binding.name.isVisible = false


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


    private fun searchById(id: String) {

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
                    binding.rvHeroes.isVisible = true
                    binding.weight.isVisible = true
                    binding.combat.isVisible = true
                    binding.alias.isVisible = true
                    binding.align.isVisible = true
                    binding.base.isVisible = true
                    binding.durability.isVisible = true
                    binding.eye.isVisible = true
                    binding.race.isVisible = true
                    binding.height.isVisible = true
                    binding.intelligence.isVisible = true
                    binding.gender.isVisible = true
                    binding.speed.isVisible = true
                    binding.power.isVisible = true
                    binding.hair.isVisible = true
                    binding.fullName.isVisible = true
                    binding.placeBirth.isVisible = true
                    binding.firstApp.isVisible = true
                    binding.publisher.isVisible = true
                    binding.occupation.isVisible = true
                    binding.group.isVisible = true
                    binding.relatives.isVisible = true
                    binding.strength.isVisible = true


                    //Récupération des données qui nous intéressent
                    val txtName = response?.name
                    val urlImage = response?.image?.url
                    val fullName = response?.biography?.fullName
                    val alignment = response?.biography?.alignment
                    val weight = response?.appearance?.weight?.get(1)
                    val height = response?.appearance?.height?.get(1)
                    val race = response?.appearance?.race
                    val gender = response?.appearance?.gender
                    val intelligence = response?.powerstats?.intelligence
                    val strength = response?.powerstats?.strength
                    val speed = response?.powerstats?.speed
                    val durability = response?.powerstats?.durability
                    val power = response?.powerstats?.power
                    val combat = response?.powerstats?.combat
                    val eye = response?.appearance?.eyeColor
                    val hair = response?.appearance?.hairColor
                    val alias = response?.biography?.aliases
                    val place = response?.biography?.placeOfBirth
                    val firstApp = response?.biography?.firstAppearance
                    val publisher = response?.biography?.publisher
                    val occup = response?.work?.occupation
                    val base = response?.work?.base
                    val group = response?.connections?.groupAffiliation
                    val relatives = response?.connections?.relatives

                    //On donne les valeurs à la vue
                    binding.name.text = txtName
                    binding.fullName.text = "Full Name : ${fullName}"
                    binding.align.text = "Alignment : ${alignment}"
                    binding.weight.text = "Weight : ${weight}"
                    binding.height.text = "Height : ${height}"
                    binding.race.text = "Race : ${race}"
                    binding.gender.text = "Gender : ${gender}"
                    binding.intelligence.text = "Intelligence : ${intelligence}"
                    binding.strength.text = "Strength : ${strength}"
                    binding.speed.text = "Speed : ${speed}"
                    binding.durability.text = "Durability : ${durability}"
                    binding.power.text = "Power : ${power}"
                    binding.combat.text = "Combat : ${combat}"
                    binding.eye.text = "Eye-Color : ${eye}"
                    binding.hair.text = "Hair-Color : ${hair}"
                    binding.alias.text = "Aliases : ${alias}"
                    binding.placeBirth.text = "Place Of Birth : ${place}"
                    binding.publisher.text = "Publisher : ${publisher}"
                    binding.occupation.text = "Occupation : ${occup}"
                    binding.base.text = "Base : ${base}"
                    binding.group.text = "Group Affiliation : ${group}"
                    binding.firstApp.text = "First Appearance : ${firstApp}"
                    binding.relatives.text = "Relatives : ${relatives}"




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

