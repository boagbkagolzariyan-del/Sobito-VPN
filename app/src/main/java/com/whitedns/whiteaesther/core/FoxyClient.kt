package com.whitedns.whiteaesther.core

import android.content.Context
import com.whitedns.whiteaesther.service.CarrierProbe
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow

/** Foxy is always an outer hop; downstream must use its authenticated SOCKS listener. */
class FoxyClient(private val context: Context, private val scope: CoroutineScope) : CarrierClient {
    private val snapshot = MutableStateFlow(CarrierSnapshot())
    override val state = snapshot
    override val socksAuth: SocksAuth? get() = runCatching { FoxyRuntime.credentials() }.getOrNull()
    private var watcher: Job? = null
    override suspend fun start(timeoutMs: Long): Result<Int> = withContext(Dispatchers.IO) {
        runCatching {
            FoxyRuntime.ensureStarted(context)
            snapshot.value = CarrierSnapshot(CarrierStage.CONNECTING)
            withTimeout(timeoutMs) {
                while (FoxyRuntime.status()?.optString("api").isNullOrBlank()) {
                    check(FoxyRuntime.running()) { "Foxy helper stopped" }; delay(300)
                }
                FoxyRuntime.connect()
                var port = 0
                while (port == 0) {
                    check(FoxyRuntime.running()) { "Foxy helper stopped" }
                    val s = FoxyRuntime.status()
                    if (s != null && s.optBoolean("on") && !s.optBoolean("pending")) {
                        port = s.optString("proxy").substringAfterLast(':').toIntOrNull() ?: 0
                    }
                    delay(300)
                }
                check(CarrierProbe.works(port, auth = checkNotNull(socksAuth))) { "Foxy proxy did not pass a network test; check Firefox VPN account" }
                snapshot.value = CarrierSnapshot(CarrierStage.CONNECTED, port)
                watcher = scope.launch(Dispatchers.IO) {
                    while (isActive) {
                        delay(1500)
                        if (!FoxyRuntime.running() || FoxyRuntime.status()?.optBoolean("on") == false) {
                            snapshot.value = CarrierSnapshot(CarrierStage.FAILED, failure = "Foxy stopped")
                            break
                        }
                    }
                }
                port
            }
        }.onFailure { snapshot.value = CarrierSnapshot(CarrierStage.FAILED, failure = it.message) }
    }
    override fun stop() { watcher?.cancel(); FoxyRuntime.stop(); snapshot.value = CarrierSnapshot() }
}
