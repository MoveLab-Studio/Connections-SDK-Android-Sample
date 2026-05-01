package mls.connections.sample

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.BackHandler
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mls.connections.sample.ui.DeviceListScreen
import mls.connections.sample.ui.MetricsScreen
import mls.connections.sample.ui.theme.ConnectionsSampleTheme

class MainActivity : ComponentActivity() {

    private val viewModel: ConnectionsViewModel by viewModels()

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        if (results.values.all { it }) {
            viewModel.startScanning()
        } else {
            viewModel.setError("Bluetooth permissions are required to scan for rowing machines.")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestBluetoothPermissions()
        setContent {
            ConnectionsSampleTheme {
                val connectedDevice by viewModel.connectedDevice.collectAsStateWithLifecycle()
                if (connectedDevice != null) {
                    BackHandler { viewModel.disconnect() }
                    MetricsScreen(
                        viewModel = viewModel,
                        deviceName = connectedDevice?.device?.name ?: "Rowing Machine"
                    )
                } else {
                    DeviceListScreen(viewModel = viewModel)
                }
            }
        }
    }

    private fun requestBluetoothPermissions() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
            )
        } else {
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        permissionLauncher.launch(permissions)
    }
}
