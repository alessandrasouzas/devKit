package com.ale.devkit.lab.integrations.pokeDex.controller

import com.ale.devkit.lab.integrations.pokeDex.controller.dto.PokemonMetadata
import com.ale.devkit.lab.integrations.pokeDex.controller.dto.PokemonResponse
import com.ale.devkit.lab.integrations.pokeDex.controller.request.CreatePokemonRequest
import com.ale.devkit.lab.integrations.pokeDex.useCase.PokeDexService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("pokemons")
class PokedexController (
    private val service: PokeDexService
){

    @GetMapping()
    fun buscarPokemon(@RequestHeader pokemon: String): ResponseEntity<PokemonResponse>?{
        val response = service.getPokemon(pokemon)

        return response?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()
    }

    @PostMapping("/create")
    fun createPokemon(
        @RequestBody request: CreatePokemonRequest,
        @RequestHeader("trainer-id") trainerId: String,
        @RequestHeader("correlation-id", required = false) correlationId: String?,
        @RequestHeader("source", required = false) source: String?
    ): ResponseEntity<String> {

        val metadata = PokemonMetadata(
            trainerId = trainerId,
            correlationId = correlationId,
            source = source
        )

        println("Metadata: $metadata")
        println("Payload: $request")

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body("Pokémon criado com sucesso")
    }
}