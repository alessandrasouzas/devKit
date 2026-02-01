package com.ale.devkit.lab.integrations.viaCep.client

import com.ale.devkit.lab.integrations.viaCep.controller.response.ViaCepResponse
import com.ale.devkit.lab.integrations.viaCep.exception.ViaCepException
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class ViaCepClient(
    private val okHttpClient: OkHttpClient,
    private val objectMapper: ObjectMapper,

    @Value("\${integration.viacep.url}")
    private val viaCepBaseUrl: String
) {

    fun buscarEnderecoPorCep(cep: String): ViaCepResponse {

        val url = "$viaCepBaseUrl/$cep/json/"

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        try {
            okHttpClient.newCall(request).execute().use { response ->

                if (!response.isSuccessful) {
                    throw ViaCepException(
                        "Erro ao chamar ViaCEP. HTTP status: ${response.code}"
                    )
                }

                val responseBody = response.body?.string()
                    ?: throw ViaCepException(
                        "Resposta do ViaCEP sem corpo"
                    )

                val viaCepResponse =
                    objectMapper.readValue(responseBody, ViaCepResponse::class.java)

                return viaCepResponse
            }

        } catch (ex: Exception) {
            throw ViaCepException(
                "Falha ao integrar com ViaCEP",
                ex
            )
        }
    }

}
