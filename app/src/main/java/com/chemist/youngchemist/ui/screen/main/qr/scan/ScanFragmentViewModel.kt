package com.chemist.youngchemist.ui.screen.main.qr.scan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chemist.youngchemist.ui.screen.Screens
import com.chemist.youngchemist.ui.screen.main.qr.analyzer.BaseImageAnalyzer
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

    fun navigateToQrCodeScreen(qrCodeRawValue: String,lastSelectedItemPosition: Int) {
        router.replaceScreen(Screens.qrCodeFragmnet(qrCodeRawValue,lastSelectedItemPosition))
    }

    fun exit(lastSelectedItemPosition: Int) {
        router.newRootScreen(Screens.mainScreen(lastSelectedItemPosition))
    }
}