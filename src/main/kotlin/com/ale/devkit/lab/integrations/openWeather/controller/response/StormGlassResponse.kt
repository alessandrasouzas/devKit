package com.ale.devkit.lab.integrations.openWeather.controller.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class StormGlassResponse(

    @JsonProperty("hours")
    val hours: List<StormGlassHour>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class StormGlassHour(
    @JsonProperty("airTemperature")
    val airTemperature: AirTemperature? = null,

    @JsonProperty("cloudCover")
    val cloudCover: CloudCover? = null,

    @JsonProperty("humidity")
    val humidity: Humidity? = null,

    @JsonProperty("precipitation")
    val precipitation: Precipitation? = null,

    @JsonProperty("time")
    val time: String
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AirTemperature(

    @JsonProperty("sg")
    val sg: Double? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class CloudCover(

    @JsonProperty("sg")
    val sg: Double? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Precipitation(
    @JsonProperty("sg")
    val sg: Double? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Humidity(
    @JsonProperty("sg")
    val sg: Double? = null
)