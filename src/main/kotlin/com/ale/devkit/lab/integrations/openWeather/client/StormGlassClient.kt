package com.ale.devkit.lab.integrations.openWeather.client

import com.ale.devkit.lab.integrations.openWeather.controller.request.ParamsRequest
import com.ale.devkit.lab.integrations.openWeather.controller.response.StormGlassResponse
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.Request
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("dev")
class StormGlassClient(

    private val okHttpClient: OkHttpClient,
    private val objectMapper: ObjectMapper,

    @Value("\${integration.stormGlass.url}")
    private val stormGlassUrl: String,

    @Value("\${integration.stormGlass.apikey}")
    private val apiKey: String

) : WeatherClient {

    override fun getWeather(params: ParamsRequest): StormGlassResponse? {

        val client = OkHttpClient()
        val url = HttpUrl.Builder()
            .scheme("https")
            .host("$stormGlassUrl")
            .addPathSegments("v2/weather/point")
            .addQueryParameter("lat", params.lat)
            .addQueryParameter("lng", params.lng)
            .addEncodedQueryParameter(
                "params",
                params.params
            )
            .build()

        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Authorization", apiKey)
            .addHeader("Accept", "*/*")
            .build()
        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw Exception(
                        "Erro ao chamar StormGlass. HTTP status: ${response.code}"
                    )
                }

                val responseBody = response.body?.string()
                    ?: throw Exception(
                        "Resposta do stormGlass sem corpo"
                    )

                val response = objectMapper.readValue(responseBody, StormGlassResponse::class.java)

                return response
            }
        }catch (ex: Exception) {
            throw Exception(
                "Falha ao integrar com a Storm Glass",
                ex
            )
        }
    }


}