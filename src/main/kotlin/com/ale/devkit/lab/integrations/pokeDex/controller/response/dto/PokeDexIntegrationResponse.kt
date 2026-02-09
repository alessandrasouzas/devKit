package com.ale.devkit.lab.integrations.pokeDex.controller.response.dto

import com.ale.devkit.lab.integrations.pokeDex.controller.response.PokemonResponse
import com.ale.devkit.lab.integrations.pokeDex.domain.PokemonAbilities
import com.ale.devkit.lab.integrations.pokeDex.domain.PokemonMoves
import com.ale.devkit.lab.integrations.pokeDex.domain.PokemonTypes
import com.fasterxml.jackson.annotation.JsonProperty

data class PokeDexIntegrationResponse (

    @JsonProperty("name")
    val name: String,
    @JsonProperty("types")
    val types: List<PokemonTypes>,
    @JsonProperty("weight")
    val weight: String,
    @JsonProperty("height")
    val height: String,
    @JsonProperty("abilities")
    val abilities: List<PokemonAbilities>,
    @JsonProperty("id")
    val id: String,
    @JsonProperty("moves")
    val moves: List<PokemonMoves>
)

fun PokeDexIntegrationResponse.toPokemon(): PokemonResponse {
    return PokemonResponse(
        tipos = types?.map { it.type.name }.orEmpty(),
        peso = weight.toIntOrNull(),
        altura = height.toIntOrNull(),
        habilidades = abilities?.map { it.ability.name }.orEmpty(),
        identificacao = id.toInt(),
        movimentos = moves
            ?.take(10)
            ?.map { it.move.name }
            .orEmpty(),
        speciesName = name.lowercase(),
        categoria = null,
        habitat = null,
        lendario = null,
        mitico = null
    )
}