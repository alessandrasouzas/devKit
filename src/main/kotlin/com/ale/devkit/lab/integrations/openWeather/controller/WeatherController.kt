package com.ale.devkit.lab.integrations.openWeather.controller

import com.ale.devkit.lab.integrations.openWeather.controller.request.ParamsRequest
import com.ale.devkit.lab.integrations.openWeather.domain.DailyWeather
import com.ale.devkit.lab.integrations.openWeather.useCase.WeatherService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
class WeatherController (
    private val service: WeatherService
){

    @GetMapping("/previsoes")
    fun getDailyWeather(@RequestParam latitude: String,
                        @RequestParam longetude: String
    ): ResponseEntity<DailyWeather> {

        val params = ParamsRequest(latitude, longetude, null)
        val resp =  service.getDailyWeather(params)

        return ResponseEntity.ok(resp)
    }
}