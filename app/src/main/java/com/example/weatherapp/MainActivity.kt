package com.example.weatherapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.weatherapp.databinding.ActivityMainBinding
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Timer
import java.util.TimerTask

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    val APP_ID = "3e49f26d70812f5fd84a2883e7dfcb0b"
    val WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather"
    val MIN_TIME: Long = 5000
    val MIN_DISTANCE = 1000f
    val REQUEST_CODE = 101
    var Location_Provider = LocationManager.GPS_PROVIDER

    var mLocationManager: LocationManager? = null
    var mLocationListener: LocationListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (checkForInternet(this)) {
            localdata()
            weatherForCurrentLocation
            getWeatherForNewCity("New York")
            getWeatherForNewCity("singapore")
            getWeatherForNewCity("Mumbai")
            getWeatherForNewCity("Delhi")
            getWeatherForNewCity("sydney")
            getWeatherForNewCity("melbourne")
            Toast.makeText(this, "Retrieved successfully", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "please check your internet Connection", Toast.LENGTH_SHORT).show()
            localdata()
        }

    }

    override fun onResume() {
        super.onResume()
        localdata()
    }

    //retrieving the data stored in shared preferences

    private fun localdata() {
        val sharedPreferences = getSharedPreferences("MY_KEY", MODE_PRIVATE)
        val place = sharedPreferences.getString("place", "")
        val wstate = sharedPreferences.getString("wstate", "")
        val temp = sharedPreferences.getString("temperature", "")
        val icon = sharedPreferences.getInt("icon", 0)
        val datetime = sharedPreferences.getString("datetime","")

        val newyork = sharedPreferences.getString("newyork","")
        val singapore = sharedPreferences.getString("singapore","")
        val mumbai = sharedPreferences.getString("mumbai","")
        val delhi = sharedPreferences.getString("delhi","")
        val sydney = sharedPreferences.getString("sydney","")
        val melbourne = sharedPreferences.getString("melbourne","")
        if (place != null && wstate!= null && temp != null && icon != null && datetime!=null &&
            newyork!=null && singapore != null && mumbai!= null && delhi != null && sydney != null && melbourne!=null) {
            binding.wicon.setImageResource(icon)
            binding.place.text = place
            binding.wstate.text = wstate
            binding.temp.text = temp
            binding.datetime.text = datetime

            binding.newyork.text=newyork
            binding.mumbai.text = mumbai
            binding.singapore.text=singapore
            binding.delhi.text = delhi
            binding.sydney.text=sydney
            binding.melbourne.text = melbourne
        }
    }

    //check the availability of Internet

    private fun checkForInternet(context: Context): Boolean {

        // register activity with the connectivity manager service
        val connectivityManager = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

        // if the android version is equal to M
        // or greater we need to use the
        // NetworkCapabilities to check what type of
        // network has the internet connection
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // Returns a Network object corresponding to
            // the currently active default data network.
            val network = connectivityManager.activeNetwork ?: return false

            // Representation of the capabilities of an active network.
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                // Indicates this network uses a Wi-Fi transport,
                // or WiFi has network connectivity

                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true

                // Indicates this network uses a Cellular transport. or
                // Cellular has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

                // else return false
                else -> false
            }
        } else {
            // if the android version is below M
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }

    //Weather for the other locations

    private fun getWeatherForNewCity(city: String) {
        val params = RequestParams()
        params.put("q", city)
        params.put("appid", APP_ID)
        val client = AsyncHttpClient()
        client[WEATHER_URL, params, object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, response: JSONObject) {
                val WData = WeatherData.fromJson(response)
                val sharedPreferences = getSharedPreferences("MY_KEY", MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                if (WData != null) {
                    if (city=="New York"){
                        binding.newyork.text=WData.getmTemperature()+"/"+WData.getmWeatherType()
                        editor.putString("newyork",WData.getmTemperature()+"/"+WData.getmWeatherType())
                    }else if(city=="singapore"){
                        binding.singapore.text=WData.getmTemperature()+"/"+WData.getmWeatherType()
                        editor.putString("singapore",WData.getmTemperature()+"/"+WData.getmWeatherType())
                    }else if(city=="Mumbai"){
                        binding.mumbai.text=WData.getmTemperature()+"/"+WData.getmWeatherType()
                        editor.putString("mumbai",WData.getmTemperature()+"/"+WData.getmWeatherType())
                    }else if(city=="Delhi"){
                        binding.delhi.text=WData.getmTemperature()+"/"+WData.getmWeatherType()
                        editor.putString("delhi",WData.getmTemperature()+"/"+WData.getmWeatherType())
                    }else if(city=="sydney"){
                        binding.sydney.text=WData.getmTemperature()+"/"+WData.getmWeatherType()
                        editor.putString("sydney",WData.getmTemperature()+"/"+WData.getmWeatherType())
                    }else if(city=="melbourne"){
                        binding.melbourne.text=WData.getmTemperature()+"/"+WData.getmWeatherType()
                        editor.putString("melbourne",WData.getmTemperature()+"/"+WData.getmWeatherType())
                    }
                    editor.apply()


                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<Header>,
                throwable: Throwable,
                errorResponse: JSONObject
            ) {
                // super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        }]
    }

    //Weather for current Location

    private val weatherForCurrentLocation: Unit private get() {
            mLocationManager = getSystemService(LOCATION_SERVICE) as LocationManager
            mLocationListener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    val Latitude = location.latitude.toString()
                    val Longitude = location.longitude.toString()
                    val params = RequestParams()
                    params.put("lat", Latitude)
                    params.put("lon", Longitude)
                    params.put("appid", APP_ID)
                    letsdoSomeNetworking(params)
                }

                override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {}
            }
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_CODE
                )
                return
            }
            mLocationManager!!.requestLocationUpdates(
                Location_Provider,
                MIN_TIME,
                MIN_DISTANCE,
                mLocationListener as LocationListener
            )
        }

    //Getting the weather data by api call

    private fun letsdoSomeNetworking(params: RequestParams) {
        val client = AsyncHttpClient()
        client[WEATHER_URL, params, object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, response: JSONObject) {

                val WData = WeatherData.fromJson(response)

                WData?.let { updateUI(it) }

            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<Header>,
                throwable: Throwable,
                errorResponse: JSONObject
            ) {
                // super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        }]
    }

    //To Update the UI by the required data

    private fun updateUI(wData: WeatherData) {
        val resourceId = resources.getIdentifier(wData.micon, "drawable", packageName)
        binding.wicon.setImageResource(resourceId)
        binding.place.text = wData.mcity
        binding.wstate.text = wData.getmWeatherType()
        binding.temp.text = wData.getmTemperature()

        val simpleDateFormat = SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss z")
        val currentDateAndTime: String = simpleDateFormat.format(Date())

        binding.datetime.text="Last update was on "+currentDateAndTime

        val sharedPreferences = getSharedPreferences("MY_KEY", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("icon", resourceId)
        editor.putString("place", wData.mcity)
        editor.putString("wstate",wData.getmWeatherType())
        editor.putString("temperature",wData.getmTemperature())
        editor.putString("datetime","Last update was on "+currentDateAndTime)
        editor.apply()
    }

    //Requesting permissions from the device user

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this@MainActivity, "Location get Succesffully", Toast.LENGTH_SHORT)
                    .show()
                weatherForCurrentLocation
                getWeatherForNewCity("New York")
                getWeatherForNewCity("singapore")
                getWeatherForNewCity("Mumbai")
                getWeatherForNewCity("Delhi")
                getWeatherForNewCity("sydney")
                getWeatherForNewCity("melbourne")
            } else {
                //user denied the permission
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (mLocationManager != null) {
            mLocationManager!!.removeUpdates(mLocationListener!!)
        }
    }
}