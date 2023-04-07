package fr.isen.tazibt.androidsmartdevice

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import fr.isen.tazibt.androidsmartdevice.databinding.ActivityScanBinding
import kotlin.Int as Int1


class ScanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScanBinding
    private var mScanning = false
    private val handler = android.os.Handler()
    private val REQUEST_ENABLE_BT = 1



    ////// Stops scanning after 10 seconds.//////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private val SCAN_PERIOD: Long = 10000
    private val bluetoothAdapter: BluetoothAdapter? by
    lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager =
            getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    private lateinit var adapter: ScanAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (bluetoothAdapter?.isEnabled == true) {
            scanDeviceWithPermission()
        } else {
            handleBLENotAvailable()
        }

    }

    private val leScanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int1, result: ScanResult) {

            super.onScanResult(callbackType, result)
            adapter.addDevice(result)
            adapter.notifyDataSetChanged()
        }
    }

    @SuppressLint("MissingPermission")
    private fun scanLeDevice() {
        if (!mScanning) { // Stops scanning after a pre-defined scan period.
            handler.postDelayed({
                mScanning = false
                bluetoothAdapter?.bluetoothLeScanner?.stopScan(leScanCallback)
                togglePlayPauseAction()
            }, SCAN_PERIOD)
            mScanning = true
            bluetoothAdapter?.bluetoothLeScanner?.startScan(leScanCallback)
        } else {
            mScanning = false
            bluetoothAdapter?.bluetoothLeScanner?.stopScan(leScanCallback)
        }
        togglePlayPauseAction()
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->

        val permig = permissions.all { it.value }

        if (permig) {
            initScanList()
            //scanLeDevice()
        } else {
            Log.e("No perm", "Il faut les perms")
        }
    }


    @SuppressLint("MissingPermission")
    override fun onStop() {
        super.onStop()
        if (bluetoothAdapter?.isEnabled == true && allPermissionGranted()) {
            mScanning = false
            bluetoothAdapter?.bluetoothLeScanner?.stopScan(leScanCallback)
        }
    }

    private fun handleBLENotAvailable() {
        Log.e("Bluetooth ","Non activé")

    }

    private fun scanDeviceWithPermission() {
        if (allPermissionGranted()) {
            initScanList()
            scanLeDevice()
        } else {
            ///request pour toutes les perms
            requestPermissionLauncher.launch(getAllPermissions())
        }

    }

    private fun initScanList() {
        binding.playPause.setOnClickListener {
            scanLeDevice()
        }
        binding.scanRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ScanAdapter(arrayListOf()) {
            val intent = Intent(this, DeviceActivity::class.java)
            intent.putExtra("cle", it)
            startActivity(intent)
        }
        binding.scanRecyclerView.adapter = adapter
    }

    private fun allPermissionGranted(): Boolean {
        val allPermission = getAllPermissions()
        return allPermission.all {
            ContextCompat.checkSelfPermission(
                this,
                it
            ) == PackageManager.PERMISSION_GRANTED

        }
    }


    private fun getAllPermissions(): Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN,
            )
        } else {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,

                )
        }

    }

    private fun togglePlayPauseAction() {
        if (mScanning) {
            binding.scanTitle.text = getString(R.string.ble_scan_title_pause)
            binding.playPause.setImageResource(R.drawable.baseline_pause_24)
            binding.progressBar.isVisible = true
        } else {
            binding.scanTitle.text = getString(R.string.ble_scan_title_play)
            binding.playPause.setImageResource(R.drawable.baseline_play_arrow_24)
            binding.progressBar.isVisible = false
        }

    }

}
