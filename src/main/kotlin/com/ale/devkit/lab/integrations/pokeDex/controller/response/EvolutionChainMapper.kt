package com.ale.devkit.lab.integrations.pokeDex.controller.response

fun EvolutionChainResponse.toEvolutions(): List<String> {
    return chain.flattenEvolutions()
}

fun EvolutionChainResponse.toEvolutionsFrom(speciesName: String): List<String> {
    val node = chain.findNode(speciesName)
    return node?.collectNextEvolutions() ?: emptyList()
}


private fun EvolutionNode.findNode(name: String): EvolutionNode? {
    if (species.name.equals(name, ignoreCase = true)) {
        return this
    }
    evolvesTo.forEach {
        val found = it.findNode(name)
        if (found != null) return found
    }
    return null
}


private fun EvolutionNode.collectNextEvolutions(): List<String> {
    val result = mutableListOf<String>()
    evolvesTo.forEach {
        result.add(it.species.name)
        result.addAll(it.collectNextEvolutions())
    }
    return result
}

fun EvolutionNode.flattenEvolutions(): List<String> {
    val evolucoes = mutableListOf<String>()

    evolvesTo.forEach { node ->
        evolucoes.add(node.species.name)
        evolucoes.addAll(node.flattenEvolutions())
    }

    return evolucoes
}
