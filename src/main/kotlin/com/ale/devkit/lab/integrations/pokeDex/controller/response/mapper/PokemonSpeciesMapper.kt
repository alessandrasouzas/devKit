package com.ale.devkit.lab.integrations.pokeDex.controller.response.mapper

import com.ale.devkit.lab.integrations.pokeDex.controller.response.PokemonResponse
import com.ale.devkit.lab.integrations.pokeDex.controller.response.PokemonSpeciesResponse

fun PokemonSpeciesResponse.applyTo(pokemon: PokemonResponse) {
    pokemon.habitat = habitat?.name
    pokemon.lendario = if (legendary == true) "Yes" else "No"
    pokemon.mitico = if (mythical == true) "Yes" else "No"
    pokemon.categoria = genera
        .firstOrNull { it.language.name == "en" }
        ?.genus
}