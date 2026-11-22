package com.whitedns.whiteaesther.data

/** Desktop ConnectionMethod ordering; candidates are not validated equivalents. */
enum class Se7enMethod(val token: String, val label: String) {
    PSIPHON("psiphon", "Psiphon"), MASQUE("masque", "MASQUE"),
    WIREGUARD("wireguard", "WARP / WireGuard"), WARP_ON_WARP("warp_on_warp", "Gool"),
    TOR("tor", "Tor"), PSIPHON_OVER_WARP("psiphon_over_warp", "Psiphon over WARP"),
    TOR_OVER_WARP("tor_over_warp", "Tor over WARP"), SHARD("shard", "SHARD"),
    PSIPHON_OVER_V2RAY("psiphon_over_v2ray", "Psiphon over V2Ray"),
    TOR_OVER_V2RAY("tor_over_v2ray", "Tor over V2Ray"),
    MASQUE_IN_MASQUE("masque_in_masque", "MASQUE on MASQUE"),
    WARP_OVER_V2RAY("warp_over_v2ray", "WARP over V2Ray"),
    FOXY("foxy", "Foxy"), FOXY_OVER_WARP("foxy_over_warp", "Foxy over WARP"),
    FOXY_OVER_V2RAY("foxy_over_v2ray", "Foxy over V2Ray"),
    PSIPHON_OVER_FOXY("psiphon_over_foxy", "Psiphon over Foxy"),
    LANTERN("lantern", "Lantern"), LANTERN_OVER_WARP("lantern_over_warp", "Lantern over WARP"),
    VEEP("veep", "Veep"), VEEP_OVER_WARP("veep_over_warp", "Veep over WARP"),
    PSIPHON_OVER_LANTERN("psiphon_over_lantern", "Psiphon over Lantern");

    /** Null means not implemented: never silently substitute another method. */
    fun candidate(settings: AppSettings): AppSettings? {
        val pair = when (this) {
            FOXY -> Carrier.FOXY to null
            PSIPHON_OVER_FOXY -> Carrier.FOXY to Carrier.PSIPHON
            PSIPHON -> Carrier.PSIPHON to null
            MASQUE -> Carrier.AETHER to null
            WIREGUARD -> Carrier.AETHER to null
            WARP_ON_WARP -> Carrier.AETHER to null
            TOR -> Carrier.TOR to null
            PSIPHON_OVER_WARP -> Carrier.AETHER to Carrier.PSIPHON
            TOR_OVER_WARP -> Carrier.AETHER to Carrier.TOR
            MASQUE_IN_MASQUE -> Carrier.AETHER to null
            else -> return null
        }
        val protocol = when (this) {
            WIREGUARD -> TunnelProtocol.WIREGUARD
            WARP_ON_WARP -> TunnelProtocol.WIREGUARD_OVER_MASQUE
            MASQUE_IN_MASQUE -> TunnelProtocol.MASQUE_IN_MASQUE
            else -> TunnelProtocol.AUTO
        }
        // Disable the separate, optional exit-node chain; it changes egress and
        // is not part of the desktop ConnectionMethod. Keep stored nodes/rules.
        return settings.copy(
            automaticCarrier = false, carrier = pair.first, secondCarrier = pair.second,
            transport = protocol, mode = EngineMode.TUN,
            chain = settings.chain.copy(enabled = false),
        )
    }
}
