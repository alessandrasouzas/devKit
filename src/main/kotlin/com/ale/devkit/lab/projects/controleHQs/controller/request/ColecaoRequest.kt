package com.ale.devkit.lab.projects.controleHQs.controller.request

import com.ale.devkit.lab.projects.controleHQs.dto.api.enums.Status

data class ColecaoRequest (
    val titulo: String,
    val autor: String?,
    val isbn: String?,
    val categoria: String,
    val editora: String?,
    val volume: Int?,
    val preco: Double?,
    val caixa: Int?,
    val status: Status?
)