package mls.connections.sample.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mls.connections.sample.ConnectionsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetricsScreen(viewModel: ConnectionsViewModel, deviceName: String) {
    val strokeRate by viewModel.strokeRate.collectAsStateWithLifecycle()
    val powerWatts by viewModel.powerWatts.collectAsStateWithLifecycle()
    val splitSeconds by viewModel.splitSeconds.collectAsStateWithLifecycle()
    val distanceMeters by viewModel.distanceMeters.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text(deviceName) }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MetricCard(title = "Stroke Rate", value = strokeRate?.toString() ?: "—", unit = "spm", modifier = Modifier.weight(1f))
                MetricCard(title = "Split", value = splitSeconds?.takeIf { it > 0 }?.let { formatSplit(it) } ?: "—", unit = "/500m", modifier = Modifier.weight(1f))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MetricCard(title = "Power", value = powerWatts?.toString() ?: "—", unit = "W", modifier = Modifier.weight(1f))
                MetricCard(title = "Distance", value = distanceMeters?.let { "%.1f".format(it) } ?: "—", unit = "m", modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { viewModel.disconnect() },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Disconnect")
            }
        }
    }
}

@Composable
private fun MetricCard(title: String, value: String, unit: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = value, fontSize = 48.sp, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.onSurface)
            Text(text = unit, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

private fun formatSplit(seconds: Double): String {
    val totalSeconds = seconds.toInt()
    val minutes = totalSeconds / 60
    val secs = totalSeconds % 60
    return "%d:%02d".format(minutes, secs)
}
