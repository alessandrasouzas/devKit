package com.ale.devkit.lab.integrations.pokeDex.controller.response

data class PokemonResponse(
    val nome: String?,
    val identificacao: Int?,
    val tipos: List<String>,
    val peso: Int?,
    val altura: Int?,
    val habilidades: List<String>,
    val movimentos: List<String>
)

