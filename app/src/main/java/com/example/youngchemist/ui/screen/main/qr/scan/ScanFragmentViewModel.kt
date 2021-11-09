package com.example.youngchemist.ui.screen.main.qr.scan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youngchemist.ui.screen.Screens
import com.example.youngchemist.ui.screen.main.qr.analyzer.BaseImageAnalyzer
import com.github.terrakok.cicerone.Router
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ScanFragmentViewModel @Inject constructor(
    private val router: Router
) : ViewModel() {

    fun navigateToMainScreen(qrCodeRawValue: String) {
        router.navigateTo(Screens.mainScreen(qrCodeRawValue))
    }


    class QrCodeImageAnalyzer() : BaseImageAnalyzer<List<Barcode>>() {

        private val _qrCode: MutableLiveData<String> = MutableLiveData<String>()
        val qrCode: LiveData<String> = _qrCode

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
            results.forEach {
                it.rawValue?.let {
                    _qrCode.postValue(it)
                    stop()
                    return
                }
            }
        }

        override fun onFailure(e: Exception) {
            e.printStackTrace()
        }
    }
}