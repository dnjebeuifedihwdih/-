package com.example.core

import com.example.model.PublicIpInfo
import com.example.model.SupportedCountries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object IpLocationRepository {

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(6, TimeUnit.SECONDS)
        .readTimeout(8, TimeUnit.SECONDS)
        .build()

    suspend fun getPublicIpInfo(): PublicIpInfo = withContext(Dispatchers.IO) {
        // Method 1: ip-api.com for rich geo & ISP info
        try {
            val req = Request.Builder()
                .url("http://ip-api.com/json/?fields=status,message,country,countryCode,city,isp,org,query")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) JimVPN/2.35")
                .build()
            val resp = httpClient.newCall(req).execute()
            if (resp.isSuccessful) {
                val body = resp.body?.string() ?: ""
                val json = JSONObject(body)
                if (json.optString("status") == "success") {
                    val ip = json.optString("query")
                    val countryCode = json.optString("countryCode")
                    val country = json.optString("country")
                    val city = json.optString("city")
                    val isp = json.optString("isp").ifBlank { json.optString("org") }
                    val flag = SupportedCountries.find(countryCode).flagEmoji

                    return@withContext PublicIpInfo(
                        ip = ip,
                        country = if (flag.isNotBlank()) "$flag $country" else country,
                        countryCode = countryCode,
                        city = city,
                        isp = isp,
                        isSecureDns = true,
                        isLoading = false,
                        lastUpdated = System.currentTimeMillis()
                    )
                }
            }
        } catch (_: Exception) {}

        // Method 2: ipinfo.io
        try {
            val req = Request.Builder()
                .url("https://ipinfo.io/json")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) JimVPN/2.35")
                .build()
            val resp = httpClient.newCall(req).execute()
            if (resp.isSuccessful) {
                val body = resp.body?.string() ?: ""
                val json = JSONObject(body)
                val ip = json.optString("ip")
                val countryCode = json.optString("country")
                val city = json.optString("city")
                val org = json.optString("org")
                val flag = SupportedCountries.find(countryCode).flagEmoji

                if (ip.isNotBlank()) {
                    return@withContext PublicIpInfo(
                        ip = ip,
                        country = if (flag.isNotBlank()) "$flag $countryCode" else countryCode,
                        countryCode = countryCode,
                        city = city,
                        isp = org,
                        isSecureDns = true,
                        isLoading = false,
                        lastUpdated = System.currentTimeMillis()
                    )
                }
            }
        } catch (_: Exception) {}

        // Fallback 1: Cloudflare trace
        try {
            val req = Request.Builder()
                .url("https://www.cloudflare.com/cdn-cgi/trace")
                .header("User-Agent", "Mozilla/5.0 JimVPN")
                .build()
            val resp = httpClient.newCall(req).execute()
            if (resp.isSuccessful) {
                val lines = resp.body?.string()?.lines() ?: emptyList()
                var ip = ""
                var loc = ""
                for (line in lines) {
                    if (line.startsWith("ip=")) ip = line.removePrefix("ip=").trim()
                    if (line.startsWith("loc=")) loc = line.removePrefix("loc=").trim()
                }
                if (ip.isNotBlank()) {
                    val countryObj = SupportedCountries.find(loc)
                    return@withContext PublicIpInfo(
                        ip = ip,
                        country = "${countryObj.flagEmoji} ${countryObj.name}",
                        countryCode = loc,
                        city = "Cloudflare Edge",
                        isp = "Cloudflare Anycast Backbone",
                        isSecureDns = true,
                        isLoading = false,
                        lastUpdated = System.currentTimeMillis()
                    )
                }
            }
        } catch (_: Exception) {}

        // Fallback 2: ipify
        try {
            val req = Request.Builder()
                .url("https://api.ipify.org?format=json")
                .build()
            val resp = httpClient.newCall(req).execute()
            if (resp.isSuccessful) {
                val body = resp.body?.string() ?: ""
                val json = JSONObject(body)
                val ip = json.optString("ip")
                if (ip.isNotBlank()) {
                    return@withContext PublicIpInfo(
                        ip = ip,
                        country = "🌐 شبکه بین‌الملل",
                        countryCode = "NET",
                        city = "Protected Tunnel",
                        isp = "Protected Tunnel Gateway",
                        isSecureDns = true,
                        isLoading = false,
                        lastUpdated = System.currentTimeMillis()
                    )
                }
            }
        } catch (e: Exception) {
            return@withContext PublicIpInfo(
                ip = "خطا در دریافت آی‌پی",
                country = "نامشخص",
                errorMessage = "لطفاً اتصال اینترنت خود را بررسی کنید",
                isLoading = false
            )
        }

        return@withContext PublicIpInfo(
            ip = "نامشخص",
            country = "در انتظار اتصال",
            isLoading = false
        )
    }
}
