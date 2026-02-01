package com.ale.devkit.lab.integrations.viaCep.controller

import com.ale.devkit.lab.integrations.viaCep.controller.response.ViaCepResponse
import com.ale.devkit.lab.integrations.viaCep.useCase.ViaCepService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController

@RestController
class ViaCepController (
    private val service: ViaCepService
) {

    @GetMapping("/ceps")
    fun buscarCep(@RequestHeader cep: String): ResponseEntity<ViaCepResponse>? {
        val response = service.getCep(cep)

        return response?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()
    }
}