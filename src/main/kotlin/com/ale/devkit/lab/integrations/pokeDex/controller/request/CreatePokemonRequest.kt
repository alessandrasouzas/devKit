package com.ale.devkit.lab.integrations.pokeDex.controller.request

data class CreatePokemonRequest(
    val name: String,
    val type: String,
    val level: Int
)