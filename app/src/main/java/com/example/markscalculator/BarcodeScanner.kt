package com.example.markscalculator

import android.content.Context
import android.util.Log
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.tasks.await

class BarcodeScanner(
    appContext: Context
) {
    private val options = GmsBarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            Barcode.FORMAT_ALL_FORMATS
        )
        .build()

    private val scanner = GmsBarcodeScanning.getClient(appContext, options)
    private val barCodeResults = MutableStateFlow<String?>(null)

    suspend fun startScan(done: (result: String?) -> Unit) {
        try {
            val result = scanner.startScan().await()
            barCodeResults.value = result.rawValue
            done(result.rawValue)
        } catch (e: Exception) {
            Log.i("scan error", "$e")
        }
    }

    /* alt:
    scanner.startScan()
    .addOnSuccessListener { barcode ->
        // Task completed successfully
    }
    .addOnCanceledListener {
        // Task canceled
    }
    .addOnFailureListener { e ->
        // Task failed with an exception
    }*/

}