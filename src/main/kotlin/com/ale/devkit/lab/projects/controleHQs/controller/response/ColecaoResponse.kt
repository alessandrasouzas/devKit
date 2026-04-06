package com.ale.devkit.lab.projects.controleHQs.controller.response

data class ColecaoResponse(
    val id: String,
    val titulo: String,
    val categoria: String,
    val preco: Double?,
    val status: String?
)