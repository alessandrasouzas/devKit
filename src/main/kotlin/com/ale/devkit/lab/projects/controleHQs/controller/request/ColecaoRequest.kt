package com.ale.devkit.lab.projects.controleHQs.controller.request

data class ColecaoRequest (
    val titulo: String,
    val categoria: String,
    val editora: String?,
    val volume: Int?,
    val preco: Double?
)