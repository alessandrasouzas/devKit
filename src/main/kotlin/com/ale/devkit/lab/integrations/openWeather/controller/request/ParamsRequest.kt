package com.ale.devkit.lab.integrations.openWeather.controller.request

data class ParamsRequest (
    val lat: String,
    val lng: String,
    var params: String?
)