package com.ale.devkit.lab.integrations.pokeDex.controller.response

data class PokemonResponse(

    val speciesName: String,
    val identificacao: Int,
    val tipos: List<String>,
    var evolucoes: List<String> = emptyList(),
    val habilidades: List<String>,
    val movimentos: List<String>,
    val peso: Int?,
    val altura: Int?
)