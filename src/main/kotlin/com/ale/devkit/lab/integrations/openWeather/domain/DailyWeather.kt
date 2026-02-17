package com.ale.devkit.lab.integrations.openWeather.domain

import com.fasterxml.jackson.annotation.JsonProperty

data class DailyWeather(
    @JsonProperty("data")
    val data: String,
    @JsonProperty("temperatura_minima")
    val temperatura_minima: Int,
    @JsonProperty("temperatura_maxima")
    val temperatura_maxima: Int,
    @JsonProperty("umidade_media")
    val umidade_media: Int,
    @JsonProperty("volume_chuva_mm")
    val volumeChuvaMm: Double,
    @JsonProperty("nebulosidade_media")
    val nebulosidadeMedia: Int,
    @JsonProperty("resumo")
    val resumo: String
)