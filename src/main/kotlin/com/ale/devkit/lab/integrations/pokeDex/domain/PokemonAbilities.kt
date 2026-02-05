package com.ale.devkit.lab.integrations.pokeDex.domain

import com.fasterxml.jackson.annotation.JsonProperty

data class PokemonAbilities (
    @JsonProperty("ability")
    val ability: Abilitie
)

data class Abilitie(
    @JsonProperty("name")
    val name: String
)