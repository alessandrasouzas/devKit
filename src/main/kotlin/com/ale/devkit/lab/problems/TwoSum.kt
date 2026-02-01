package com.ale.devkit.lab.problems

class TwoSum {
    /*
    Origem: LeetCode
    Descrição: Dado um array de inteiros nums e um número inteiro alvo‚retorno índices dos dois números tais que somam alvo.O.
    Você pode supor que cada entrada teria exatamente uma solução, e você não pode usar o mesmo elemento duas vezes.
    Pode retornar a resposta em qualquer ordem.
     */

    fun twoSum(nums: IntArray, target: Int): IntArray {
        val vistos = mutableMapOf<Int, Int>() // número -> índice

        for (i in nums.indices) {
            val complemento = target - nums[i] // o que falta para chegar no alvo

            // se já vimos o complemento antes, encontramos o par!
            if (vistos.containsKey(complemento)) {
                return intArrayOf(vistos[complemento]!!, i)
            }

            // guarda o número atual e seu índice
            vistos[nums[i]] = i
        }

        // se não encontrar, retorna vazio
        return intArrayOf()
    }
}

//fun main() {
//    val solution = TwoSum()
//    val nums = intArrayOf(2, 7, 11, 15)
//    val target = 11
//    val resultado = solution.twoSum(nums, target)
//    println(resultado.joinToString(", "))
//
//}