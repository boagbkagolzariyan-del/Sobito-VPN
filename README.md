<div align="center">
  <img src="branding/sobito-logo.png" alt="Sobito VPN logo" width="160">
  <h1>Sobito VPN</h1>
  <p>کلاینت آزمایشی اندروید با اولویت Psiphon over Foxy</p>
</div>

> **وضعیت: آزمایشی؛ حسابرسی امنیتی مستقل انجام نشده است.** یک کاربر اتصال Psiphon over Foxy را در شبکهٔ خود گزارش کرده است. این گزارش، صحت همهٔ دستگاه‌ها، جلوگیری از نشت یا امنیت کد و فایل APK را تضمین نمی‌کند. برای کاربرد حساس از این نسخه بدون ارزیابی مستقل استفاده نکنید.

Sobito VPN یک کلاینت اندرویدی آزمایشی و مستقل است. این برنامه وابستگی رسمی به Mozilla یا Psiphon ندارد. کد اجزای متن‌باز، اطلاعات حق نشر و مجوزهای مربوط به آن‌ها در فایل `THIRD_PARTY_NOTICES.md` ثبت شده‌اند.

## مسیر موردنظر

```text
برنامه‌ها → Android VpnService → mihomo → Psiphon → پراکسی محلی Foxy → Firefox VPN → سرور Psiphon → مقصد
```

در روش **Psiphon over Foxy**، Foxy ابتدا با حساب دارای اشتراک Firefox VPN وصل می‌شود و سایفون با پراکسی SOCKS محلی Foxy تلاش می‌کند به سرورهای سایفون وصل شود. خروجی موردنظر، سایفون است؛ هیچ تضمینی برای مکان، کیفیت IP یا دسترسی به سرویس مشخص وجود ندارد. ورود به حساب Firefox در این برنامهٔ شخص ثالث به کد این پروژه و وابستگی‌های آن اعتماد می‌خواهد. رمز یا کد تأییدتان را در issue عمومی نفرستید.

## وضعیت قابلیت‌ها

| وضعیت | روش‌ها |
| --- | --- |
| پیاده‌سازی آزمایشی؛ اتصال توسط یک کاربر گزارش شده | Psiphon over Foxy |
| گزینه‌های آزمایشی؛ عملکرد روی همهٔ شبکه‌ها تأیید نشده | Foxy، Psiphon، MASQUE، WARP / WireGuard، Gool، Tor، Psiphon over WARP، Tor over WARP، MASQUE on MASQUE |
| هنوز پیاده‌سازی نشده؛ برنامه‌ریزی برای نسخه‌های بعدی، بدون تاریخ تضمین‌شده | SHARD، Psiphon over V2Ray، Tor over V2Ray، WARP over V2Ray، Foxy over WARP، Foxy over V2Ray، Lantern، Lantern over WARP، Veep، Veep over WARP، Psiphon over Lantern |

قابلیت‌های مسیردهی، split tunneling، DNS، IPv6، UDP، قطع ناگهانی اتصال و kill switch هنوز به‌صورت مستقل در همهٔ حالت‌ها تأیید نشده‌اند. رابط اندروید برنامه را از VPN خودش مستثنا می‌کند و وضعیت always-on/lockdown نیز باید آزموده شود. وجود یک کد upstream proxy به‌تنهایی اثبات نبود خروج مستقیم نیست. برای محدودیت‌های اتصال و تست‌های ضروری، [راهنمای فنی Foxy](SE7EN-FOXY-EXPERIMENTAL.md) و [وضعیت پورت](SE7EN-PORT-STATUS.md) را بخوانید.

## Build / ساخت

در حال حاضر دستور زیر فقط نسخهٔ **ARM64** آزمایشی را هدف می‌گیرد. JDK 21، Android SDK 37، NDK `29.0.14206865`، CMake 3.22.1، Go 1.27.2، Rust با هدف‌های اندروید و cargo-ndk، PowerShell و دسترسی به منابع وابستگی لازم است. اطلاعات کامل upstream در [راهنمای اصلی](docs/UPSTREAM-README.md) و `native/*/` است. وابستگی‌ها و باینری‌ها دانلود/ساخته می‌شوند و در این بسته درج نشده‌اند.

```bash
export JAVA_HOME=/path/to/jdk21
export ANDROID_HOME=/path/to/android-sdk
export PATH="$JAVA_HOME/bin:$ANDROID_HOME/cmake/3.22.1/bin:$PATH"
pwsh -File native/chain/setup.ps1
pwsh -File native/chain/build.ps1 -Abi arm64-v8a
pwsh -File native/psiphon/setup.ps1
# Optional for Tor bridges:
pwsh -File native/tor/setup.ps1
pwsh -File native/tor/build.ps1 -Abi arm64-v8a
bash native/foxy/build-android.sh
./gradlew :app:assembleStableDebug :app:testStableDebugUnitTest \
  -PWHITEAESTHER_ABIS=arm64-v8a -PWHITEAESTHER_DISABLE_ABI_SPLITS=true
```

APK دیباگ نتیجهٔ آزمون امنیتی نیست. برای تأیید مطابقت یک APK منتشرشده با کد، باید فرایند build و امضا جداگانه بررسی شود؛ بازبودن کد به‌تنهایی این تطابق را اثبات نمی‌کند. بسته‌بندی و بازتوزیع باید پس از بررسی همهٔ مجوزهای وابستگی‌ها انجام شود.

## ساختار کد و مجوزها

- `app/`: رابط اندروید، سرویس VPN، مسیرهای اتصال و تست‌ها.
- `native/foxy/core/`: سورس هستهٔ Foxy با برنامهٔ واسط `cmd/se7en-foxy`.
- `native/android-bridge/`, `native/aether/`: رابط و هستهٔ Aether.
- `native/chain/`, `native/psiphon/`, `native/tor/`: کد واسط، اسکریپت‌های تهیه و بیلد وابستگی‌ها.
- `branding/`: نشان پیشنهادی Sobito.
- `LICENSE`, `THIRD_PARTY_NOTICES.md`, `native/foxy/LICENSE`: مجوزها و اعلان‌های مؤلفان و اجزای متن‌باز.

