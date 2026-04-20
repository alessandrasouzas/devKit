package com.ale.devkit.lab.projects.controleHQs.message.consumer

import com.ale.devkit.lab.projects.controleHQs.data.PublicacaoRepository
import com.ale.devkit.lab.projects.controleHQs.dto.ColecaoMessage

import com.ale.devkit.lab.projects.controleHQs.integrations.OpenLibraryClient
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@Component
class PublicacaoConsumer(
    private val repository: PublicacaoRepository,
    private val openLibraryClient: OpenLibraryClient
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

            val dados = openLibraryClient.buscaPeloIsbn(entity.isbn)

            // Só atualiza se tiver dado (não sobrescreve com null)
            val enriched = entity.copy(
                autors = dados?.autor ?: entity.autors,
                editora = dados?.editora ?: entity.editora,
                numeroPaginas = dados?.numeroPaginas ?: entity.numeroPaginas,
                dataPublicacao = dados?.dataPublicacao
                    ?.let { parseDate(it) }
                    ?: entity.dataPublicacao
            )

            repository.save(enriched)

            log.info(
                "Publicacao enriquecida com sucesso: id='{}'",
                enriched.id
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

    fun parseDate(date: String): LocalDate? {

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

        return parsed?.atDay(1) // 🔥 sem hora
    }

}