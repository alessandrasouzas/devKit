package com.ale.devkit.lab.projects.controleHQs.infraestrutura.integration

import com.ale.devkit.lab.projects.controleHQs.dto.api.ColecaoDetalhes
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class OpenLibraryClient(
    private val objectMapper: ObjectMapper
) {

    private val client = RestClient.create("https://openlibrary.org")

    fun buscaPeloIsbn(isbn: String): ColecaoDetalhes? {
        val cleanIsbn = isbn.replace("-", "")

        val json = client.get()
            .uri("/api/books?bibkeys=ISBN:$cleanIsbn&format=json&jscmd=data")
            .retrieve()
            .body(String::class.java)

        val map: Map<String, Any> = objectMapper.readValue(json, Map::class.java) as Map<String, Any>

        val book = map["ISBN:$cleanIsbn"] as? Map<String, Any> ?: return null

        val autor = (book["authors"] as? List<Map<String, Any>>)
            ?.firstOrNull()
            ?.get("name") as? String

        val numeroPaginas = (book["number_of_pages"] as? Number)?.toInt()

        val editora = (book["publishers"] as? List<Map<String, Any>>)
            ?.firstOrNull()
            ?.get("name") as? String

        val dataPublicacao = book["publish_date"] as? String

        return ColecaoDetalhes(
            autor = autor,
            numeroPaginas = numeroPaginas,
            editora = editora,
            dataPublicacao = dataPublicacao
        )
    }
}