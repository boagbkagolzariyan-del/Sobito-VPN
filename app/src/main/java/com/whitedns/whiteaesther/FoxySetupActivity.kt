package com.whitedns.whiteaesther

import android.app.Activity
import android.os.Bundle
import android.text.InputType
import android.view.WindowManager
import android.widget.*
import android.graphics.BitmapFactory
import android.util.Base64
import com.whitedns.whiteaesther.core.FoxyRuntime
import org.json.JSONObject

/** Minimal account setup for the experimental Foxy helper. Credentials are not logged. */
class FoxySetupActivity : Activity() {
    private lateinit var status: TextView
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var answer: EditText
    private lateinit var captcha: ImageView
    private var id: String? = null
    @Volatile private var alive = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        val layout = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(32, 32, 32, 32) }
        status = TextView(this).apply { text = "ورود به حساب Mozilla دارای اشتراک Firefox VPN. اتصال آزمایشی است." }
        email = EditText(this).apply { hint = "ایمیل Firefox"; inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS }
        password = EditText(this).apply { hint = "رمز عبور Firefox"; inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD }
        answer = EditText(this).apply { hint = "کد تأیید / پاسخ تصویر" }
        captcha = ImageView(this)
        val login = Button(this).apply { text = "ورود"; setOnClickListener { signIn() } }
        val submit = Button(this).apply { text = "ارسال کد تأیید"; setOnClickListener { submitAnswer() } }
        listOf(status,email,password,login,captcha,answer,submit).forEach(layout::addView)
        setContentView(ScrollView(this).apply { addView(layout) })
        work { FoxyRuntime.ensureStarted(applicationContext); waitReady(); show("هسته آماده است؛ ایمیل و رمز حساب Firefox VPN را وارد کنید.") }
    }
    private fun waitReady() { repeat(100) { if (!FoxyRuntime.running()) error("هسته Foxy اجرا نشد"); if (!FoxyRuntime.status()?.optString("api").isNullOrBlank()) return; Thread.sleep(200) }; error("آماده‌سازی Foxy طولانی شد") }
    private fun signIn() {
        val e = email.text.toString().trim(); val p = password.text.toString(); password.text.clear()
        if (e.isBlank() || p.isBlank()) { show("ایمیل و رمز عبور را وارد کنید."); return }
        work { waitReady(); val result = FoxyRuntime.api("POST", "/api/signin", JSONObject().put("email", e).put("password", p)); id = result.optString("id"); poll() }
    }
    private fun submitAnswer() {
        val value = answer.text.toString(); answer.text.clear(); val session = id ?: return
        work { FoxyRuntime.api("POST", "/api/signin/$session", JSONObject().put("value", value)); poll() }
    }
    private fun poll() {
        val session = id ?: return
        while (alive) {
            val state = FoxyRuntime.api("GET", "/api/signin/$session")
            val step = state.optString("step")
            val message = state.optString("message").ifBlank { state.optString("error").ifBlank { step } }
            show("وضعیت ورود: $message")
            if (step == "need_captcha") {
                val data = state.optString("captcha").substringAfter(",", "")
                val bitmap = runCatching { val b = Base64.decode(data, Base64.DEFAULT); BitmapFactory.decodeByteArray(b, 0, b.size) }.getOrNull()
                runOnUiThread { captcha.setImageBitmap(bitmap) }
            }
            when (step) {
                "done" -> { show("حساب اضافه شد. حالا روش Psiphon over Foxy را انتخاب کنید."); return }
                "failed", "cancelled", "need_email_code", "need_totp", "need_unblock_code", "need_captcha", "need_email_link" -> return
            }
            Thread.sleep(1200)
        }
    }
    private fun show(text: String) { runOnUiThread { status.text = text } }
    private fun work(block: () -> Unit) { Thread { try { block() } catch (e: Exception) { show("خطا: ${e.message ?: "عملیات ناموفق بود"}") } }.start() }
    override fun onDestroy() { alive = false; super.onDestroy() }
}
