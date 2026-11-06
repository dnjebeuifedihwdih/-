package com.example.model

enum class TunnelMethod(val id: String, val title: String, val subtitle: String) {
    AETHER_CORE(
        id = "aether",
        title = "هسته هوشمند اِتِر (Aether Core)",
        subtitle = "ماسک، کلودفلر وارپ، سایفون و تور"
    ),
    GITHUB_VPS(
        id = "github_vps",
        title = "سرورهای اختصاصی VLESS Reality",
        subtitle = "فرگمنت ضد فیلترینگ و بدون قطعی"
    )
}

enum class AetherProtocol(val id: String, val label: String, val description: String, val flags: List<String>) {
    MASQUE_H2(
        id = "masque_h2",
        label = "ماسک H2 (پیش‌فرض پایدار)",
        description = "تونل پرسرعت TCP روی پورت ۴۴۳ - ضد اختلال و پکت‌دراپ UDP ایران",
        flags = listOf("--masque", "--http2")
    ),
    MASQUE_H3(
        id = "masque_h3",
        label = "ماسک H3 (QUIC UDP)",
        description = "تونل فوق‌العاده سریع HTTP/3 با تاخیر مینیمم",
        flags = listOf("--masque")
    ),
    GOOL_WG(
        id = "gool",
        label = "وایرگارد ترکیبی (GOOL)",
        description = "کپسوله‌سازی وایرگارد بر بستر پروتکل ماسک",
        flags = listOf("--gool")
    ),
    MIM_NESTED(
        id = "mim",
        label = "ماسک تو در تو (MIM)",
        description = "تونل دو لایه ضد فیلترینگ عمیق",
        flags = listOf("--mim")
    ),
    WARP_CLASSIC(
        id = "warp_classic",
        label = "وارپ کلاسیک (WARP)",
        description = "مسیریابی وارپ در وارپ دوگانه",
        flags = listOf("--gool-classic")
    )
}

enum class RoutingMode(val id: String, val label: String, val description: String) {
    AETHER_WARP_DEFAULT(
        id = "aether_warp",
        label = "اِتِر وارپ مستقیم (پیش‌فرض؛ پرسرعت‌ترین)",
        description = "phone → WARP (حداکثر سرعت و پهنای باند برای اینستاگرام و یوتوب)"
    ),
    PSIPHON_INSIDE(
        id = "psiphon_inside",
        label = "وارپ + سایفون (پوشش تمام کشورها)",
        description = "phone → WARP → Psiphon (ورودی کلودفلر با خروجی سایفون به کشور مقصد)"
    ),
    PSIPHON_REVERSE(
        id = "psiphon_reverse",
        label = "سایفون معکوس (شبکه‌های با اختلال شدید)",
        description = "phone → Psiphon → WARP (گذر از فیلترینگ با سایفون و خروج پرسرعت از وارپ)"
    ),
    PSIPHON_ONLY_CDN(
        id = "psiphon_cdn",
        label = "سایفون CDN فرانتینگ",
        description = "phone → Psiphon (اتصال مستقیم سایفون از طریق CDN)"
    ),
    TOR_INSIDE(
        id = "tor_inside",
        label = "وارپ + تور (حداکثر ناشناسی)",
        description = "phone → WARP → Tor (خروج محافظت‌شده از شبکه لایه‌ای تور)"
    ),
    TOR_REVERSE(
        id = "tor_reverse",
        label = "تور معکوس (Pluggable)",
        description = "phone → Tor → WARP (ورودی با ترنسپورت Lyrebird و خروج از وارپ)"
    )
}

enum class NoiseProfile(val id: String, val label: String, val value: String) {
    FIREWALL(id = "firewall", label = "فایروال (استاندارد)", value = "firewall"),
    GFW(id = "gfw", label = "ضد سانسور عمیق (GFW)", value = "gfw"),
    AGGRESSIVE(id = "aggressive", label = "نویز تهاجمی", value = "aggressive")
}

enum class ScanMode(val id: String, val label: String, val value: String) {
    TURBO(id = "turbo", label = "توربو (جستجوی سریع)", value = "turbo"),
    BALANCED(id = "balanced", label = "متوازن (پایدار)", value = "balanced")
}

data class Country(
    val code: String,
    val name: String,
    val flagEmoji: String = ""
)

