package mls.connections.sample.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mls.connections.DeviceConnectionStatus
import mls.connections.DiscoveredDevice
import mls.connections.sample.ConnectionsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceListScreen(viewModel: ConnectionsViewModel) {
    val devices by viewModel.discoveredDevices.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Rowing Devices") }) }
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            if (devices.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator()
                    Text("Scanning for devices...")
                }
            } else {
                LazyColumn {
                    items(devices, key = { it.device.id.value }) { device ->
                        DeviceRow(device = device, onTap = { viewModel.connect(device) })
                        HorizontalDivider()
                    }
                }
            }
        }

        if (errorMessage != null) {
            AlertDialog(
                onDismissRequest = { viewModel.clearError() },
                title = { Text("Error") },
                text = { Text(errorMessage!!) },
                confirmButton = {
                    TextButton(onClick = { viewModel.clearError() }) { Text("OK") }
                }
            )
        }
    }
}

@Composable
private fun DeviceRow(device: DiscoveredDevice, onTap: () -> Unit) {
    val isConnecting = device.connectionStatus is DeviceConnectionStatus.Connecting
    val statusText = when (device.connectionStatus) {
        is DeviceConnectionStatus.Disconnected -> "Disconnected"
        is DeviceConnectionStatus.Connecting -> "Connecting..."
        is DeviceConnectionStatus.Connected -> "Connected"
        is DeviceConnectionStatus.Failed -> "Failed"
        else -> "Unknown"
    }

    ListItem(
        headlineContent = { Text(device.device.name ?: "Unknown Device") },
        supportingContent = { Text(statusText) },
        trailingContent = {
            if (isConnecting) CircularProgressIndicator(modifier = Modifier.size(24.dp))
        },
        modifier = Modifier.clickable(enabled = !isConnecting) { onTap() }
    )
}
