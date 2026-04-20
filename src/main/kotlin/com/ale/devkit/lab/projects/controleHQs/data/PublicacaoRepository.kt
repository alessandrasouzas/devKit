package com.ale.devkit.lab.projects.controleHQs.data

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PublicacaoRepository : JpaRepository<PublicacaoEntity, Long>{
    fun findByIsbn(isbn: String): PublicacaoEntity?
}