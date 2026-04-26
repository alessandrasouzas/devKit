package com.ale.devkit.lab.projects.controleHQs.message.consumer

import com.ale.devkit.lab.projects.controleHQs.dto.api.enums.StatusIntegracao
import com.ale.devkit.lab.projects.controleHQs.dto.message.ColecaoMessage
import com.ale.devkit.lab.projects.controleHQs.infraestrutura.data.repository.ColecaoRepository
import com.ale.devkit.lab.projects.controleHQs.infraestrutura.integration.OpenLibraryClient
import org.slf4j.LoggerFactory
import org.springframework.amqp.AmqpRejectAndDontRequeueException
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@Component
class PublicacaoConsumer(
    private val repository: ColecaoRepository,
    private val openLibraryClient: OpenLibraryClient
) {

    private val log = LoggerFactory.getLogger(PublicacaoConsumer::class.java)
    private val dlqFile = File("data/dlq_erros.txt")

    @RabbitListener(queues = ["colecao.enriquecimento"])
    fun consumirMensagemEnriquecerLivro(mensagem: ColecaoMessage) {

        log.info(
            "Recebido para enriquecimento: id='{}', titulo='{}'",
            mensagem.id,
            mensagem.titulo,
            mensagem.isbn
        )

        try {
            val dados = openLibraryClient.buscaPeloIsbn(mensagem.isbn)

            val entity = repository.findById(mensagem.id)
                .orElseThrow { RuntimeException("Publicacao não encontrada para id=${mensagem.id}") }

            if (dados == null) {
                log.warn("Nenhum dado encontrado para ISBN='{}'", mensagem.isbn)
                entity.statusIntegracao = StatusIntegracao.NAO_ENCONTRADO
                repository.save(entity)

                registrarNoDlqTxt(mensagem.isbn, "ISBN não encontrado na OpenLibrary")
                throw AmqpRejectAndDontRequeueException("ISBN não encontrado — enviando para DLQ")
            }

            // Só atualiza se tiver dado (não sobrescreve com null)
            val enriched = entity.copy(
                autors = dados?.autor ?: entity.autors,
                editora = dados?.editora ?: entity.editora,
                numeroPaginas = dados?.numeroPaginas ?: entity.numeroPaginas,
                dataPublicacao = dados?.dataPublicacao
                    ?.let { parseDate(it) }
                    ?: entity.dataPublicacao,
                statusIntegracao = StatusIntegracao.PROCESSANDO
            )

            if (enriched != entity) {
                entity.statusIntegracao = StatusIntegracao.ENRIQUECIDO
                repository.save(enriched)
                log.info("Publicacao enriquecida com sucesso: id='{}'", enriched.id)
            }

        } catch (ex: AmqpRejectAndDontRequeueException) {
            throw ex

        } catch (ex: Exception) {
            log.error("Erro ao processar enriquecimento: id='{}', erro='{}'", mensagem.id, ex.message, ex)
            registrarNoDlqTxt(mensagem.isbn, ex.message ?: "Erro desconhecido")
            throw AmqpRejectAndDontRequeueException("Erro inesperado — enviando para DLQ")

        }
    }

    private fun parseDate(date: String): LocalDate? {

        val formatters = listOf(
            DateTimeFormatter.ofPattern("MMM, yyyy", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("MMM, yyyy", Locale("pt", "BR"))
        )

        val parsed = formatters.asSequence()
            .mapNotNull {
                try {
                    YearMonth.parse(date.replace(".", ""), it)
                } catch (_: Exception) {
                    null
                }
            }
            .firstOrNull()

        return parsed?.atDay(1)
    }


    private fun registrarNoDlqTxt(isbn: String, motivo: String) {
        try {
            dlqFile.parentFile.mkdirs()
            dlqFile.appendText("${LocalDateTime.now()} | isbn=$isbn | motivo=$motivo\n")
            log.info("ISBN '{}' registrado no dlq_erros.txt", isbn)
        } catch (ex: Exception) {
            log.error("Falha ao escrever dlq_erros.txt para isbn='{}'", isbn, ex)
        }
    }

}