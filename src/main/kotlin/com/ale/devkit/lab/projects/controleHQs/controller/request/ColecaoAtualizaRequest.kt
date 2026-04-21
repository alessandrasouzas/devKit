package com.ale.devkit.lab.projects.controleHQs.controller.request

import com.ale.devkit.lab.projects.controleHQs.dto.api.enums.Status
import java.time.LocalDate

data class ColecaoAtualizaRequest(
    val titulo: String?,
    val categoria: String?,
    val editora: String?,
    val volume: Int?,
    val preco: Double?,
    val dataPublicacao: LocalDate?,
    val numeroPaginas: Int?,
    val caixa: Int?,
    val status: Status?
)