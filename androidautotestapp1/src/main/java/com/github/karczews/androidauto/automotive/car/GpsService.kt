package com.github.karczews.androidauto.automotive.car

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.karczews.androidauto.automotive.MainActivity
import com.github.karczews.androidauto.automotive.R

class GpsService : Service() {
    private val binder = LocalBinder()
    override fun onBind(intent: Intent?): IBinder? = binder

    private val _providerStatus = MutableLiveData<ProviderStatus>()
    val providerStatus: LiveData<ProviderStatus> = _providerStatus

    private val locationManager by lazy { getSystemService(LocationManager::class.java)!! }
    private val locationListener = object : LocationListener {
        private var providerEnabled = false
        private var location: Location? = null
        override fun onLocationChanged(location: Location?) {
            Log.d(toString(), "onLocationChanged $location")
            this.location = location
            locationUpdated()
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            Log.d(toString(), "onStatusChanged $provider, status=$status")
            // deprecated
        }

        override fun onProviderEnabled(provider: String?) {
            Log.d(toString(), "onProviderEnabled")
            providerEnabled = true
            locationUpdated()
        }

        override fun onProviderDisabled(provider: String?) {
            Log.d(toString(), "onProviderDisabled")
            providerEnabled = false
            locationUpdated()
        }

        private fun locationUpdated() {
            _providerStatus.value = LocationUpdate(providerEnabled = providerEnabled, location = location)
        }
    }

    fun start() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            _providerStatus.value = NoPermission
            return
        }

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        createNotificationChannel(this)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("GpsService")
            .setContentIntent(pendingIntent)
            .build()

        startForeground(1, notification);

        Log.d(toString(), "requesting location updates")
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0f, locationListener)
    }

    fun stop() {
        stopForeground(true)
        locationManager.removeUpdates(locationListener)
    }


    inner class LocalBinder : Binder() {
        fun getService(): GpsService = this@GpsService
    }

    companion object {
        const val CHANNEL_ID = "GpsService"
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun createNotificationChannel(context: Context) {
        val manager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        var channel = manager.getNotificationChannel(CHANNEL_ID)
        if (channel == null) {
            channel = NotificationChannel(CHANNEL_ID, "GPSServiceChannel", NotificationManager.IMPORTANCE_MIN)
            channel.description = "GpsService channel"
            channel.setShowBadge(false)
            channel.enableVibration(false)
            channel.lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            manager.createNotificationChannel(channel)
        }
    }
}