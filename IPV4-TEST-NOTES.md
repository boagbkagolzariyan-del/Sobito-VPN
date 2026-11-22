# 0.3.1 IPv4 diagnostic build

This experimental build disables IPv6 in the VPN interface, native TUN configuration, and mihomo DNS for the exact Foxy -> Psiphon carrier combination. Other connection modes retain their previous settings. This is a diagnostic workaround, not a confirmed universal fix. Device and leak tests are needed. Android blocks IPv6 only for applications included in this VPN; excluded apps and disconnect/reconnect behavior require separate testing.

398 JVM unit tests passed. The delivered arm64-v8a debug APK reuses the native libraries and Psiphon server entries from the 0.3.0 arm64 debug APK; all ten native library files and the entries file matched byte-for-byte. This is not a fully reproducible build: building the same APK needs those prebuilt native artifacts or their own build toolchains.

The APK has a different debug signing key from 0.3.0. Android will not install it as an update over that version. Uninstalling the old app can erase local settings and other data. This is not a production release key.
