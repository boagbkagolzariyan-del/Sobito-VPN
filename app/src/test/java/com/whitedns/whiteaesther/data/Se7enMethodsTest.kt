package com.whitedns.whiteaesther.data

import org.junit.Assert.*
import org.junit.Test

class Se7enMethodsTest {
    @Test fun desktopOrderAndNoFallback() {
        val tokens = listOf("psiphon", "masque", "wireguard", "warp_on_warp", "tor", "psiphon_over_warp", "tor_over_warp", "shard", "psiphon_over_v2ray", "tor_over_v2ray", "masque_in_masque", "warp_over_v2ray", "foxy", "foxy_over_warp", "foxy_over_v2ray", "psiphon_over_foxy", "lantern", "lantern_over_warp", "veep", "veep_over_warp", "psiphon_over_lantern")
        assertEquals(tokens, Se7enMethod.entries.map { it.token })
        assertEquals(Carrier.FOXY, Se7enMethod.FOXY.candidate(AppSettings())!!.carrier)
        assertNull(Se7enMethod.FOXY_OVER_WARP.candidate(AppSettings()))
        assertEquals(10, Se7enMethod.entries.count { it.candidate(AppSettings()) != null })
    }
    @Test fun correctOuterBeforeInner() {
        val psiphon = Se7enMethod.PSIPHON_OVER_WARP.candidate(AppSettings())!!
        assertEquals(Carrier.AETHER, psiphon.carrier)
        assertEquals(Carrier.PSIPHON, psiphon.secondCarrier)
        assertFalse(psiphon.automaticCarrier)
        assertFalse(psiphon.chain.enabled)
        val foxy = Se7enMethod.PSIPHON_OVER_FOXY.candidate(AppSettings())!!
        assertEquals(listOf(Carrier.FOXY, Carrier.PSIPHON), foxy.carrierPath)
        assertFalse(foxy.automaticCarrier)
        val tor = Se7enMethod.TOR_OVER_WARP.candidate(AppSettings())!!
        assertEquals(Carrier.AETHER, tor.carrier)
        assertEquals(Carrier.TOR, tor.secondCarrier)
    }
    @Test fun aetherTransportMapsWithoutAutomaticSubstitution() {
        assertEquals(TunnelProtocol.WIREGUARD, Se7enMethod.WIREGUARD.candidate(AppSettings())!!.transport)
        assertEquals(TunnelProtocol.WIREGUARD_OVER_MASQUE, Se7enMethod.WARP_ON_WARP.candidate(AppSettings())!!.transport)
        assertEquals(TunnelProtocol.MASQUE_IN_MASQUE, Se7enMethod.MASQUE_IN_MASQUE.candidate(AppSettings())!!.transport)
    }
}
