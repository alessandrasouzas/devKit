package com.ale.devkit.lab.cases

class BigOAnotation {

    fun temElementoEmComum(a: List<Int>, b: List<Int>): Boolean {
        val setB = b.toHashSet()
        return a.any { it in setB }
    }
}

//fun main() {
//    val a = listOf(1, 2, 5, 4, 3)
//    val b = listOf(7, 2, 6, 1, 8)
//
//    val resultado = BigOAnotation().temElementoEmComum(a, b)
//    println("resultado: $resultado")
//}
