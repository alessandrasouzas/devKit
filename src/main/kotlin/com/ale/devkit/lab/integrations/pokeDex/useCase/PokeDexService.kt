package com.ale.devkit.lab.integrations.pokeDex.useCase

import com.ale.devkit.lab.integrations.pokeDex.client.PokedexClient
import com.ale.devkit.lab.integrations.pokeDex.controller.response.PokemonResponse
import com.ale.devkit.lab.integrations.pokeDex.controller.response.dto.PokeDexIntegrationResponse
import com.ale.devkit.lab.integrations.pokeDex.controller.response.dto.toDomain
import org.springframework.stereotype.Service

@Service
class PokeDexService (
    private val client: PokedexClient
){
    fun getPokemon(string: String): PokemonResponse? {
        return client.buscarPokemon(string).toDomain()
    }
}