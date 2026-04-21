package com.ale.devkit.lab.projects.controleHQs.exception

// exception/ColecaoNotFoundException.kt
class ColecaoNotFoundException(isbn: String) :
    RuntimeException("Colecao não encontrada para isbn=$isbn")