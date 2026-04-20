package com.ale.devkit.lab.projects.controleHQs.dto

import java.time.LocalDate

data class PublicacaoCsv(
    val id: Long,
    val isbn: String,
    val titulo: String,
    val categoria: String,
    val editora: String?,
    val autors: String?,
    val volume: Int?,
    val preco: Double?,
    val condicao: String?,
    val status: Status?,
    val dataCadastro: LocalDate,
    val dataPublicacao: LocalDate?,
    val numeroPaginas: Int?,
    val caixa: Int?
)