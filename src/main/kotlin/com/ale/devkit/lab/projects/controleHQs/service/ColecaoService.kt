package com.ale.devkit.lab.projects.controleHQs.service

import com.ale.devkit.lab.projects.controleHQs.controller.request.ColecaoRequest
import com.ale.devkit.lab.projects.controleHQs.controller.response.ColecaoResponse
import com.ale.devkit.lab.projects.controleHQs.data.PublicacaoEntity
import com.ale.devkit.lab.projects.controleHQs.data.PublicacaoRepository
import com.ale.devkit.lab.projects.controleHQs.dto.ColecaoMessage
import com.ale.devkit.lab.projects.controleHQs.message.producer.PublicacaoProducer
import com.opencsv.CSVWriter
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronizationManager
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class PublicacaoService(
    private val repository: PublicacaoRepository,
    private val producer: PublicacaoProducer
) {

    private val log = LoggerFactory.getLogger(PublicacaoService::class.java)

    @Transactional
    fun adicionaColecao(request: ColecaoRequest ): PublicacaoEntity {

        log.info("Iniciando cadastro de Publicacao: titulo='{}'", request.titulo)


        try {

            // Idempotência: verifica ISBN duplicado
            if (request.isbn != null) {
                val existente = repository.findByIsbn(request.isbn)
                if (existente != null) {
                    log.warn("Publicacao já cadastrada com ISBN '{}': id='{}'", request.isbn, existente.id)
                    return existente
                }
            }

            val entity = PublicacaoEntity(
                titulo = request.titulo,
                categoria = request.categoria,
                editora = request.editora,
                volume = request.volume,
                preco = request.preco,
                condicao = null,
                status = request.status,
                isbn = request.isbn,
                dataCadastro = LocalDate.now(),
                id = null,
                caixa = request.caixa,
                numeroPaginas = null,
                autors = null,
                dataPublicacao = null
            )

            val saved = repository.save(entity)

            log.info("Publicacao salva com sucesso: id='{}', titulo='{}'", saved.id, saved.titulo)

            val message = ColecaoMessage(
                id = saved.id!!,
                titulo = saved.titulo
            )

            TransactionSynchronizationManager.registerSynchronization(
                object : TransactionSynchronization {
                    override fun afterCommit() {
                        producer.enviarMsgParaFila(message)
                    }
                }
            )

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

    fun exportarCsv() {
        val caminho = "data/livros.csv"  // relativo à raiz do projeto
        val livros = repository.findAll()

        val file = File(caminho)
        val writer = CSVWriter(
            OutputStreamWriter(
                FileOutputStream(file),
                StandardCharsets.UTF_8
            ),
            ';',
            CSVWriter.DEFAULT_QUOTE_CHARACTER,
            CSVWriter.DEFAULT_ESCAPE_CHARACTER,
            CSVWriter.DEFAULT_LINE_END
        )

        writer.writeNext(arrayOf(
            "ID", "Título", "Categoria", "Editora", "Volume",
            "Preço", "Condição", "Status", "ISBN", "Data Cadastro", "Caixa"
        ))

        livros.forEach { livro ->
            writer.writeNext(arrayOf(
                livro.id?.toString() ?: "",
                livro.titulo,
                livro.categoria,
                livro.editora ?: "",
                livro.volume?.toString() ?: "",
                livro.preco?.let { "%.2f".format(it).replace('.', ',') } ?: "",
                livro.condicao ?: "",
                livro.status?.name ?: "",
                livro.isbn ?: "",
                livro.dataCadastro.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                livro.caixa?.toString() ?: ""
            ))
        }

        writer.flush()
        writer.close()
    }

    fun buscarColecao(isbn: String): ColecaoResponse {

        val entity = repository.findByIsbn(isbn)
            ?: throw RuntimeException("Publicacao não encontrada para isbn=$isbn")

        return ColecaoResponse(
            id = entity.id!!,
            isbn = entity.isbn,
            titulo = entity.titulo,
            categoria = entity.categoria,
            editora = entity.editora,
            autors = entity.autors,
            volume = entity.volume,
            preco = entity.preco,
            condicao = entity.condicao,
            status = entity.status,
            dataCadastro = entity.dataCadastro,
            dataPublicacao = entity.dataPublicacao,
            numeroPaginas = entity.numeroPaginas,
            caixa = entity.caixa
        )
    }
}