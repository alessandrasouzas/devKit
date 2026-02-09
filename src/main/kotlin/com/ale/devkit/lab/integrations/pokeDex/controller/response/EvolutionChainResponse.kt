package com.ale.devkit.lab.integrations.pokeDex.controller.response

import com.fasterxml.jackson.annotation.JsonProperty

data class EvolutionChainResponse(
    @JsonProperty("chain")
    val chain: EvolutionNode
)

data class EvolutionNode(
    @JsonProperty("species")
    val species: Species,

    @JsonProperty("evolves_to")
    val evolvesTo: List<EvolutionNode> = emptyList()
)

data class Species(
    @JsonProperty("name")
    val name: String
)

data class PokemonSpeciesResponse(
    @JsonProperty("evolution_chain")
    val evolutionChain: EvolutionChainUrl
)

data class EvolutionChainUrl(
    val url: String
)