object SupportedCountries {
    val list = listOf(
        Country("DE", "آلمان (Germany)", "🇩🇪"),
        Country("NL", "هلند (Netherlands)", "🇳🇱"),
        Country("US", "آمریکا (United States)", "🇺🇸"),
        Country("GB", "انگلیس (United Kingdom)", "🇬🇧"),
        Country("CA", "کانادا (Canada)", "🇨🇦"),
        Country("FR", "فرانسه (France)", "🇫🇷"),
        Country("FI", "فنلاند (Finland)", "🇫🇮"),
        Country("CH", "سوئیس (Switzerland)", "🇨🇭"),
        Country("SE", "سوئد (Sweden)", "🇸🇪"),
        Country("JP", "ژاپن (Japan)", "🇯🇵"),
        Country("SG", "سنگاپور (Singapore)", "🇸🇬"),
        Country("AT", "اتریش (Austria)", "🇦🇹"),
        Country("IT", "ایتالیا (Italy)", "🇮🇹"),
        Country("ES", "اسپانیا (Spain)", "🇪🇸"),
        Country("TR", "ترکیه (Turkey)", "🇹🇷"),
        Country("PL", "لهستان (Poland)", "🇵🇱")
    )

    fun find(code: String): Country =
        list.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: Country(code.uppercase(), code.uppercase(), "🌐")
}

data class VpsServer(
    val id: String,
    val title: String,
    val countryCode: String,
    val protocol: String = "vless",
    val address: String,
    val port: Int,
    val rawUri: String,
    val pingMs: Long = -1L,
    val isTesting: Boolean = false,
    val uuid: String = "",
    val security: String = "reality",
    val flow: String = "xtls-rprx-vision",
    val sni: String = "",
    val pbk: String = "",
    val sid: String = "",
    val fp: String = "chrome",
    val path: String = "",
    val host: String = ""
)

enum class VpnStatus(val titleFa: String) {
    DISCONNECTED("قطع - آماده به کار"),
    CONNECTING("در حال اتصال..."),
    CONNECTED("متصل به اینترنت آزاد"),
    ERROR("خطا در اتصال")
}

data class VpnConnectionState(
    val status: VpnStatus = VpnStatus.DISCONNECTED,
    val method: TunnelMethod = TunnelMethod.AETHER_CORE,
    val activeInfo: String = "آماده به کار",
    val pingMs: Long = -1L,
    val isPinging: Boolean = false,
    val bytesIn: Long = 0L,
    val bytesOut: Long = 0L,
    val downSpeedBytesPerSec: Long = 0L,
    val upSpeedBytesPerSec: Long = 0L,
    val totalRxBytes: Long = 0L,
    val totalTxBytes: Long = 0L,
    val connectedSince: Long = 0L,
    val errorMessage: String? = null
) {
    fun formattedDownSpeed(): String = formatBytesPerSec(downSpeedBytesPerSec)
    fun formattedUpSpeed(): String = formatBytesPerSec(upSpeedBytesPerSec)
    fun formattedTotalRx(): String = formatTotalBytes(totalRxBytes)
    fun formattedTotalTx(): String = formatTotalBytes(totalTxBytes)

    companion object {
        fun formatBytesPerSec(bytesPerSec: Long): String {
            if (bytesPerSec <= 0) return "۰ KB/s"
            val kb = bytesPerSec / 1024.0
            if (kb < 1000) {
                return String.format("%.0f KB/s", kb)
            }
            val mb = kb / 1024.0
            return String.format("%.1f MB/s", mb)
        }

        fun formatTotalBytes(bytes: Long): String {
            if (bytes <= 0) return "۰ MB"
            val mb = bytes / (1024.0 * 1024.0)
            if (mb < 1000) {
                return String.format("%.1f MB", mb)
            }
            val gb = mb / 1024.0
            return String.format("%.2f GB", gb)
        }
    }
}

data class PublicIpInfo(
    val ip: String = "",
    val country: String = "",
    val countryCode: String = "",
    val city: String = "",
    val isp: String = "",
    val isSecureDns: Boolean = true,
    val isLoading: Boolean = false,
    val lastUpdated: Long = 0L,
    val errorMessage: String? = null
)

enum class SplitTunnelMode(val titleFa: String, val subtitleFa: String) {
    ALL_APPS("تمام برنامه‌ها", "تمام ترافیک اینترنت گوشی از تونل وی‌پی‌ان عبور می‌کند."),
    BYPASS_SELECTED("دور زدن برنامه‌های انتخابی (Bypass)", "برنامه‌های انتخاب‌شده بدون فیلترشکن و مستقیماً متصل می‌شوند (بانک‌ها و اپ‌های ایرانی)."),
    ONLY_SELECTED("فقط برنامه‌های انتخابی (VPN Only)", "تنها برنامه‌های انتخاب‌شده از تونل وی‌پی‌ان عبور می‌کنند.")
}

data class AppInfoItem(
    val packageName: String,
    val appName: String,
    val isSystemApp: Boolean = false,
    val isIranianOrBank: Boolean = false
)
