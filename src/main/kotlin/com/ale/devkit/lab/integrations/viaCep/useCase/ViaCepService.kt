package com.ale.devkit.lab.integrations.viaCep.useCase

import com.ale.devkit.lab.integrations.viaCep.client.ViaCepClient
import com.ale.devkit.lab.integrations.viaCep.controller.response.ViaCepResponse
import org.springframework.stereotype.Service

@Service
class ViaCepService (
    private val client: ViaCepClient
) {

    fun getCep(cep: String): ViaCepResponse? {
        return client.buscarEnderecoPorCep(cep)
    }
}