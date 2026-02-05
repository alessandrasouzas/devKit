package com.ale.devkit.lab.integrations.pokeDex.controller

import com.ale.devkit.lab.integrations.pokeDex.controller.response.PokemonResponse
import com.ale.devkit.lab.integrations.pokeDex.useCase.PokeDexService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController

@RestController
class PokedexController (
    private val service: PokeDexService
){

    @GetMapping("/pokemons")
    fun buscarPokemon(@RequestHeader pokemon: String): ResponseEntity<PokemonResponse>?{
        val response = service.getPokemon(pokemon)

        return response?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()
    }
}