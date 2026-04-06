package com.ale.devkit.lab.projects.controleHQs.data

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "publicacao")
data class PublicacaoEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val titulo: String,
    val categoria: String,
    val editora: String?,
    val volume: Int?,
    val preco: Double?,
    val condicao: String?,
    val status: String?,
    val isbn: String?,
    val dataCadastro: LocalDateTime
)