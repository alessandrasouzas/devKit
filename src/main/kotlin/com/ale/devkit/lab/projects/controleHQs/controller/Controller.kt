package com.ale.devkit.lab.projects.controleHQs.controller

import com.ale.devkit.lab.projects.controleHQs.controller.request.ColecaoRequest
import com.ale.devkit.lab.projects.controleHQs.controller.request.ColecaoAtualizaRequest
import com.ale.devkit.lab.projects.controleHQs.controller.response.ColecaoResponse
import com.ale.devkit.lab.projects.controleHQs.dto.import.ImportacaoResult
import com.ale.devkit.lab.projects.controleHQs.service.PublicacaoService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/colecoes")
class Controller (
    private val service: PublicacaoService
){

    @PostMapping
    fun adicionaColecao(
        @RequestBody request: ColecaoRequest
    ): ResponseEntity<Any> {

        return try {
            val response = service.adicionaColecao(request)

            ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response)

        } catch (ex: Exception) {
            ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(mapOf("Erro ao add colecao!" to ex.message))
        }
    }

    @GetMapping("/{isbn}")
    fun buscarColeacao (@PathVariable isbn: String): ColecaoResponse {
       return service.buscarColecao(isbn)
    }

    @PatchMapping("/{isbn}")
    fun atualizarTitulo (@PathVariable isbn: String,
                         @RequestBody colecao: ColecaoAtualizaRequest): ColecaoResponse {
        return service.atualizarColecao(isbn, colecao)
    }

    @GetMapping("/exportar-csv")
    fun exportarCsv(): ResponseEntity<Any> {
        val csv = service.exportarCsv()

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body("Exportado com Sucesso!")
    }

    //import csv
    @PostMapping("/importar-csv", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun importarCsv(
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<ImportacaoResult> {
        val resultado = service.importarCsv(file)
        return ResponseEntity.ok(resultado)
    }

    //deletar by isbn

    //consultar todos

}