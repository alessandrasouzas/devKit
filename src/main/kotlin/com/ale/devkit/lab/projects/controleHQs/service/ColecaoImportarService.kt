package com.ale.devkit.lab.projects.controleHQs.service

import com.ale.devkit.lab.projects.controleHQs.dto.api.enums.RegistroResultado
import com.ale.devkit.lab.projects.controleHQs.dto.api.enums.Status
import com.ale.devkit.lab.projects.controleHQs.dto.api.enums.StatusIntegracao
import com.ale.devkit.lab.projects.controleHQs.dto.message.ColecaoMessage
import com.ale.devkit.lab.projects.controleHQs.infraestrutura.data.entity.ColecaoEntity
import com.ale.devkit.lab.projects.controleHQs.infraestrutura.data.repository.ColecaoRepository
import com.ale.devkit.lab.projects.controleHQs.message.producer.ColecaoProducer
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronizationManager
import java.time.LocalDate

@Service
class ColecaoImportarService (

    private val repository: ColecaoRepository,
    private val producer: ColecaoProducer
) {

    private val log = LoggerFactory.getLogger(ColecaoImportarService::class.java)

    @Transactional
    fun salvarRegistro(linha: String): RegistroResultado {
        val campos = linha.split(";")

        if (campos.size < 4) throw IllegalArgumentException("Linha com campos insuficientes")

        val isbn = campos[1].trim().removeSurrounding("\"")

        val existente = repository.findByIsbn(isbn)
        if (existente != null) {
            val atualizado = existente.copy(
                titulo = campos[2].trim().removeSurrounding("\"").ifBlank { existente.titulo },
                categoria = campos[3].trim().removeSurrounding("\"").ifBlank { existente.categoria },
                autors = campos.getOrNull(4)?.trimOrNull() ?: existente.autors,
                editora = campos.getOrNull(5)?.trimOrNull() ?: existente.editora,
                volume = campos.getOrNull(6)?.trimOrNull()?.toIntOrNull() ?: existente.volume,
                numeroPaginas = campos.getOrNull(7)?.trimOrNull()?.toIntOrNull() ?: existente.numeroPaginas,
                caixa = campos.getOrNull(8)?.trimOrNull()?.toIntOrNull() ?: existente.caixa,
                preco = campos.getOrNull(9)?.trimOrNull()?.replace(',', '.')?.toDoubleOrNull() ?: existente.preco,
                status = campos.getOrNull(10)?.trimOrNull()
                    ?.let { runCatching { Status.valueOf(it) }.getOrNull() } ?: existente.status,
                emprestadoPara = campos.getOrNull(14)?.trimOrNull() ?: existente.emprestadoPara
            )

            if (atualizado != existente) {
                repository.save(atualizado)
                log.info("ISBN '{}' atualizado via import", isbn)
                return RegistroResultado.ATUALIZADO
            }

            log.info("ISBN '{}' já existe e sem alterações, pulando...", isbn)
            return RegistroResultado.IGNORADO
        }

        val entity = ColecaoEntity(
            isbn = isbn,
            titulo = campos[2].trim().removeSurrounding("\""),
            categoria = campos[3].trim().removeSurrounding("\""),
            autors = campos.getOrNull(4)?.trimOrNull(),
            editora = campos.getOrNull(5)?.trimOrNull(),
            volume = campos.getOrNull(6)?.trimOrNull()?.toIntOrNull(),
            numeroPaginas = campos.getOrNull(7)?.trimOrNull()?.toIntOrNull(),
            caixa = campos.getOrNull(8)?.trimOrNull()?.toIntOrNull(),
            preco = campos.getOrNull(9)?.trimOrNull()?.replace(',', '.')?.toDoubleOrNull(),
            status = campos.getOrNull(10)?.trimOrNull()
                ?.let { runCatching { Status.valueOf(it) }.getOrNull() },
            statusIntegracao = campos.getOrNull(11)?.trimOrNull()
                ?.let { runCatching { StatusIntegracao.valueOf(it) }.getOrNull() },
            dataCadastro = LocalDate.now(),
            dataPublicacao = null,
            id = null,
            emprestadoPara = campos.getOrNull(15)?.trimOrNull()?.toString()
        )

        val saved = repository.save(entity)
        log.info("ISBN '{}' inserido com sucesso", isbn)

        TransactionSynchronizationManager.registerSynchronization(
            object : TransactionSynchronization {
                override fun afterCommit() {
                    producer.enviarMsgParaFila(
                        ColecaoMessage(id = saved.id!!, titulo = saved.titulo, isbn = saved.isbn)
                    )
                }
            }
        )

        return RegistroResultado.INSERIDO
    }

    // Extension para limpar string ou retornar null
    fun String.trimOrNull(): String? = trim().removeSurrounding("\"").ifBlank { null }

}