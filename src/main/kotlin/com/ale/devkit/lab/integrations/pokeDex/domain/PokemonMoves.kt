package com.ale.devkit.lab.integrations.pokeDex.domain

import com.fasterxml.jackson.annotation.JsonProperty

data class PokemonMoves (
    @JsonProperty("move")
    val move: Move
)

data class Move(
    @JsonProperty("name")
    val name: String
)