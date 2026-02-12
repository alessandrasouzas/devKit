package com.ale.devkit.lab.integrations.pokeDex.controller.dto

data class PokemonResponse(

    val speciesName: String,
    val identificacao: Int,
    val tipos: List<String>,
    var categoria: String?,
    var habitat: String?,
    var evolucoes: List<String> = emptyList(),
    var lendario: String?,
    var mitico: String?,
    val habilidades: List<String>?,
    val movimentos: List<String>?,
    val peso: Int?,
    val altura: Int?
)