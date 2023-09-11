package com.example.ldattempt3

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Binder
import android.os.IBinder
import android.os.PowerManager
import android.util.Log


class AccelerometerService : Service(), SensorEventListener {

    private val TAG = ".AccelerometerService"
    private val binder = LocalBinder()
    private lateinit var sensorManager: SensorManager
    private var accelerometerSensor: Sensor? = null
    private val NOTIFICATION_ID = 123
    private val NOTIFICATION_ID2 = 124
    private lateinit var wakeLock: PowerManager.WakeLock
    private lateinit var mNM: NotificationManager

        override fun onBind(intent: Intent?): IBinder? {
            return binder
        }

        inner class LocalBinder : Binder() {
            fun getService(): AccelerometerService {
                return this@AccelerometerService
            }
        }

        override fun onCreate() {
            super.onCreate()

            mNM = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
            accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

            if (accelerometerSensor == null) {
                Log.e(TAG, "Accelerometer sensor not available on this device.")
                stopSelf()
            } else {
                val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
                wakeLock = powerManager.newWakeLock(
                    PowerManager.PARTIAL_WAKE_LOCK,
                    "AccelerometerService::WakelockTag"
                )
                wakeLock.acquire(1000*3600*10) // Acquire the wake lock
                accelerometerSensor?.let {
                    sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
                }
            }
        }

        override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
            accelerometerSensor?.let {
                sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
            }
            val notification = createNotification()
            startForeground(NOTIFICATION_ID, notification)
            return START_STICKY
        }

        override fun onDestroy() {
            super.onDestroy()
            mNM.cancel(NOTIFICATION_ID2)
            sensorManager.unregisterListener(this)
            wakeLock.release() // Release the wake lock
        }

        private fun createNotification(): Notification {
            val notificationIntent = Intent(this, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
            )

            return Notification.Builder(this, "SOME_CHANNEL_ID")
                .setContentTitle("Accelerometer Service")
                .setContentText("Recording accelerometer data")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .build()
        }

        override fun onSensorChanged(sensorEvent: SensorEvent?) {
            if (sensorEvent != null) {
                val x = sensorEvent.values[0] * sensorEvent.values[0] +
                        sensorEvent.values[1] * sensorEvent.values[1] +
                        sensorEvent.values[2] * sensorEvent.values[2] - 96.2361 // 9.81**2
                if (x > 7.0) {
                    val intent = Intent("SOME_ACTION")
                    intent.putExtra("someKey", "set_movement_t_now(${x});")
                    sendBroadcast(intent)
                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            // Not used in this example
        }
}