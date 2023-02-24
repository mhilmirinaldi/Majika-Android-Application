package com.example.majika

import android.graphics.drawable.ColorDrawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.majika.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var binding: ActivityMainBinding

    private lateinit var sensorManager : SensorManager
    private var temperatureSensor : Sensor? = null
    private var menu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setBackgroundDrawable(ColorDrawable(getColor(R.color.navbar)))
        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_twibbon, R.id.navigation_restoran, R.id.navigation_menu, R.id.navigation_keranjang
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
        if (temperatureSensor == null) {
            Toast.makeText(this, "Sensor suhu tidak tersedia", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_nav_menu, menu)
        this.menu = menu ?: return false
        val temperatureMenu = menu.findItem(R.id.action_temperature)
        temperatureMenu.title = "-°C"

        return true
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (menu != null && event?.sensor?.type == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            val temperature = event.values[0]

            Log.d("MainActivity", "Temperature changed: $temperature")

            val temperatureMenuItem = menu!!.findItem(R.id.action_temperature)
            temperatureMenuItem.title = "${temperature}°C"
        }
    }
    override fun onResume() {
        super.onResume()
        temperatureSensor?.also { temperature ->
            sensorManager.registerListener(this, temperature, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }
}