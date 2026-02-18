package com.ale.devkit.lab.integrations.openWeather.client

import com.ale.devkit.lab.integrations.openWeather.controller.request.ParamsRequest
import com.ale.devkit.lab.integrations.openWeather.controller.response.StormGlassResponse

interface WeatherClient {
    // Implementação de mock devido a restrição de requests da api -> limite 5/dia: StormGlassMockClient
    fun getWeather(params: ParamsRequest): StormGlassResponse?
}