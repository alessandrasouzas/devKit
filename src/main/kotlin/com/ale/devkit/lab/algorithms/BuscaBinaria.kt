package com.ale.devkit.lab.algorithms

class BuscaBinaria {

    // solução simples
    fun buscaBinaria(lista: List<Int>, alvo: Int): Int? {

        var inicio = 0
        var fim = lista.lastIndex
        var meio: Int

        while(inicio <= fim) {
            meio = (inicio + fim) / 2

            if (lista[meio] == alvo) {
                println("Achei o alvo $alvo no índice $meio usando buscaBinaria")
                return meio
            }

            if (lista[meio] < alvo)
                inicio = meio + 1
            else
                fim = meio - 1
        }
        return null
    }

    //soluçao com recursiva
    fun buscaBinariaRecursiva(lista: List<Int>, inicio: Int, fim: Int, alvo: Int): Int {
        if (inicio > fim)
            return -1

        val meio: Int =  (inicio + fim) / 2

        if (lista[meio] == alvo) {
            println("Achei o alvo $alvo no índice $meio usando buscaBinariaRecursiva")
            return meio
        }

        return if (lista[meio] < alvo)
                buscaBinariaRecursiva(lista, meio + 1, fim, alvo)
               else buscaBinariaRecursiva(lista, inicio, meio-1, alvo)
    }

    //solucao com Função utilitária (Standard Library)
    fun buscaUtilitaria(lista: List<Int>, alvo: Int){
        val resultado = lista.binarySearch(alvo)

        if (resultado < 0) {
            val insertionPoint = -(resultado + 1)
            println("Não encontrado, deveria estar no índice $insertionPoint")
        } else
            println("Achei o alvo $alvo no índice $resultado usando buscaUtilitaria")
    }
}

//fun main(){
//    val alvo = 9;
//    var lista = listOf(1,3,5,7,9,11,13)
//    val inicio = 0
//    val fim = lista.lastIndex
//
//    BuscaBinaria().buscaBinaria(lista, alvo)
//    BuscaBinaria().buscaBinariaRecursiva(lista,inicio,fim, alvo)
//    BuscaBinaria().buscaUtilitaria(lista, alvo)
//}