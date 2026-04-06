package com.ale.devkit.lab.projects.controleHQs.controller

import com.ale.devkit.lab.projects.controleHQs.controller.request.ColecaoRequest
import com.ale.devkit.lab.projects.controleHQs.service.PublicacaoService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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
                .body(mapOf("erro" to ex.message))
        }
    }
}