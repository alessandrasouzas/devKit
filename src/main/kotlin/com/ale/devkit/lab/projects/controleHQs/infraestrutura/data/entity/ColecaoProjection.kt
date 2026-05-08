package com.ale.devkit.lab.projects.controleHQs.infraestrutura.data.entity

import com.ale.devkit.lab.projects.controleHQs.dto.api.enums.Status

interface ColecaoProjection {
    val isbn: String
    val titulo: String
    val categoria: String
    val autors: String?
    val editora: String?
    val volume: Int?
    val preco: Double?
    val caixa: Int?
    val status: Status?
}