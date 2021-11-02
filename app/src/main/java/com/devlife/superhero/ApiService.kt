package com.devlife.superhero

import com.devlife.superhero.data.Hero
import com.devlife.superhero.data.Image
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface ApiService {

    //Requete de type GET
    @GET
    suspend fun getHeroeByName(@Url url: String): Response<Hero>
}