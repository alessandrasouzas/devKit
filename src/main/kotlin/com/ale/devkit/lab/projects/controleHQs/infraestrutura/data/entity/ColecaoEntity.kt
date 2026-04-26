package com.ale.devkit.lab.projects.controleHQs.infraestrutura.data.entity

import com.ale.devkit.lab.projects.controleHQs.dto.api.enums.Status
import com.ale.devkit.lab.projects.controleHQs.dto.api.enums.StatusIntegracao
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
data class ColecaoEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val isbn: String = "",
    val titulo: String = "",
    val categoria: String = "",
    val editora: String? = null,
    val autors: String? = null,
    val volume: Int? = null,
    val preco: Double? = null,
    @Enumerated(EnumType.STRING)
    val status: Status? = null,
    @Enumerated(EnumType.STRING)
    var statusIntegracao: StatusIntegracao? = null,
    val dataCadastro: LocalDate = LocalDate.now(),
    val dataPublicacao: LocalDate? = null,
    val numeroPaginas: Int? = null,
    val caixa: Int? = null

)