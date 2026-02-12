package com.ale.devkit.lab.integrations.pokeDex.controller.dto

data class PokemonMetadata(
    val trainerId: String,
    val correlationId: String?,
    val source: String?
)

