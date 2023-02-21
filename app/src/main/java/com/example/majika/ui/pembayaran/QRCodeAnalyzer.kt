package com.example.majika.ui.pembayaran

import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

@ExperimentalGetImage class QRCodeAnalyzer(
    val scanSuccess: ScanSuccess
) : ImageAnalysis.Analyzer {
    override fun analyze(image: ImageProxy) {
        val img = image.image
        if (img != null) {
            val inputImage = InputImage.fromMediaImage(img, image.imageInfo.rotationDegrees)

            val options = BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build()
            val scanner = BarcodeScanning.getClient(options)

            scanner.process(inputImage)
                .addOnSuccessListener {
                    if (it.size > 0) {
                        scanSuccess.onScanSuccess(it[0])
                    }
                }
                .addOnFailureListener {
                    // Do nothing
                }

            img.close()
        }
        image.close()
    }

}

interface ScanSuccess {
    fun onScanSuccess(barcode: Barcode)
}
