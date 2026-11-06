# 🛡️ جیم وی پی ان | JIM VPN (v2.35)

![Android](https://img.shields.io/badge/Platform-Android-green.svg?style=flat-square&logo=android)
![Kotlin](https://img.shields.io/badge/Language-Kotlin-purple.svg?style=flat-square&logo=kotlin)
![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-blue.svg?style=flat-square&logo=jetpackcompose)
![Min SDK](https://img.shields.io/badge/Min%20SDK-24%20(Android%207.0)-orange.svg?style=flat-square)
![Target SDK](https://img.shields.io/badge/Target%20SDK-36-brightgreen.svg?style=flat-square)
![Version](https://img.shields.io/badge/Version-2.35-blueviolet.svg?style=flat-square)
![License](https://img.shields.io/badge/License-MIT-green.svg?style=flat-square)

**جیم وی پی ان (JIM VPN)** یک ابزار تانلینگ پیشرفته، پایدار و پرسرعت اندروید است که با بهره‌گیری از هسته‌های بومی **Aether Core** و **Xray (VLESS Reality)**، ارتباط اینترنتی بدون اختلال، امن و رمزنگاری‌شده را برای تمامی برنامه‌های گوشی فراهم می‌سازد.

این برنامه بر پایه زبان **Kotlin** و فریم‌ورک modern **Jetpack Compose** با الگوی طراحی متمایز **Light Liquid Glass (شیشه‌ای مایع)** پیاده‌سازی شده است.

---

## 🚀 ویژگی‌های کلیدی (Key Features)

- **⚡ دو هسته قدرتمند تانلینگ (Dual Core Architecture):**
  - **هسته اِتِر (Aether Core):** متدهای مدرن MASQUE QUIC (H3)، MASQUE H2 (TCP)، Warp Direct، Psiphon Pluggable Transports و Lyrebird Tor.
  - **هسته Xray VLESS Reality:** پشتیبانی از پروتکل‌های **VLESS + Reality + XTLS-Vision** با لایه امنیتی غیرقابل شناسایی.
- **🔄 دریافت خودکار سرورهای VPS آنلاین (GitHub & CDN Collector):**
  - به‌روزرسانی زنده سرورها از میرورهای CDN اختصاصی و ریپوزیتوری‌های گیت‌هاب.
  - تست پینگ متوازی و هوشمند (Parallel TCP Ping) جهت انتخاب خودکار سریع‌ترین نود.
- **🇮🇷 مدیریت ترافیک ایران و بای‌پاس هوشمند (Bypass Iran Traffic):**
  - هدایت مستقیم دامنه و آی‌پی‌های داخلی (`.ir`) جهت جلوگیری از مصرف ترافیک بین‌الملل و اتصال سریع به سایت‌های ایرانی.
- **📱 تفکیک برنامه‌ها (Split Tunneling):**
  - امکان مستثنی‌کردن همراه‌بانک‌ها، اسنپ، روبیکا و برنامه‌های ایرانی یا تانل‌کردن فقط برنامه‌های انتخابی.
  - دارای لیست پیش‌فرض هوشمند برای اپلیکیشن‌های بانکی و پرداخت ایران.
- **🌐 نمایش آنلاین آی‌پی خروجی و موقعیت مکانی (Live Geo IP Lookup):**
  - استعلام لحظه‌ای آی‌پی خروجی، ISP و پرچم کشور اتصال.
- **📊 سنجش سرعت زنده و مصرف ترافیک (Live Traffic Meter):**
  - نمایش سرعت آپلود، دانلود و حجم تبادل‌شده در طول اتصال.
- **🎛️ دکمه لغزنده تعاملی اتصال (iOS Slide-to-Tunnel Gesture):**
  - رابط کاربری لمسی فوق‌العاده نرم همراه با بازخورد هپتیک (Vibration).
- **🔘 کاشی تنظیمات سریع اندروید (Quick Settings Tile):**
  - قابلیت روشن/خاموش کردن فیلترشکن مستقیم از منوی اعلانات (Quick Settings) بدون نیاز به ورود به برنامه.
- **🔐 کلید قطع اضطراری (Kill Switch):**
  - قطع کامل ترافیک اینترنت در صورت قطع ناگهانی تانل جهت جلوگیری از نشت اطلاعات.
- **📋 گزارشات زنده سیستم (System Logs Viewer):**
  - دیباگ و مشاهده لحظه‌ای لاگ‌های هسته جهت عیب‌یابی خطوط شبکه.

---

## 🏗️ معماری و هسته‌های بومی (Architecture)

برنامه از معماری **Clean Architecture + MVVM** پیروی می‌کند و هسته‌های تانلینگ بومی C/C++ و Go در آن یکپارچه شده‌اند:

```text
┌─────────────────────────────────────────────────────────────┐
│              JIM VPN Jetpack Compose UI (M3)                │
│    (Liquid Glass Background, Slide Button, Dialogs)        │
└──────────────────────────────┬──────────────────────────────┘
                               │ Flow / StateFlow
┌──────────────────────────────▼──────────────────────────────┐
│                    MainViewModel & Core                     │
│    (IpLocationRepository, GithubVpsManager, Vibration)      │
└──────────────┬──────────────────────────────┬───────────────┘
               │                              │
┌──────────────▼──────────────┐┌──────────────▼──────────────┐
│     AetherCoreManager       ││      AetherPaperVpnService   │
│   (libaether.so / MASQUE)   ││      (VpnService & Xray)     │
└─────────────────────────────┘└─────────────────────────────┘
```

### 🔹 هسته اِتِر (Aether Core)
- **پروتکل‌ها:** MASQUE H3 (QUIC/UDP), MASQUE H2 (TCP), WARP Direct, Psiphon, Tor Lyrebird.
- **پروفایل‌های نویز (Noise Profiles):** Low Entropy, Medium Entropy, High Entropy جهت دور زدن سیستم‌های DPI.
- **حالت‌های اسکن (Scan Modes):** Aggressive, Smart CDN, Direct IP.

### 🔹 هسته Xray (libv2ray)
- **پروتکل اصلی:** `VLESS` با امنیت `Reality` (Curve25519) و `XTLS-Vision`.
- **دریافت سرور:** کانفیگ‌های مستقیم و CDN با فیلتر کشورها (آلمان 🇩🇪، هلند 🇳🇱، آمریکا 🇺🇸، فنلاند 🇫🇮، انگلیس 🇬🇧 و ...).

---

## 🛠️ تکنولوژی‌های استفاده‌شده (Tech Stack)

| بخش | تکنولوژی / کتابخانه |
| :--- | :--- |
| **زبان توسعه** | Kotlin 100% |
| **رابط کاربری** | Jetpack Compose + Material 3 |
| **طراحی** | Light Liquid Glass & Neumorphic Paper Theme |
| **معماری** | MVVM + Clean Architecture |
| **همروندی** | Kotlin Coroutines & Flow |
| **شبکه و HTTP** | Retrofit 2 + OkHttp 3 + Moshi |
| **ذخیره‌سازی** | Jetpack DataStore Preferences |
| **هسته‌های نیتیو** | C/C++ Shared Libraries (`libaether.so`, `libpsiphon.so`, `liblyrebird.so`) & Go (`libv2ray.aar`) |
| **حداقل اندروید** | Android 7.0 (API Level 24) |

---

## 📥 دانلود و نصب (Installation)

1. آخرین نسخه فایل `APK` را از بخش [Releases](../../releases) ریپوزیتوری دریافت کنید.
2. فایل را روی دستگاه اندرویدی خود نصب کنید.
3. هنگام اولین اجرا، مجوز اتصال **Android VPN Service** را تأیید کنید.

---

## 💻 راهنمای بیلد از سورس کد (Building from Source)

برای کامپایل و اجرای پروژه در Android Studio:

### پیش‌نیازها:
- Android Studio Ladybug (یا نسخه‌های جدیدتر)
- JDK 11 یا higher
- Android NDK (نسخه r25 یا بالاتر)

### مراحل بیلد:

```bash
# 1. کلون کردن ریپوزیتوری
git clone https://github.com/USERNAME/jim-vpn.git
cd jim-vpn

# 2. بیلد پروژه با Gradle
gradle assembleDebug
```

پس از اتمام بیلد، فایل APK در مسیر زیر تولید می‌شود:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 📱 اسکرین‌شات‌ها و نمای برنامه (User Interface)

- **صفحه اصلی:** دارای پنل Glassmorphism، دکمه تعاملی Slide-to-Tunnel، سنجش سرعت زنده و نمایش آی‌پی.
- **انتخاب سرور:** قابلیت تست پینگ متوازی و انتخاب بر اساس کشور.
- **تفکیک برنامه‌ها (Split Tunneling):** انتخاب اپلیکیشن‌های دلخواه جهت بای‌پاس یا تانل کامل.
- **تنظیمات پیشرفته:** تنظیم پروفایل نویز، متد اسکن، پروتکل اتِر و کلید قطع اضطراری.

---

## ⚠️ سلب مسئولیت قانونی (Legal Disclaimer)

این پروژه صرفاً جهت اهداف آموزشی، تحقیقاتی و حفظ حریم خصوصی کاربران توسعه یافته است. توسعه‌دهندگان هیچ‌گونه مسئولیتی در قبال استفاده نادرست از این ابزار ندارند.

---

## 📜 لایسنس (License)

این پروژه تحت لایسنس **MIT** منتشر شده است. برای اطلاعات بیشتر فایل [LICENSE](LICENSE) را مطالعه کنید.
