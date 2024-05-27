package com.example.myapplication

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.telephony.TelephonyManager
import android.telephony.CellInfo
import android.telephony.CellInfoLte
import android.telephony.CellSignalStrengthLte
import android.telephony.CellIdentityLte
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private lateinit var telephonyManager: TelephonyManager
    private val PERMISSION_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_PHONE_STATE
            ), PERMISSION_REQUEST_CODE)
        } else {
            displayTelephonyMetrics()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            displayTelephonyMetrics()
        }
    }

    private fun displayTelephonyMetrics() {
        telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager

        val operatorName = telephonyManager.networkOperatorName  // this return the operator name
        val mnc = telephonyManager.networkOperator.substring(3)
        val mcc = telephonyManager.networkOperator.substring(0, 3)
        val volteSupported = telephonyManager.isVoiceCapable

        var dataConnectivityType = "Unknown"
        when (telephonyManager.dataNetworkType) {
            //change this to getDataNetworkType
            TelephonyManager.NETWORK_TYPE_LTE -> dataConnectivityType = "LTE"
            TelephonyManager.NETWORK_TYPE_UMTS -> dataConnectivityType = "3G"
            TelephonyManager.NETWORK_TYPE_EDGE -> dataConnectivityType = "EDGE"
            TelephonyManager.NETWORK_TYPE_NR -> dataConnectivityType = "5G"
            // Add other types as necessary
        }

        //chnage to getAllCellInfo
        val cellInfoList = telephonyManager.allCellInfo ?: emptyList()
        val metrics = StringBuilder()

        for (cellInfo in cellInfoList) {
            if (cellInfo is CellInfoLte) {
                //chnage to getAllCellInfo
                val cellSignalStrengthLte: CellSignalStrengthLte = cellInfo.cellSignalStrength
                val cellIdentityLte: CellIdentityLte = cellInfo.cellIdentity

                val eNbid = cellIdentityLte.ci
                val cid = cellIdentityLte.ci
                val lac = cellIdentityLte.tac
                val rssi = cellSignalStrengthLte.dbm
                val rsrp = cellSignalStrengthLte.rsrp
                val rsrq = cellSignalStrengthLte.rsrq
                val snr = cellSignalStrengthLte.rssnr
                // For NR (5G) metrics, you'd use a similar process with CellInfoNr and related classes

                metrics.append("eNbid: $eNbid\n")
                metrics.append("CID: $cid\n")
                metrics.append("LAC: $lac\n")
                metrics.append("RSSI: $rssi\n")
                metrics.append("RSRP: $rsrp\n")
                metrics.append("RSRQ: $rsrq\n")
                metrics.append("SNR: $snr\n")
                // Append other metrics as needed
            }
        }

        // Display the collected metrics
        val metricsTextView: TextView = findViewById(R.id.metricsTextView)
        metricsTextView.text = """
            Operator Name: $operatorName
            MNC: $mnc
            MCC: $mcc
            VoLTE Supported: $volteSupported
            Data Connectivity Type: $dataConnectivityType
            Metrics:
            $metrics
        """.trimIndent()
    }
}
