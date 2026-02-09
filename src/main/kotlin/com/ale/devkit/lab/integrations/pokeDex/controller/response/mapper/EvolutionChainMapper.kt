package com.ale.devkit.lab.integrations.pokeDex.controller.response.mapper

import com.ale.devkit.lab.integrations.pokeDex.controller.response.EvolutionChainResponse
import com.ale.devkit.lab.integrations.pokeDex.controller.response.EvolutionNode


fun EvolutionNode.flattenEvolutions(): List<String> {
    val evolucoes = mutableListOf<String>()

    evolvesTo.forEach { node ->
        evolucoes.add(node.species.name)
        evolucoes.addAll(node.flattenEvolutions())
    }

    return evolucoes
}

fun EvolutionChainResponse.toEvolutionsFrom(pokemonName: String): List<String> {
    val path = findPath(chain, pokemonName)
    return path
        ?.evolvesTo
        ?.map { it.species.name }
        .orEmpty()
}

private fun findPath(
    node: EvolutionNode,
    pokemonName: String
): EvolutionNode? {
    if (node.species.name == pokemonName) {
        return node
    }

    node.evolvesTo.forEach { child ->
        val found = findPath(child, pokemonName)
        if (found != null) return found
    }

    return null
}
