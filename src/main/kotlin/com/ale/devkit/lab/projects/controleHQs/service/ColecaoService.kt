package com.ale.devkit.lab.projects.controleHQs.service

import com.ale.devkit.lab.projects.controleHQs.controller.request.ColecaoAtualizaRequest
import com.ale.devkit.lab.projects.controleHQs.controller.request.ColecaoRequest
import com.ale.devkit.lab.projects.controleHQs.controller.response.ColecaoResponse
import com.ale.devkit.lab.projects.controleHQs.dto.message.ColecaoMessage
import com.ale.devkit.lab.projects.controleHQs.infraestrutura.data.entity.ColecaoEntity
import com.ale.devkit.lab.projects.controleHQs.infraestrutura.data.repository.ColecaoRepository
import com.ale.devkit.lab.projects.controleHQs.message.producer.ColecaoProducer
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
    private val repository: ColecaoRepository,
    private val producer: ColecaoProducer
) {

    private val log = LoggerFactory.getLogger(PublicacaoService::class.java)

    @Transactional
    fun adicionaColecao(request: ColecaoRequest ): ColecaoEntity {
        log.info("Iniciando cadastro de Colecao: titulo='{}'", request.titulo)

        try {
            // Idempotência: verifica ISBN duplicado
            repository.findByIsbn(request.isbn)?.let { existente ->
                log.warn("Colecao já cadastrada com ISBN '{}': id='{}'", request.isbn, existente.id)
                throw Exception("Colecao já cadastrada com isbn=${request.isbn}")
            }

            val entity = ColecaoEntity(
                titulo = request.titulo,
                categoria = request.categoria,
                editora = request.editora,
                volume = request.volume,
                preco = request.preco,
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

            log.info("Colecao salva com sucesso: id='{}', titulo='{}'", saved.id, saved.titulo)

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
                "Erro ao salvar Colecao: titulo='{}', erro='{}'",
                request.titulo,
                ex.message,
                ex
            )

            throw RuntimeException("Erro ao cadastrar Colecao")
        }
    }

    fun exportarCsv() {
        val caminho = "data/livros.csv"
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
            "Preço", "Status", "ISBN", "Data Cadastro", "Caixa"
        ))

        livros.forEach { livro ->
            writer.writeNext(arrayOf(
                livro.id?.toString() ?: "",
                livro.titulo,
                livro.categoria,
                livro.editora ?: "",
                livro.volume?.toString() ?: "",
                livro.preco?.let { "%.2f".format(it).replace('.', ',') } ?: "",
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
            ?: throw RuntimeException("Colecao não encontrada para isbn=$isbn")

        return ColecaoResponse(
            id = entity.id!!,
            isbn = entity.isbn,
            titulo = entity.titulo,
            categoria = entity.categoria,
            editora = entity.editora,
            autors = entity.autors,
            volume = entity.volume,
            preco = entity.preco,
            status = entity.status,
            dataCadastro = entity.dataCadastro,
            dataPublicacao = entity.dataPublicacao,
            numeroPaginas = entity.numeroPaginas,
            caixa = entity.caixa
        )
    }

    @Transactional // Garante que o find e o save acontecem na mesma transação
    fun atualizarColecao(isbn: String, colecao: ColecaoAtualizaRequest): ColecaoResponse{
        val entity = repository.findByIsbn(isbn)
            ?: throw RuntimeException("Colecao não encontrada para o isbn=$isbn")

        val atualizado = entity.copy(
            titulo = colecao.titulo ?: entity.titulo,
            categoria = colecao.categoria ?: entity.categoria,
            editora = colecao.editora ?: entity.editora,
            volume = colecao.volume ?: entity.volume,
            preco = colecao.preco ?: entity.preco,
            dataPublicacao = colecao.dataPublicacao ?: entity.dataPublicacao,
            numeroPaginas = colecao.numeroPaginas ?: entity.numeroPaginas,
            caixa = colecao.caixa ?: entity.caixa,
            status = colecao.status ?: entity.status
        )

        val salvo = repository.save(atualizado)
        return ColecaoResponse.from(salvo)
    }

}