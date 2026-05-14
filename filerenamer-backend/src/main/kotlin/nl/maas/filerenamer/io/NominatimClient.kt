package nl.maas.filerenamer.io

import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import org.apache.commons.lang3.StringUtils
import java.io.File
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.*

object NominatimClient {

    private const val userAgent: String = "FileRenamer/2022.1 (michel@maas-fam.nl)"
    private const val preferredLanguage: String = "nl,en"

    private val httpClient: HttpClient = HttpClient.newHttpClient()
    private const val minRequestIntervalMs: Long = 1_500L
    private var lastRequestTime: Long = 0L

    private val cache = HashMap<String, String>()

    fun fetchLocation(path: File): String {
        val location = MediaMetadataClient.fetchLocation(path.toPath())
        val latitude = location["lat"]
        val longitude = location["long"]

        if (latitude == null || longitude == null)
            return "Unknown location"

        val cacheKey = String.format(Locale.US, "%.4f,%.4f", latitude, longitude)
        if (!cache.containsKey(cacheKey)) {
            val location = fetchLocation(latitude, longitude)
            if (location.isNotBlank())
                cache.put(cacheKey, location)
        } else {
            println("Location $cacheKey already in cache")
        }

        return cache[cacheKey] ?: StringUtils.EMPTY
    }

    @Synchronized
    private fun waitForRateLimit() {
        val now = System.currentTimeMillis()
        val elapsed = now - lastRequestTime

        if (elapsed < minRequestIntervalMs) {
            Thread.sleep(minRequestIntervalMs - elapsed)
        }

        lastRequestTime = System.currentTimeMillis()
    }

    private fun fetchLocation(latitude: Double, longitude: Double): String {
        try {
            waitForRateLimit()
            val reverseGeocode = reverseGeocode(latitude, longitude)
            val locationData =
                Gson().fromJson<Map<String, Any>>(reverseGeocode, HashMap::class.java)
            val address = locationData["address"] as LinkedTreeMap<String, String>?
            val country = address?.let { it["country"] }
            val town = address?.let {
                it["city"]
                    ?: it["town"]
                    ?: it["village"]
                    ?: it["municipality"]
                    ?: it["hamlet"]
                    ?: it["county"]
                    ?: it["state"]
            }
            return "$country - $town"
        } catch (e: Exception) {
            return "Unknown location"
        }
    }

    private fun reverseGeocode(latitude: Double, longitude: Double): String {
        val url = "https://nominatim.openstreetmap.org/reverse" +
                "?format=jsonv2" +
                "&lat=$latitude" +
                "&lon=$longitude" +
                "&addressdetails=1" +
                "&accept-language=$preferredLanguage"

        val request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("User-Agent", userAgent)
            .header("Accept", "application/json")
            .header("Accept-Language", preferredLanguage)
            .GET()
            .build()

        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())

        if (response.statusCode() != 200) {
            println("Nominatim request failed: HTTP ${response.statusCode()}")
            println(response.body())
            return "Unknown location"
        }

        return response.body()
    }
}