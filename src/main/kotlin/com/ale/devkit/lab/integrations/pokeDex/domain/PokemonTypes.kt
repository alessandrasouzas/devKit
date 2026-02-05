package com.ale.devkit.lab.integrations.pokeDex.domain

import com.fasterxml.jackson.annotation.JsonProperty

data class PokemonTypes (
    @JsonProperty("type")
    val type: Type
)

data class Type (
    @JsonProperty("name")
    val name: String
)