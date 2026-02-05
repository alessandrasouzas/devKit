package com.ale.devkit.lab.integrations.pokeDex.client

import com.ale.devkit.lab.integrations.pokeDex.controller.response.dto.PokeDexIntegrationResponse
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class PokedexClient (

    private val okHttpClient: OkHttpClient,
    private val objectMapper: ObjectMapper,

    @Value("\${integration.pokedex.url}")
    private val pokedexUrl: String
){

    fun buscarPokemon(nome: String): PokeDexIntegrationResponse{

        val url = "$pokedexUrl/$nome/"

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw Exception(
                        "Erro ao chamar pokedex. HTTP status: ${response.code}"
                    )
                }

                val responseBody = response.body?.string()
                    ?: throw Exception(
                        "Resposta do pokedex sem corpo"
                    )

                val PokeDexResponse = objectMapper.readValue(responseBody, PokeDexIntegrationResponse::class.java)

                return PokeDexResponse
            }
        }catch (ex: Exception) {
            throw Exception(
                "Falha ao integrar com a pokedex",
                ex
            )
        }
    }

}