package mls.connections.sample

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mls.connections.ConnectionsModule
import mls.connections.DiscoveredDevice
import mls.connections.capability.rowing.RowingCapability
import mls.connections.capability.rowing.distanceUpdates
import mls.connections.capability.rowing.drive
import mls.connections.capability.rowing.rowerDataSource
import mls.connections.create

class ConnectionsViewModel(application: Application) : AndroidViewModel(application) {

    private val module = ConnectionsModule.create(application) {
        add(capability = RowingCapability(application))
        rememberConnections()
        if (BuildConfig.DEBUG) {
            includeFakes()
        }
    }

    private val _discoveredDevices = MutableStateFlow<List<DiscoveredDevice>>(emptyList())
    val discoveredDevices: StateFlow<List<DiscoveredDevice>> = _discoveredDevices.asStateFlow()

    private val _connectedDevice = MutableStateFlow<DiscoveredDevice?>(null)
    val connectedDevice: StateFlow<DiscoveredDevice?> = _connectedDevice.asStateFlow()

    private val _strokeRate = MutableStateFlow<Int?>(null)
    val strokeRate: StateFlow<Int?> = _strokeRate.asStateFlow()

    private val _powerWatts = MutableStateFlow<Int?>(null)
    val powerWatts: StateFlow<Int?> = _powerWatts.asStateFlow()

    private val _splitSeconds = MutableStateFlow<Double?>(null)
    val splitSeconds: StateFlow<Double?> = _splitSeconds.asStateFlow()

    private val _distanceMeters = MutableStateFlow<Double?>(null)
    val distanceMeters: StateFlow<Double?> = _distanceMeters.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var scanJob: Job? = null
    private var driveJob: Job? = null
    private var distanceJob: Job? = null

    fun startScanning() {
        scanJob?.cancel()
        scanJob = viewModelScope.launch {
            module.deviceListProvider.devices.collect { devices ->
                _discoveredDevices.value = devices
            }
        }
    }

    fun connect(device: DiscoveredDevice) {
        viewModelScope.launch {
            module.connectionManager.connect(device = device.device)
            _connectedDevice.value = device
            startMetricListeners()
        }
    }

    fun disconnect() {
        viewModelScope.launch {
            stopMetricListeners()
            _connectedDevice.value?.let { device ->
                module.connectionManager.forget(deviceId = device.device.id)
            }
            _connectedDevice.value = null
            resetMetrics()
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun setError(message: String) {
        _errorMessage.value = message
    }

    private fun startMetricListeners() {
        val rowerFlow = module.rowerDataSource()

        driveJob = viewModelScope.launch {
            rowerFlow.drive().collect { drive ->
                _strokeRate.value = drive.strokeRate?.strokesPerMinute?.toInt()
                _powerWatts.value = drive.power.watts.toInt()
                _splitSeconds.value = drive.pace?.split500m?.seconds
            }
        }

        distanceJob = viewModelScope.launch {
            rowerFlow.distanceUpdates().collect { distance ->
                _distanceMeters.value = (_distanceMeters.value ?: 0.0) + distance.meters
            }
        }
    }

    private fun stopMetricListeners() {
        driveJob?.cancel()
        distanceJob?.cancel()
        driveJob = null
        distanceJob = null
    }

    private fun resetMetrics() {
        _strokeRate.value = null
        _powerWatts.value = null
        _splitSeconds.value = null
        _distanceMeters.value = null
    }

    override fun onCleared() {
        super.onCleared()
        scanJob?.cancel()
        stopMetricListeners()
    }
}
