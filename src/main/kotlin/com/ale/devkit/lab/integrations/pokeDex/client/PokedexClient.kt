package com.ale.devkit.lab.integrations.pokeDex.client

import com.ale.devkit.lab.integrations.pokeDex.controller.response.EvolutionChainResponse
import com.ale.devkit.lab.integrations.pokeDex.controller.response.PokemonSpeciesResponse
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
    private val pokedexUrl: String,

    @Value("\${integration.pokedex.chain.url}")
    private val pokedexChainUrl: String,

    @Value("\${integration.pokedex.species.url}")
    private val pokedexEspeciesUrl: String
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

    fun buscarEvolutionChain(id: Int): EvolutionChainResponse {
        val url = "$pokedexChainUrl/$id/"

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

                val evolutionChainResponse = objectMapper.readValue(responseBody, EvolutionChainResponse::class.java)

                return evolutionChainResponse
            }
        }catch (ex: Exception) {
            throw Exception(
                "Falha ao integrar com a pokedex",
                ex
            )
        }
    }

    fun buscarEvolucaoPorPokemon(nome: String): EvolutionChainResponse {
        val species = buscarSpecies(nome)

        val evolutionChainId = species.evolutionChain.url
            .trimEnd('/')
            .substringAfterLast('/')
            .toInt()

        return buscarEvolutionChain(evolutionChainId)
    }


    fun buscarSpecies(nome: String): PokemonSpeciesResponse {
        val request = Request.Builder()
            .url("$pokedexEspeciesUrl/$nome")
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

                val response = objectMapper.readValue(responseBody, PokemonSpeciesResponse::class.java)

                return response
            }
        }catch (ex: Exception) {
            throw Exception(
                "Falha ao integrar com a pokedex",
                ex
            )
        }
    }
}