package com.ale.devkit.lab.projects.controleHQs.messaging.consumer

import com.ale.devkit.lab.projects.controleHQs.data.PublicacaoRepository
import com.ale.devkit.lab.projects.controleHQs.dto.ColecaoMessage
import com.ale.devkit.lab.projects.controleHQs.integrations.GoogleBooksClient
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class PublicacaoConsumer(
    private val repository: PublicacaoRepository,
    private val googleBooksClient: GoogleBooksClient
) {

    private val log = LoggerFactory.getLogger(PublicacaoConsumer::class.java)

    @RabbitListener(queues = ["colecao.enriquecimento"])
    fun consumir(mensagem: ColecaoMessage) {

        log.info(
            "Recebido para enriquecimento: id='{}', titulo='{}'",
            mensagem.id,
            mensagem.titulo
        )

        try {
            val entity = repository.findById(mensagem.id)
                .orElseThrow { RuntimeException("Publicacao não encontrada para id=${mensagem.id}") }

            val novoIsbn = googleBooksClient.buscarPorTitulo(entity.titulo)
            log.info("ISBN encontrado: {}", novoIsbn)

            val enriched = entity.copy(
                isbn = entity.isbn ?: novoIsbn
                //isbn akira: 9788545702870
            )

            repository.save(enriched)

            log.info(
                "Publicacao enriquecida com sucesso: id='{}', isbn='{}'",
                enriched.id,
                enriched.isbn
            )

        } catch (ex: Exception) {
            log.error(
                "Erro ao processar enriquecimento: id='{}', erro='{}'",
                mensagem.id,
                ex.message,
                ex
            )

            throw ex
        }
    }
}