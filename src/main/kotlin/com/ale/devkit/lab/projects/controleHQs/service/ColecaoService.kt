package com.ale.devkit.lab.projects.controleHQs.service

import com.ale.devkit.lab.projects.controleHQs.controller.request.ColecaoAtualizaRequest
import com.ale.devkit.lab.projects.controleHQs.controller.request.ColecaoRequest
import com.ale.devkit.lab.projects.controleHQs.controller.response.ColecaoResponse
import com.ale.devkit.lab.projects.controleHQs.dto.api.enums.RegistroResultado
import com.ale.devkit.lab.projects.controleHQs.dto.api.enums.StatusIntegracao
import com.ale.devkit.lab.projects.controleHQs.dto.import.ImportacaoResult
import com.ale.devkit.lab.projects.controleHQs.dto.message.ColecaoMessage
import com.ale.devkit.lab.projects.controleHQs.infraestrutura.data.entity.ColecaoEntity
import com.ale.devkit.lab.projects.controleHQs.infraestrutura.data.entity.ColecaoProjection
import com.ale.devkit.lab.projects.controleHQs.infraestrutura.data.repository.ColecaoRepository
import com.ale.devkit.lab.projects.controleHQs.message.producer.ColecaoProducer
import com.opencsv.CSVWriter
import org.springframework.transaction.annotation.Transactional
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronizationManager
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class PublicacaoService(
    private val repository: ColecaoRepository,
    private val producer: ColecaoProducer,
    private val importService: ColecaoImportarService
) {

    private val log = LoggerFactory.getLogger(PublicacaoService::class.java)

    @Transactional
    fun adicionaColecao(request: ColecaoRequest ): ColecaoEntity {
        log.info("Iniciando cadastro de Colecao: titulo='{}'", request.titulo)

        try {
            // Idempotência: verifica ISBN duplicado
            repository.findByIsbn(request.isbn!!)?.let { existente ->
                log.warn("Colecao já cadastrada com ISBN '{}': id='{}'", request.isbn, existente.id)
                throw Exception("Colecao já cadastrada com isbn= ${request.isbn}")
            }

            val entity = ColecaoEntity(
                titulo = request.titulo,
                categoria = request.categoria,
                editora = request.editora,
                volume = request.volume,
                preco = request.preco,
                status = request.status,
                statusIntegracao = StatusIntegracao.PENDENTE,
                isbn = request.isbn,
                dataCadastro = LocalDate.now(),
                id = null,
                caixa = request.caixa,
                numeroPaginas = null,
                autors = request.autor,
                dataPublicacao = null
            )

            val saved = repository.save(entity)

            log.info("Colecao salva com sucesso: id='{}', titulo='{}'", saved.id, saved.titulo)

            val message = ColecaoMessage(
                id = saved.id!!,
                titulo = saved.titulo,
                isbn = saved.isbn
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
        log.info("Iniciando exportacao csv da base de Colecao")

        val caminho = "data/livros.csv"
        val publicacoes = repository.findAll()

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
            "ID", "ISBN", "Título", "Categoria", "Autor", "Editora", "Volume",
            "Núm. Páginas", "Caixa", "Preço", "Status", "Status Integração",
            "Data Cadastro", "Data Publicação","Emprestado Para"
        ))

        publicacoes.forEach { publicacao ->
            writer.writeNext(arrayOf(
                publicacao.id?.toString() ?: "",                                                          // 0
                publicacao.isbn,                                                                          // 1
                publicacao.titulo,                                                                        // 2
                publicacao.categoria,                                                                     // 3
                publicacao.autors ?: "",                                                                  // 4
                publicacao.editora ?: "",                                                                 // 5
                publicacao.volume?.toString() ?: "",                                                      // 6
                publicacao.numeroPaginas?.toString() ?: "",                                               // 7
                publicacao.caixa?.toString() ?: "",                                                       // 8
                publicacao.preco?.let { "%.2f".format(it).replace('.', ',') } ?: "",                      // 9
                publicacao.status?.name ?: "",                                                            // 10
                publicacao.statusIntegracao?.name ?: "",                                                  // 11
                publicacao.dataCadastro.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),                // 12
                publicacao.dataPublicacao?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: "",       // 13
                publicacao.emprestadoPara?.toString() ?: ""                                               // 14
            ))
        }

        writer.flush()
        writer.close()

        log.info("Exportado com sucesso!")
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
            caixa = entity.caixa,
            emprestadoPara = entity.emprestadoPara
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
            status = colecao.status ?: entity.status,
            emprestadoPara = colecao.emprestadoPara ?: entity.emprestadoPara
        )

        val salvo = repository.save(atualizado)
        return ColecaoResponse.from(salvo)
    }

    fun importarCsv(file: MultipartFile): ImportacaoResult {
        log.info("Iniciando exportacao csv da base de Colecao")

        var total = 0
        var inseridos = 0
        var ignorados = 0
        var atualizados = 0
        val erros = mutableListOf<String>()

        file.inputStream
            .bufferedReader()
            .lineSequence()
            .drop(1)
            .filter { it.isNotBlank() }
            .forEach { linha ->
                total++
                try {
                    importService.salvarRegistro(linha).also { resultado ->
                        when (resultado) {
                            RegistroResultado.INSERIDO -> inseridos++
                            RegistroResultado.IGNORADO -> ignorados++
                            RegistroResultado.ATUALIZADO -> atualizados++
                        }
                    }
                } catch (ex: Exception) {
                    log.error("Erro ao processar linha {}: '{}'", total, linha, ex)
                    erros.add("Linha $total: ${ex.message}")
                }
            }

        log.info("Import finalizado: total={}, inseridos={}, atualizados={}, ignorados={}, erros={}", total, inseridos, atualizados, ignorados, erros.size)

        return ImportacaoResult(total, inseridos, atualizados, ignorados, erros)
    }


    @Transactional(readOnly = true)
    fun listarColecao(pageable: Pageable): Page<ColecaoProjection> {
        return repository.findAllProjectedBy(pageable)
    }
}