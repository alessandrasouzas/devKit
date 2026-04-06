package com.ale.devkit.lab.projects.controleHQs.dto

data class ColecaoMessage(
    val id: Long,
    val titulo: String,
    val categoria: String,
    val editora: String?,
    val volume: Int?,
    val preco: Double?
)