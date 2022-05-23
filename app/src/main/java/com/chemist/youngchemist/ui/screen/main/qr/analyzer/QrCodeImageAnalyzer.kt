package com.chemist.youngchemist.ui.screen.main.qr.analyzer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

class QrCodeImageAnalyzer : BaseImageAnalyzer<List<Barcode>>() {

    private val _qrCodeId: MutableLiveData<String> = MutableLiveData<String>()
    val qrCodeId: LiveData<String> = _qrCodeId

    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            Barcode.FORMAT_QR_CODE,
            Barcode.FORMAT_AZTEC
        )
        .build()


    private val detector = BarcodeScanning.getClient(options)

    override fun detectInImage(image: InputImage): Task<List<Barcode>> {
        return detector.process(image)
    }

    override fun stop() {
        try {
            detector.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun onSuccess(results: List<Barcode>) {
        results.forEach { barCode ->
            barCode.rawValue?.let {
                _qrCodeId.postValue(it)
                stop()
                return
            }
        }
    }

    override fun onFailure(e: Exception) {
        e.printStackTrace()
    }
}