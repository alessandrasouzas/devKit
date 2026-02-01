package com.ale.devkit.lab.integrations.viaCep.client

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

@Configuration
class OkHttpClientConfig {

    /** Cria e configura o OkHttpClient que será reutilizado por toda a aplicação.
     *  IMPORTANTE:
     *  - OkHttpClient é thread-safe
     *  - Deve ser singleton (um único Bean)
     *  - Nunca deve ser criado com "new" dentro do client
     */
    @Bean
    fun okHttpClient(): OkHttpClient {

        /** Interceptor responsável por logar requisição e resposta HTTP.   */
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        /** Builder do OkHttpClient.
         * - deixa explícito o que está sendo configurado e facilita manutenção e leitura */
        return OkHttpClient.Builder()

            /** Tempo máximo para estabelecer a conexão.
             *  Evita threads travadas indefinidamente caso o servidor não responda. */
            .connectTimeout(5, TimeUnit.SECONDS)

            /** Tempo máximo aguardando a resposta do servidor após a conexão ter sido estabelecida. */
            .readTimeout(5, TimeUnit.SECONDS)

            /** Tempo máximo para enviar o request ao servidor.
             * Mais relevante para POST/PUT com payload grande, mas mantemos configurado por padrão. */
            .writeTimeout(5, TimeUnit.SECONDS)

            /** Interceptors: conseguem interceptar request antes de ser enviada / response antes de ser retornada */
            .addInterceptor(loggingInterceptor)
            .build()
    }
}
