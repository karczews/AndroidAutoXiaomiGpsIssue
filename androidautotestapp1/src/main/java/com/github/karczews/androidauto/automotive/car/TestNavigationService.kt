package com.github.karczews.androidauto.automotive.car

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.libraries.car.app.CarAppService
import com.google.android.libraries.car.app.CarToast
import com.google.android.libraries.car.app.Screen
import com.google.android.libraries.car.app.model.Action
import com.google.android.libraries.car.app.model.MessageTemplate
import com.google.android.libraries.car.app.model.Template

class TestNavigationService : CarAppService() {

    private var gpsService: GpsService? = null

    override fun onCreateScreen(intent: Intent): Screen {
        return object : Screen(carContext) {
            override fun getTemplate(): Template {
                val builder = MessageTemplate.builder("GpsStatus")

                builder.setHeaderAction(Action.APP_ICON)
                val actions = listOf(
                    Action.builder()
                        .setTitle("Start")
                        .setOnClickListener {
                            gpsService?.start()
                        }
                        .build(),
                    Action.builder()
                        .setTitle("Stop")
                        .setOnClickListener {
                            gpsService?.stop()
                        }
                        .build()
                )

                builder.setActions(actions)

                return builder.build()
            }
        }
    }

    init {
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                connectToGpsService()
            }

            override fun onDestroy(owner: LifecycleOwner) {
                disconnectFromGpsService()
            }
        })
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            gpsService = (service as GpsService.LocalBinder).getService()
            uilog("GpsService connected")
            gpsService?.providerStatus?.observe(this@TestNavigationService,
                {
                    uilog(it.describe())
                })
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            gpsService = null
        }

    }

    private fun connectToGpsService() {
        bindService(Intent(this, GpsService::class.java),
            serviceConnection,
            Context.BIND_AUTO_CREATE)
    }

    private fun disconnectFromGpsService() {
        unbindService(serviceConnection)
    }

    private fun uilog(message: String) {
        Log.d("UILOG", message)
        CarToast.makeText(carContext, message, CarToast.LENGTH_SHORT)
            .show()
    }
}