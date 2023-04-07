package fr.isen.tazibt.androidsmartdevice

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.*
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import fr.isen.tazibt.androidsmartdevice.databinding.ActivityDeviceBinding
import java.util.*


class DeviceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDeviceBinding
    private var bluetoothGatt: BluetoothGatt? = null
    private var ledCharacteristic: BluetoothGattCharacteristic? = null
    private var clickCharacteristic: BluetoothGattCharacteristic? = null
    private val serviceUUID = UUID.fromString("0000feed-cc7a-482a-984a-7f2ed5b3e58f")
    private val characteristicLedUUID = UUID.fromString("0000abcd-8e22-4541-9d4c-21edae82ed19")

    private var clickLed1 = false

    @SuppressLint("MissingInflatedId", "MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeviceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Récupérer l'appareil sélectionné dans la liste
        val device: BluetoothDevice = intent.getParcelableExtra<BluetoothDevice>("cle")!!

        // Se connecter à l'appareil

        bluetoothGatt = device?.connectGatt(this, false, gattCallback)

        //bluetoothGatt?.connect()

        // Afficher les différentes actions


       /* findViewById<Button>(R.id.click_button).setOnClickListener {
            // Vérifier que la caractéristique du nombre de clics est disponible
            if (clickCharacteristic == null) {
                Toast.makeText(
                    this,
                    "Caractéristique du nombre de clics non disponible",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                // Lire le nombre de clics
                bluetoothGatt.readCharacteristic(clickCharacteristic)
            }
        }*/
    }

    @SuppressLint("MissingPermission")
    private fun cliqueLed() {
        binding.LED1.setOnClickListener {
            // Vérifier que la caractéristique des LEDs est disponible
            clickLed1 = !clickLed1

            val characteristic = bluetoothGatt?.getService(serviceUUID)?.getCharacteristic(characteristicLedUUID)

            if (clickLed1) {
                characteristic?.value = byteArrayOf(0x01)
                if (characteristic != null) { // Add a null-check here
                    bluetoothGatt?.writeCharacteristic(characteristic)
                }
            } else {
                // éteindre les LEDs
                characteristic?.value = byteArrayOf(0x00)
                if (characteristic != null) { // Add a null-check here
                    bluetoothGatt?.writeCharacteristic(characteristic)
                }
            }
        }

        binding.LED2.setOnClickListener {
            // Vérifier que la caractéristique des LEDs est disponible
            clickLed1 = !clickLed1

            val characteristic = bluetoothGatt?.getService(serviceUUID)?.getCharacteristic(characteristicLedUUID)

            if (clickLed1) {
                characteristic?.value = byteArrayOf(0x02)
                if (characteristic != null) { // Add a null-check here
                    bluetoothGatt?.writeCharacteristic(characteristic)
                }
            } else {
                // éteindre les LEDs
                characteristic?.value = byteArrayOf(0x00)
                if (characteristic != null) { // Add a null-check here
                    bluetoothGatt?.writeCharacteristic(characteristic)
                }
            }
        }

        binding.LED3.setOnClickListener {
            // Vérifier que la caractéristique des LEDs est disponible
            clickLed1 = !clickLed1

            val characteristic = bluetoothGatt?.getService(serviceUUID)?.getCharacteristic(characteristicLedUUID)

            if (clickLed1) {
                characteristic?.value = byteArrayOf(0x03)
                if (characteristic != null) { // Add a null-check here
                    bluetoothGatt?.writeCharacteristic(characteristic)
                }
            } else {
                // éteindre les LEDs
                characteristic?.value = byteArrayOf(0x00)
                if (characteristic != null) { // Add a null-check here
                    bluetoothGatt?.writeCharacteristic(characteristic)
                }
            }
        }
    }


    private val gattCallback = object : BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                // Découvrir les services et les caractéristiques
                Log.e("aa", "on est connecté")
                bluetoothGatt?.discoverServices()
                runOnUiThread{
                    cliqueLed()
                }


            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.e("aa", "on est pas connecté")

            }


        }
    }
}/*

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)

            if (status == BluetoothGatt.GATT_SUCCESS) {
                // Récupérer les caractéristiques
                val service3: BluetoothGattService? =
                    gatt?.getService(UUID.fromString("service3_uuid"))
                val service4: BluetoothGattService? =
                    gatt?.getService(UUID.fromString("service4_uuid"))

                if (service3 != null) {
                    ledCharacteristic =
                        service3.getCharacteristic(UUID.fromString("led_characteristic_uuid"))
                }

                if (service4 != null) {
                    clickCharacteristic =
                        service4.getCharacteristic(UUID.fromString("click_characteristic_uuid"))
                }
            }
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            super.onCharacteristicRead(gatt, characteristic, status)

            if (status == BluetoothGatt.GATT_SUCCESS) {
                // Afficher le nombre de clics
                val value = characteristic?.value
                val clicks = value?.get(0)?.toInt()
                Toast.makeText(this@DeviceActivity, "Nombre de clics : $clicks", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            super.onCharacteristicWrite(gatt, characteristic, status)

            if (status == BluetoothGatt.GATT_SUCCESS) {
                // LEDs allumées

            }
        }
    }
}*/