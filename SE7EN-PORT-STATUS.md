# Se7en Pro Android multicore port — experimental baseline

This is a modified, incomplete fork of WhiteDNS/WhiteAestherMobile commit `7a27239d2d351e9cd273458ded1aeda7bd1102c7`, not an official Se7en Pro release and not yet feature equivalent to Se7en Pro. The desktop baseline is `d9b5ceb14cc27588411e694bff21fe1f7bad5a02`. Original project documentation, licenses, source and third-party notices are retained. The fork changes the application ID and name and adds an experimental desktop-method selector mapping ten entries to existing upstream mechanisms. These mappings are candidates only; no exact equivalence, complete settings parity, or live connection tests are established. Unsupported entries are disabled without fallback. Do not claim the upstream's capabilities are Se7en Pro feature parity. App ID differs to avoid overwriting the upstream installation.

The upstream source contains Aether/MASQUE, WireGuard, Gool-like WireGuard-over-MASQUE, MASQUE-in-MASQUE, Psiphon and Tor, plus its own routing and chain mechanisms. Whether these match the desktop methods and settings needs a separate per-method validation. An experimental Foxy outer hop and Psiphon-over-Foxy have been added but are not device-validated; Lantern, Veep, SHARD and most desktop chain semantics remain absent. Psiphon and Tor both depend on the optional separately built mihomo/FlClash library; Psiphon also needs the independently acquired signed bootstrap list. Tor bridges need separately built PTs. See the upstream README and native directories for the exact build and license requirements.

The updater is disabled in this experimental build with WHITEAESTHER_NO_SELF_UPDATE in gradle.properties; it must not install WhiteAesther release binaries over this fork. The license for this derivative is AGPL-3.0; other bundled components have their own licenses and distribution obligations. Do not distribute without fulfilling each obligation.

This directory is a proposed starting point for the full port, not a completed port and not a verified release. See ../android/PORTING.md and ../android/SOURCE_MAP.md for the required method-by-method acceptance work.

For the latest Foxy experiment, build instructions and important privacy warnings, see `SE7EN-FOXY-EXPERIMENTAL.md`.

## Current method selector

These are **candidate implementations, not device-verified parity**. Desktop WARP identity provisioning, scan behavior, Foxy credentials, split routing and every advanced setting still require compatibility work.

| Desktop method | Selected baseline path |
|---|---|
| `psiphon` | Psiphon carrier + native chain + signed server entries |
| `masque` | Aether, automatic H2/H3 MASQUE |
| `wireguard` | Aether WireGuard transport |
| `warp_on_warp` | Aether WireGuard over MASQUE (Gool candidate) |
| `tor` | Tor carrier + native chain; bridges need separate transports |
| `psiphon_over_warp` | Aether outer → Psiphon inner + native chain |
| `tor_over_warp` | Aether outer → Tor inner + native chain |
| `masque_in_masque` | Aether MASQUE-in-MASQUE |
| `foxy` | Experimental Arman/Foxy (Firefox VPN) outer hop; not device tested |
| `psiphon_over_foxy` | Experimental Foxy outer → Psiphon inner; not device tested |
| `shard`, `psiphon_over_v2ray`, `tor_over_v2ray`, `warp_over_v2ray`, `foxy_over_warp`, `foxy_over_v2ray`, `lantern`, `lantern_over_warp`, `veep`, `veep_over_warp`, `psiphon_over_lantern` | Disabled, not implemented |

The upstream also has native Android app selection, route rules and a failure-blocking implementation, but its semantics are not yet demonstrated to match all desktop include/exclude, IP/domain routing and kill-switch cases. Its chain path excludes the application package from the VPN, and OS lockdown can interfere with carriers; these and private DNS, IPv6, process death, permission revocation and reconnect behavior need independent device testing. Treat all security controls as unverified, not a guarantee.

## Build notes for this fork

See upstream README for the full prerequisites. For Linux builds, ensure the Android SDK CMake `bin` directory containing `ninja` is on `PATH` as well as Cargo. Native Aether, the separate Go chain and (for Tor bridges) Tor transport builds need NDK 29, Rust 1.98.0, Go, PowerShell, Android SDK 37 and JDK 21. Narrowing to ARM64 with `-PWHITEAESTHER_ABIS=arm64-v8a` creates an incomplete-ABI test APK, not a production release. Without the signed bootstrap list or chain native library, Psiphon is not operational. Run the upstream pinned setup and verification scripts, review all dependencies and distribution rights, and never treat build success as proof of network functionality.

## Build verification in this workspace

Before the Foxy changes, an ARM64 debug build was assembled using JDK 21 and Android SDK 37.0, NDK 29, Rust 1.98.0 and Go; its Psiphon bootstrap list passed the upstream signature verifier (430 entries). The APK includes the Aether, chain and Tor native libraries and Tor PT binaries. All 393 local unit tests passed, including three for the desktop-method mapping. These are build/unit-test results only: no APK installation, live tunnel, per-ABI, network leak or security verification was performed. For a clean source checkout, generated dependencies, native binaries and the bootstrap list are not committed; run the documented setup scripts to recreate them.
