# Security

This is an experimental project with no independent audit or guaranteed security support. Public source permits scrutiny but is not proof of security or of a distributed APK's correspondence to the source.

Before public release the repository owner should enable GitHub private vulnerability reporting and identify a monitored private contact. Until that exists, do not post sensitive vulnerabilities, account credentials, private keys, access tokens or packet captures in public issues. Reports should give the version, Android version, component, impact, reproduction steps and sanitized evidence.

Areas needing review include Firefox account handling and storage, all loopback listeners, executable lifecycle and process boundaries, Psiphon upstream-proxy enforcement and direct UDP/DNS behavior, routing and app exclusion, network changes, kill-switch/lockdown behavior, and dependency provenance. See `SE7EN-FOXY-EXPERIMENTAL.md`.
