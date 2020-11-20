package com.github.karczews.androidauto.automotive.car

import android.location.Location

sealed class ProviderStatus {
    abstract fun describe(): String
}

object NoPermission : ProviderStatus() {
    override fun describe() = "No Permission to get location"
}

data class LocationUpdate(val providerEnabled: Boolean, val location: Location?) : ProviderStatus() {
    override fun describe() =
        "got $location, provider ${if (providerEnabled) "enabled" else "disabled"}"
}