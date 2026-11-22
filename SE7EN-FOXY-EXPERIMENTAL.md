# Psiphon over Foxy — experimental, NOT validated on an Android device

Path: Android VpnService -> mihomo TUN -> Psiphon SOCKS -> Psiphon tunnel -> authenticated loopback SOCKS -> Arman/Foxy -> Firefox VPN (Mozilla/Fastly) -> Psiphon server. The final exit is Psiphon, not Firefox VPN.

In the home method selector choose «تنظیم حساب Firefox VPN (آزمایشی)», sign in to a Mozilla account with a usable paid Firefox VPN subscription, complete any email/2FA/CAPTCHA, then select `Psiphon over Foxy` and Connect. Login may itself be blocked in the user's network. This setup is not a browser-based OAuth flow; it is Arman's password-based API and will not handle all sign-in policies or Google/Apple-only accounts. Never send credentials in chat. Using a third-party client means trusting this build with account credentials and tokens.

This is an experimental development build. The developer environment had no live Mozilla account or physical Android device; subsequently one user reported that Psiphon over Foxy connected on their device. That report does not validate full routing, proxy loss, network switching, DNS, IP and leak behavior across devices. Android's always-on lockdown can block excluded app sockets. Do not use it for security-sensitive traffic until independent device tests pass. Psiphon core comments say UDPDial does not use UpstreamProxyURL; although the core filters incompatible tunnel protocols, full direct-traffic and DNS-leak tests are still required. The app's UID is excluded from its own VpnService for the cores to reach the network. This is NOT a guarantee of a system-wide kill switch. Firefox/Mozilla proxy limits destination TCP ports to 80, 443, 8080, 8443; Psiphon must find a permitted upstream-compatible server/transport. A clean IP or access to any particular AI service is not guaranteed.

The helper uses a per-launch authenticated localhost SOCKS proxy, passes credentials to Psiphon over an unexported service intent, and keeps the dashboard token under app-private storage. Other loopback listeners inherited from upstream have not all been security audited. Device/app backup is disabled.

## Build ARM64 debug (Linux)

Upstream baseline WhiteAestherMobile 7a27239d2d351e9cd273458ded1aeda7bd1102c7; Foxy core copied from armanlife/arman-vpn-client 492dd7f389c539790cde3d07167535d28788a0d0, GPL-3.0, included source and license in native/foxy. This is not an official Mozilla or Psiphon client. Se7en desktop baseline d9b5ceb14cc27588411e694bff21fe1f7bad5a02. See LICENSE and THIRD_PARTY_NOTICES.md for other components and fulfill upstream licenses before redistribution.

JDK 21, Android SDK 37.2, NDK 29.0.14206865, CMake 3.22.1, Go 1.27.2 (for Foxy), Rust, PowerShell and working network access to fetch Gradle/Go/native dependencies are required. See README.md and native/*/setup.ps1 for upstream prerequisites, native chain/Tor/Psiphon setup and supply-chain checks.

```bash
export ANDROID_HOME=/path/to/android-sdk JAVA_HOME=/path/to/jdk21
bash native/foxy/build-android.sh
./gradlew :app:assembleStableDebug :app:testStableDebugUnitTest \
  -PWHITEAESTHER_ABIS=arm64-v8a -PWHITEAESTHER_DISABLE_ABI_SPLITS=true
```

Other architectures need their own NDK cross-compiler, binary and testing; the bundled helper is arm64 only. Do not enable unimplemented methods by renaming them. Other Se7en connection methods, full kill switch, domain split behavior and desktop parity remain incomplete; see SE7EN-PORT-STATUS.md, whose earlier count predates this experiment.

## Required device tests before release
1. Sign in with a real Firefox VPN subscription and all offered confirmation flows; no logging or backup of passwords.
2. Prove outer Firefox exit and final Psiphon exit independently and compare with direct egress; check DNS, IPv4/IPv6, TCP/UDP and Psiphon bootstrap with packet capture.
3. Interrupt Foxy, Psiphon and network, including airplane mode/roaming, force stop, reboot, always-on/lockdown: confirm there is no direct Psiphon dial or unwanted fallback. Test split and all error paths.
4. Exercise port restrictions against actual Psiphon servers and test captive/bot-blocked login and lack of subscription.
5. Test Android 8+ and current 16 KB page devices and APK installation/PIE execution across supported vendors; build and test other architectures.
