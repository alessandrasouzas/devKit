package com.ale.devkit.lab.projects.controleHQs.dto.export

import com.ale.devkit.lab.projects.controleHQs.dto.api.enums.Status
import java.time.LocalDate

data class ColecaoToCsv(
    val id: Long,
    val isbn: String,
    val titulo: String,
    val categoria: String,
    val editora: String?,
    val autors: String?,
    val volume: Int?,
    val preco: Double?,
    val status: Status?,
    val dataCadastro: LocalDate,
    val dataPublicacao: LocalDate?,
    val numeroPaginas: Int?,
    val caixa: Int?
)