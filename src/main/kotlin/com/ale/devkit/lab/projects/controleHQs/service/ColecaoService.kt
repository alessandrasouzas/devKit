package com.ale.devkit.lab.projects.controleHQs.service

import com.ale.devkit.lab.projects.controleHQs.controller.request.ColecaoRequest
import com.ale.devkit.lab.projects.controleHQs.data.PublicacaoEntity
import com.ale.devkit.lab.projects.controleHQs.data.PublicacaoRepository
import com.ale.devkit.lab.projects.controleHQs.dto.ColecaoMessage
import com.ale.devkit.lab.projects.controleHQs.messaging.producer.PublicacaoProducer
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class PublicacaoService(
    private val repository: PublicacaoRepository,
    private val producer: PublicacaoProducer
) {

    private val log = LoggerFactory.getLogger(PublicacaoService::class.java)

    fun adicionaColecao(request: ColecaoRequest): PublicacaoEntity {

        log.info("Iniciando cadastro de Publicacao: titulo='{}'", request.titulo)

        try {
            val entity = PublicacaoEntity(
                titulo = request.titulo,
                categoria = request.categoria,
                editora = request.editora,
                volume = request.volume,
                preco = request.preco,
                condicao = null,
                status = "colecao",
                isbn = null,
                dataCadastro = LocalDateTime.now()
            )

            val saved = repository.save(entity)

            log.info("Publicacao salva com sucesso: id='{}', titulo='{}'", saved.id, saved.titulo)

            val message = ColecaoMessage(
                id = entity.id!!,
                titulo = entity.titulo,
                categoria = entity.categoria,
                editora = entity.editora,
                volume = entity.volume,
                preco = entity.preco
            )

            producer.enviarMsgParaFila(message)

            return saved

        } catch (ex: Exception) {
            log.error(
                "Erro ao salvar Publicacao: titulo='{}', erro='{}'",
                request.titulo,
                ex.message,
                ex
            )

            throw RuntimeException("Erro ao cadastrar Publicacao")
        }
    }
}