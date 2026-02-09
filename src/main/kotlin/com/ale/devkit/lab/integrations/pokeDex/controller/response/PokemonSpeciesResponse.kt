package com.ale.devkit.lab.integrations.pokeDex.controller.response

import com.fasterxml.jackson.annotation.JsonProperty

data class PokemonSpeciesResponse(

    @JsonProperty("name")
    val name: String,

    @JsonProperty("habitat")
    val habitat: Habitat?,

    @JsonProperty("is_legendary")
    val legendary: Boolean,

    @JsonProperty("is_mythical")
    val mythical: Boolean,

    @JsonProperty("genera")
    val genera: List<Genus>,

    @JsonProperty("evolution_chain")
    val evolutionChain: EvolutionChainUrl
)

data class EvolutionChainUrl(
    @JsonProperty("url")
    val url: String
)

data class Habitat(
    @JsonProperty("name")
    val name: String
)

data class Genus(
    @JsonProperty("genus")
    val genus: String,

    @JsonProperty("language")
    val language: Language
)

data class Language(
    @JsonProperty("name")
    val name: String
)
