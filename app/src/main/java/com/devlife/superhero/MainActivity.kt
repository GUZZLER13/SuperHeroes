package com.devlife.superhero

import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.devlife.superhero.api.ApiService
import com.devlife.superhero.data.Result
import com.devlife.superhero.databinding.ActivityMainBinding
import com.devlife.superhero.recyclerView.HeroAdapter
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
        showOrHideRecyclerView(boolRV = false, boolOthers = false)

        val queryIntentId = intent.getStringExtra("hisId")
//        val queryIntentName = intent.getStringExtra("hisName")


        if (queryIntentId != null) {
            searchById(queryIntentId)
            binding.svHero.isVisible = true


            hideUnnecessaryAndChangeColor(true)


        } else {
            searchById("${(1..731).random()}")
            binding.svHero.isVisible = true


            hideUnnecessaryAndChangeColor(true)

//            Titre de la toolbar
//            title = "Search Your Super Hero"
        }

        hideKeyboard()

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

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
                            //au moins deux lettres dans la recherche
                            query.length < 2 -> {
                                showError("Minimum 2 lettres dans la recherche")
                            }
                            response.results.size == 1 -> {
                                //il n'y a qu'un seul résultat -> affichage détail
                                //on cache la recyclerView et on affiche les détails
                                showOrHideRecyclerView(boolRV = false, boolOthers = true)

                                //Récupération des données qui nous intéressent
                                val txtName = response.results[0].name
                                val urlImage = response.results[0].image.url
                                val fullName = response.results[0].biography.fullName
                                val alignment = response.results[0].biography.alignment
                                val weight = response.results[0].appearance.weight[1]
                                val height = response.results[0].appearance.height[1]
                                val race = response.results[0].appearance.race
                                val gender = response.results[0].appearance.gender
                                val intelligence = response.results[0].powerstats.intelligence
                                val strength = response.results[0].powerstats.strength
                                val speed = response.results[0].powerstats.speed
                                val durability = response.results[0].powerstats.durability
                                val power = response.results[0].powerstats.power
                                val combat = response.results[0].powerstats.combat
                                val eye = response.results[0].appearance.eyeColor
                                val hair = response.results[0].appearance.hairColor
                                val alias = response.results[0].biography.aliases.toString().replace("[", "").replace("]", "")
                                val place = response.results[0].biography.placeOfBirth
                                val firstApp = response.results[0].biography.firstAppearance
                                val publisher = response.results[0].biography.publisher
                                val occup = response.results[0].work.occupation
                                val base = response.results[0].work.base
                                val group = response.results[0].connections.groupAffiliation
                                val relatives = response.results[0].connections.relatives


                                //On donne les valeurs à la vue
                                binding.name.text = txtName
                                binding.fullName.text = "Full Name : $fullName"
                                binding.align.text = "Alignment : $alignment"
                                binding.weight.text = "Weight : $weight"
                                binding.height.text = "Height : $height"
                                binding.race.text = "Race : $race"
                                binding.gender.text = "Gender : $gender"
                                binding.intelligence.text = "Intelligence : $intelligence"
                                binding.strength.text = "Strength : $strength"
                                binding.speed.text = "Speed : $speed"
                                binding.durability.text = "Durability : $durability"
                                binding.power.text = "Power : $power"
                                binding.combat.text = "Combat : $combat"
                                binding.eye.text = "Eye-Color : $eye"
                                binding.hair.text = "Hair-Color : $hair"
                                binding.alias.text = "Aliases : $alias"
                                binding.placeBirth.text = "Place Of Birth : $place"
                                binding.publisher.text = "Publisher : $publisher"
                                binding.occupation.text = "Occupation : $occup"
                                binding.base.text = "Base : $base"
                                binding.group.text = "Group Affiliation : $group"
                                binding.firstApp.text = "First Appearance : $firstApp"
                                binding.relatives.text = "Relatives : ${relatives}"

                                if (alias == "-") {
                                    binding.alias.isVisible = false
                                }
                                if (txtName == "-") {
                                    binding.name.isVisible = false
                                }
                                if (fullName == "-") {
                                    binding.fullName.isVisible = false
                                }
                                if (alignment == "-") {
                                    binding.align.isVisible = false
                                }
                                if (weight == "-") {
                                    binding.weight.isVisible = false
                                }
                                if (height == "-") {
                                    binding.height.isVisible = false
                                }
                                if (race == "-") {
                                    binding.race.isVisible = false
                                }
                                if (gender == "-") {
                                    binding.gender.isVisible = false
                                }
                                if (intelligence == "-") {
                                    binding.intelligence.isVisible = false
                                }
                                if (strength == "-") {
                                    binding.strength.isVisible = false
                                }
                                if (speed == "-") {
                                    binding.speed.isVisible = false
                                }
                                if (durability == "-") {
                                    binding.combat.isVisible = false
                                }
                                if (power == "-") {
                                    binding.power.isVisible = false
                                }
                                if (combat == "-") {
                                    binding.combat.isVisible = false
                                }
                                if (eye == "-") {
                                    binding.eye.isVisible = false
                                }
                                if (hair == "-") {
                                    binding.hair.isVisible = false
                                }
                                if (place == "-") {
                                    binding.placeBirth.isVisible = false
                                }
                                if (firstApp == "-") {
                                    binding.firstApp.isVisible = false
                                }
                                if (publisher == "-") {
                                    binding.publisher.isVisible = false
                                }
                                if (occup == "-") {
                                    binding.occupation.isVisible = false
                                }
                                if (base == "-") {
                                    binding.base.isVisible = false
                                }
                                if (group == "-") {
                                    binding.group.isVisible = false
                                }
                                if (relatives == "-") {
                                    binding.relatives.isVisible = false
                                }




                                Glide.with(applicationContext).load(urlImage).centerCrop()
                                    .into(binding.imageHero)

                                hideUnnecessaryAndChangeColor(true)
                            }


                            //il y a plus d'un résultat -> affichage de la liste
                            response.results.size > 1 -> {
                                "${response.results.size} SuperHeroes Found".also {
                                    binding.numbers.text = it
                                }

                                //valeur SDK
//                                val sdk = Build.VERSION.SDK_INT

                                binding.viewRoot.setBackgroundColor(Color.parseColor("#000000"))


                                //on cache tout sauf la recyclerview
                                showOrHideRecyclerView(boolRV = true, boolOthers = false)

                                //Initialisation de la RecyclerView
                                initRecyclerView()
                                //on efface la liste des résultats
                                heroesResults.clear()
                                //on met dans cette liste tous les résultats
                                heroesResults.addAll(response.results)
                                //On prévient la recyclerView que les données ont changé
                                adapter.notifyItemInserted(response.results.size)

                                binding.rvHeroes.addItemDecoration(
                                    DividerItemDecoration(
                                        applicationContext,
                                        LinearLayoutManager.VERTICAL


                                    )


                                )

                                binding.root.setBackgroundColor(Color.parseColor("#000000"))
                                hideUnnecessaryAndChangeColor(true)
                            }
                        }
                    } else if (response?.response == "error") {
                        showError(getString(R.string.trySearch))
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

                    //on cache la recyclerView et on affiche les détails
                    showOrHideRecyclerView(boolRV = false, boolOthers = true)

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
                    val alias =
                        response?.biography?.aliases.toString().replace("[", "").replace("]", "")
                    val place = response?.biography?.placeOfBirth
                    val firstApp = response?.biography?.firstAppearance
                    val publisher = response?.biography?.publisher
                    val occupation = response?.work?.occupation
                    val base = response?.work?.base
                    val group = response?.connections?.groupAffiliation
                    val relatives = response?.connections?.relatives


                    //On donne les valeurs à la vue
                    binding.name.text = txtName
                    binding.fullName.text = "Full Name : $fullName"
                    binding.align.text = "Alignment : $alignment"
                    binding.weight.text = "Weight : $weight"
                    binding.height.text = "Height : $height"
                    binding.race.text = "Race : $race"
                    binding.gender.text = "Gender : $gender"
                    binding.intelligence.text = "Intelligence : $intelligence"
                    binding.strength.text = "Strength : $strength"
                    binding.speed.text = "Speed : $speed"
                    binding.durability.text = "Durability : $durability"
                    binding.power.text = "Power : $power"
                    binding.combat.text = "Combat : $combat"
                    binding.eye.text = "Eye-Color : $eye"
                    binding.hair.text = "Hair-Color : $hair"
                    binding.alias.text = "Aliases : $alias"
                    binding.placeBirth.text = "Place Of Birth : $place"
                    binding.publisher.text = "Publisher : $publisher"
                    binding.occupation.text = "Occupation : $occupation"
                    binding.base.text = "Base : $base"
                    binding.group.text = "Group Affiliation : $group"
                    binding.firstApp.text = "First Appearance : $firstApp"
                    binding.relatives.text = "Relatives : ${relatives}"

                    if (alias == "-") {
                        binding.alias.isVisible = false
                    }
                    if (txtName == "-") {
                        binding.name.isVisible = false
                    }
                    if (fullName == "-") {
                        binding.fullName.isVisible = false
                    }
                    if (alignment == "-") {
                        binding.align.isVisible = false
                    }
                    if (weight == "-") {
                        binding.weight.isVisible = false
                    }
                    if (height == "-") {
                        binding.height.isVisible = false
                    }
                    if (race == "-") {
                        binding.race.isVisible = false
                    }
                    if (gender == "-") {
                        binding.gender.isVisible = false
                    }
                    if (intelligence == "-") {
                        binding.intelligence.isVisible = false
                    }
                    if (strength == "-") {
                        binding.strength.isVisible = false
                    }
                    if (speed == "-") {
                        binding.speed.isVisible = false
                    }
                    if (durability == "-") {
                        binding.combat.isVisible = false
                    }
                    if (power == "-") {
                        binding.power.isVisible = false
                    }
                    if (combat == "-") {
                        binding.combat.isVisible = false
                    }
                    if (eye == "-") {
                        binding.eye.isVisible = false
                    }
                    if (hair == "-") {
                        binding.hair.isVisible = false
                    }
                    if (place == "-") {
                        binding.placeBirth.isVisible = false
                    }
                    if (firstApp == "-") {
                        binding.firstApp.isVisible = false
                    }
                    if (publisher == "-") {
                        binding.publisher.isVisible = false
                    }
                    if (occupation == "-") {
                        binding.occupation.isVisible = false
                    }
                    if (base == "-") {
                        binding.base.isVisible = false
                    }
                    if (group == "-") {
                        binding.group.isVisible = false
                    }
                    if (relatives == "-") {
                        binding.relatives.isVisible = false
                    }




                    binding.viewRoot.setBackgroundColor(Color.parseColor("#000000"))


//                    when (response?.biography?.alignment) {
//                        "good" -> {
//                            binding.viewRoot.setBackgroundColor(Color.parseColor("#559900"))
//                        }
//                        "bad" -> {
//                            binding.viewRoot.setBackgroundColor(Color.parseColor("#cc181e"))
//                        }
//                        "neutral" -> {
//                            binding.viewRoot.setBackgroundColor(Color.parseColor("#2793e8"))
//                        }
//                        else -> {
//                            binding.viewRoot.setBackgroundColor(Color.parseColor("#000000"))
//                        }
//
//                    }


                    Glide.with(applicationContext).load(urlImage).centerCrop()
                        .into(binding.imageHero)


                    hideUnnecessaryAndChangeColor(true)
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
        }
        return false
    }

    //On modifie le texte de la saisie
    override fun onQueryTextChange(newText: String?): Boolean {
        return false
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
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }

    private fun showOrHideRecyclerView(boolRV: Boolean, boolOthers: Boolean) {


        binding.rvHeroes.isVisible = boolRV
        binding.imHero.isVisible = boolOthers
        binding.weight.isVisible = boolOthers
        binding.combat.isVisible = boolOthers
        binding.alias.isVisible = boolOthers
        binding.numbers.isVisible = boolRV
        binding.align.isVisible = boolOthers
        binding.base.isVisible = boolOthers
        binding.name.isVisible = boolOthers
        binding.durability.isVisible = boolOthers
        binding.eye.isVisible = boolOthers
        binding.race.isVisible = boolOthers
        binding.height.isVisible = boolOthers
        binding.intelligence.isVisible = boolOthers
        binding.gender.isVisible = boolOthers
        binding.speed.isVisible = boolOthers
        binding.power.isVisible = boolOthers
        binding.hair.isVisible = boolOthers
        binding.fullName.isVisible = boolOthers
        binding.placeBirth.isVisible = boolOthers
        binding.firstApp.isVisible = boolOthers
        binding.publisher.isVisible = boolOthers
        binding.occupation.isVisible = boolOthers
        binding.group.isVisible = boolOthers
        binding.relatives.isVisible = boolOthers
        binding.strength.isVisible = boolOthers
    }

    private fun hideUnnecessaryAndChangeColor(changeColorText: Boolean) {
        val listTextView = mutableListOf<TextView>()


        listTextView.add(binding.weight)
        listTextView.add(binding.combat)
        listTextView.add(binding.alias)
        listTextView.add(binding.align)
        listTextView.add(binding.base)
        listTextView.add(binding.gender)
        listTextView.add(binding.numbers)
//        list.add(binding.name)
        listTextView.add(binding.durability)
        listTextView.add(binding.eye)
        listTextView.add(binding.race)
        listTextView.add(binding.height)
        listTextView.add(binding.intelligence)
        listTextView.add(binding.speed)
        listTextView.add(binding.power)
        listTextView.add(binding.hair)
        listTextView.add(binding.fullName)
        listTextView.add(binding.placeBirth)
        listTextView.add(binding.firstApp)
        listTextView.add(binding.publisher)
        listTextView.add(binding.occupation)
        listTextView.add(binding.group)
        listTextView.add(binding.strength)
        listTextView.add(binding.firstApp)
        listTextView.add(binding.relatives)


//        list.add(binding.relatives.toString())

        for (element in listTextView) {
            if (element.text.contains("[-]") || element.text == "-" || element.text == "- " || element.text.contains(
                    "null"
                )
                || element.text.contains("0 kg") || element.text.contains("0 cm")
                || element.text.isNullOrEmpty() || element.text == "Full Name : "
            ) {
                element.isVisible = false
            }







            if (changeColorText) {
                element.setTextColor(Color.parseColor("#eeeeee"))
                binding.name.setTextColor(Color.parseColor("#eeeeee"))

            }
        }
    }
}