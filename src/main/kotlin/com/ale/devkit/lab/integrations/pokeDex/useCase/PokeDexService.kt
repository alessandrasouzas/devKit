package com.ale.devkit.lab.integrations.pokeDex.useCase

import com.ale.devkit.lab.integrations.pokeDex.client.PokedexClient
import com.ale.devkit.lab.integrations.pokeDex.controller.response.PokemonResponse
import com.ale.devkit.lab.integrations.pokeDex.controller.response.dto.toPokemon
import com.ale.devkit.lab.integrations.pokeDex.controller.response.toEvolutionsFrom
import org.springframework.stereotype.Service

@Service
class PokeDexService (
    private val client: PokedexClient
){
    fun getPokemon(nome: String): PokemonResponse {
        val pokemon = client.buscarPokemon(nome).toPokemon()
        val evolutionChain = client.buscarEvolucaoPorPokemon(nome)

        pokemon.evolucoes = evolutionChain.toEvolutionsFrom(pokemon.speciesName)
        return pokemon
    }
}