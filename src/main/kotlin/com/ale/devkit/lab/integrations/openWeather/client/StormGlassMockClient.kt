package com.ale.devkit.lab.integrations.openWeather.client

import com.ale.devkit.lab.integrations.openWeather.controller.request.ParamsRequest
import com.ale.devkit.lab.integrations.openWeather.controller.response.StormGlassResponse
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Profile("local")
@Component
class StormGlassMockClient(
    private val objectMapper: ObjectMapper
) : WeatherClient {

    override fun getWeather(params: ParamsRequest): StormGlassResponse? {

        val inputStream = javaClass
            .getResourceAsStream("/mock/stormglass-response.json")
            ?: throw IllegalArgumentException("Mock file not found")

        return objectMapper.readValue(inputStream, StormGlassResponse::class.java)
    }
}