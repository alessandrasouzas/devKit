package com.ale.devkit.lab.integrations.openWeather.client

import com.ale.devkit.lab.integrations.openWeather.controller.request.ParamsRequest
import com.ale.devkit.lab.integrations.openWeather.controller.response.StormGlassResponse

interface WeatherClient {
    fun getWeather(params: ParamsRequest): StormGlassResponse?
}