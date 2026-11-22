package com.whitedns.whiteaesther.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.whitedns.whiteaesther.R
import com.whitedns.whiteaesther.data.AppSettings
import com.whitedns.whiteaesther.data.Se7enMethod

@Composable
internal fun Se7enMethodChoices(settings: AppSettings, onChange: (AppSettings) -> Unit, onDone: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    OutlinedButton(onClick = { context.startActivity(android.content.Intent(context, com.whitedns.whiteaesther.FoxySetupActivity::class.java)) }) {
        Text(stringResource(R.string.foxy_account_setup))
    }
    Text(stringResource(R.string.se7en_candidate_heading))
    Text(stringResource(R.string.se7en_candidate_warning))
    Se7enMethod.entries.forEach { method ->
        val candidate = method.candidate(settings)
        OutlinedButton(
            modifier = Modifier.fillMaxWidth().padding(vertical = 1.dp),
            enabled = candidate != null,
            onClick = {
                if (candidate != null) { onChange(candidate); onDone() }
            },
        ) { Text(method.label + if (candidate == null) " — " + stringResource(R.string.se7en_unavailable) else "") }
    }
}
