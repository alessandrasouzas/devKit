package com.ale.devkit.lab.projects.controleHQs.controller.response

import com.ale.devkit.lab.projects.controleHQs.dto.api.enums.Status
import com.ale.devkit.lab.projects.controleHQs.infraestrutura.data.entity.ColecaoEntity
import java.time.LocalDate

data class ColecaoResponse(
    val id: Long?,
    val titulo: String,
    val isbn: String,
    val categoria: String,
    val editora: String?,
    val autors: String?,  // adiciona
    val volume: Int?,
    val preco: Double?,
    val status: Status?,
    val dataCadastro: LocalDate,
    val dataPublicacao: LocalDate?,
    val numeroPaginas: Int?,
    val caixa: Int?,
    val emprestadoPara: String?
) {
    companion object {
        fun from(entity: ColecaoEntity) = ColecaoResponse(
            id = entity.id,
            titulo = entity.titulo,
            isbn = entity.isbn,
            categoria = entity.categoria,
            editora = entity.editora,
            autors = entity.autors,
            volume = entity.volume,
            preco = entity.preco,
            status = entity.status,
            dataCadastro = entity.dataCadastro,
            dataPublicacao = entity.dataPublicacao,
            numeroPaginas = entity.numeroPaginas,
            caixa = entity.caixa,
            emprestadoPara = entity.emprestadoPara
        )
    }
}