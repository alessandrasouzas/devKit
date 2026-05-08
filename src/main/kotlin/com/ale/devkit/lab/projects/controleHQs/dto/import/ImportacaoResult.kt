package com.ale.devkit.lab.projects.controleHQs.dto.import

data class ImportacaoResult(
    val total: Int,
    val inseridos: Int,
    val atualizados: Int,
    val ignorados: Int,
    val erros: List<String>
)