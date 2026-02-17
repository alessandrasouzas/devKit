package com.ale.devkit.lab.integrations.openWeather.useCase

import com.ale.devkit.lab.integrations.openWeather.client.WeatherClient
import com.ale.devkit.lab.integrations.openWeather.controller.request.ParamsRequest
import com.ale.devkit.lab.integrations.openWeather.domain.DailyWeather
import com.ale.devkit.lab.integrations.openWeather.controller.response.StormGlassResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.round
import kotlin.math.roundToInt

@Service
class WeatherService(
    private val weatherClient: WeatherClient,

    @Value("\${integration.stormGlass.params}")
    private val parametros: String

) {

    fun getDailyWeather(params: ParamsRequest): DailyWeather? {
        params.params = parametros
        var response = weatherClient.getWeather(params)
        val retorno = response?.toDailySummary()

        return retorno
    }

    fun StormGlassResponse.toDailySummary(): DailyWeather {

        val today = LocalDate.now(ZoneId.of("America/Sao_Paulo"))
        val todayHours = this.hours.filter {
            OffsetDateTime.parse(it.time)
                .atZoneSameInstant(ZoneId.of("America/Sao_Paulo"))
                .toLocalDate() == today
        }
        if (todayHours.isEmpty()) {
            throw IllegalStateException("No weather data available for today")
        }
        val temperatures = todayHours.mapNotNull { it.airTemperature?.sg }
        val humidities = todayHours.mapNotNull { it.humidity?.sg }
        val precipitations = todayHours.mapNotNull { it.precipitation?.sg }
        val clouds = todayHours.mapNotNull { it.cloudCover?.sg }
        val totalRain = precipitations.sum()
        val avgCloud = clouds.average()
        val resumo = when {
            totalRain > 10 -> "Dia chuvoso com pancadas intensas"
            totalRain > 0 -> "Possibilidade de chuva ao longo do dia"
            avgCloud > 70 -> "Dia nublado"
            else -> "Dia ensolarado"
        }
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

        return DailyWeather(
            data = today.format(formatter),
            temperatura_minima = temperatures.minOrNull()?.roundToInt()?.toInt()?:0,
            temperatura_maxima = temperatures.maxOrNull()?.roundToInt()?.toInt()?:0,
            umidade_media = humidities.average().roundToInt(),
            volumeChuvaMm = (round(precipitations.sum() * 10) / 10),
            nebulosidadeMedia = clouds.average().roundToInt(),
            resumo = resumo
        )
    }
}