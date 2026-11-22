package com.whitedns.whiteaesther.core

import android.content.Context
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

/** The Arman Go core runs as a separate executable, not in either Go library process. */
object FoxyRuntime {
    private var process: Process? = null
    private var auth: SocksAuth? = null
    private lateinit var dir: File

    @Synchronized fun ensureStarted(context: Context) {
        if (process?.isAlive == true) return
        dir = File(context.filesDir, "foxy").apply { mkdirs() }
        File(dir, "se7en-status.json").delete()
        val executable = File(context.applicationInfo.nativeLibraryDir, "libse7enfoxy.so")
        check(executable.isFile) { "Foxy helper is missing for this device architecture" }
        val credentials = SocksAuth.random()
        val child = ProcessBuilder(executable.absolutePath)
            .redirectErrorStream(true).redirectOutput(File("/dev/null")).start()
        try {
            val setup = JSONObject().put("dir", dir.absolutePath).put("user", credentials.user)
                .put("password", credentials.password)
            child.outputStream.write((setup.toString() + "\n").toByteArray(Charsets.UTF_8))
            child.outputStream.flush()
            process = child; auth = credentials
        } catch (e: Exception) { child.destroy(); throw e }
    }
    @Synchronized fun credentials(): SocksAuth = checkNotNull(auth)
    @Synchronized fun running(): Boolean = process?.isAlive == true
    @Synchronized fun connect() {
        check(running()) { "Foxy helper is not running" }
        process!!.outputStream.write("connect\n".toByteArray()); process!!.outputStream.flush()
    }
    @Synchronized fun stop() {
        runCatching { process?.outputStream?.close() }
        runCatching { process?.destroy() }
        process = null; auth = null
        if (::dir.isInitialized) File(dir, "se7en-status.json").delete()
    }
    fun status(): JSONObject? = runCatching {
        JSONObject(File(dir, "se7en-status.json").readText())
    }.getOrNull()
    /** Private API token is read only from app-private storage, never placed in a URL or logs. */
    fun api(method: String, path: String, body: JSONObject? = null): JSONObject {
        val api = status()?.optString("api").orEmpty()
        require(api.matches(Regex("127\\.0\\.0\\.1:[0-9]{1,5}"))) { "Foxy API is not ready" }
        val token = File(dir, "local-api.token").readText().trim()
        require(token.isNotEmpty()) { "Foxy API token is unavailable" }
        val c = URL("http://$api$path").openConnection() as HttpURLConnection
        try {
            c.requestMethod = method; c.connectTimeout = 15000; c.readTimeout = 30000
            c.setRequestProperty("X-Local-Token", token)
            if (body != null) { c.doOutput = true; c.setRequestProperty("Content-Type", "application/json"); c.outputStream.use { it.write(body.toString().toByteArray()) } }
            val result = (if (c.responseCode in 200..299) c.inputStream else c.errorStream)?.bufferedReader()?.use { it.readText() }.orEmpty()
            val json = runCatching { JSONObject(result) }.getOrElse { JSONObject() }
            check(c.responseCode in 200..299) { json.optString("error", "Foxy API error ${c.responseCode}") }
            return json
        } finally { c.disconnect() }
    }
}
