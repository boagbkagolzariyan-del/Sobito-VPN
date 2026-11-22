# Privacy — experimental build

This project has not received an independent privacy or security audit. Opening source code does not certify the APK or prove the absence of leaks.

The selected connection method necessarily contacts its service providers. Foxy signs in to Firefox Accounts and Mozilla/Fastly services with the account details provided on-device; Psiphon contacts its bootstrap and tunnel infrastructure. Those providers have their own privacy policies and can observe data appropriate to their role. Login credentials, tokens, and configuration may be stored by the upstream account components in app-private storage. Android app backup is disabled in this build. Review `native/foxy/core/internal/accounts`, the login flow, configuration, and upstream components for the actual storage behavior; do not assume hardware-backed encryption.

Foxy's dashboard uses a local authenticated HTTP API and a loopback SOCKS listener with generated credentials; other local listeners inherited from upstream require further security review. The app's own UID is excluded from its VPN so carrier processes can reach the network. Always-on lockdown, failure/reconnect, IPv6, DNS, UDP and other leak behavior must be independently tested.

Diagnostics may contain network and device information. Review and sanitize a report before sharing it; never post a password, verification code, token, private key, account backup, or unredacted packet capture publicly. See `SE7EN-FOXY-EXPERIMENTAL.md` for limits and required tests.
