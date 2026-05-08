package com.ale.devkit.lab.projects.controleHQs.infraestrutura.data.repository

import com.ale.devkit.lab.projects.controleHQs.infraestrutura.data.entity.ColecaoEntity
import com.ale.devkit.lab.projects.controleHQs.infraestrutura.data.entity.ColecaoProjection
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ColecaoRepository : JpaRepository<ColecaoEntity, Long>{
    fun findByIsbn(isbn: String): ColecaoEntity?
    fun findAllProjectedBy(pageable: Pageable): Page<ColecaoProjection>
}