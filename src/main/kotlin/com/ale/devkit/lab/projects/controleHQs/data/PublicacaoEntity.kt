package com.ale.devkit.lab.projects.controleHQs.data

import com.ale.devkit.lab.projects.controleHQs.dto.Status
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
@Table(name = "publicacao")
data class PublicacaoEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(unique = true)
    val isbn: String,
    val titulo: String,
    val categoria: String,
    val editora: String?,
    val autors: String?,
    val volume: Int?,
    val preco: Double?,
    val condicao: String?,
    @Enumerated(EnumType.STRING)
    val status: Status?,
    val dataCadastro: LocalDate,
    val dataPublicacao: LocalDate?,
    val numeroPaginas: Int?,
    val caixa: Int?
)