package com.ale.devkit.lab.projects.controleHQs.dto

data class PublicacaoCsvDTO(
    val id: String?,
    val titulo: String,
    val categoria: String,
    val editora: String?,
    val volume: Int?,
    val preco: Double?,
    val condicao: String?,
    val status: String?,
    val isbn: String?
)