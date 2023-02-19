package com.example.majika.ui.pembayaran

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Size
import android.view.View
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.majika.databinding.ActivityPembayaranBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.mlkit.vision.barcode.common.Barcode
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class PembayaranActivity : AppCompatActivity(), ScanSuccess {
    private lateinit var binding: ActivityPembayaranBinding
    private lateinit var cameraExecutor: ExecutorService
    private var codeDetected = false

    val REQUEST_CAMERA_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPembayaranBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.payButton.setOnClickListener {
            Toast.makeText(this, "Pembayaran dimulai...", Toast.LENGTH_SHORT).show()
        }
        binding.retakeButton.setOnClickListener {
            codeDetected = false
            binding.paymentDetectedContainer.visibility = View.GONE
        }

        cameraExecutor = Executors.newSingleThreadExecutor()

        isCameraPermissionGranted()
    }

    override fun onDestroy() {
        super.onDestroy()

        cameraExecutor.shutdown()
    }

    private fun isCameraPermissionGranted() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            MaterialAlertDialogBuilder(this)
                .setTitle("Izin diperlukan")
                .setMessage("Aplikasi perlu akses ke kamera untuk memindai QR Code")
                .setPositiveButton("Siap") {_, _ ->
                    requestCameraPermission()
                }
                .setCancelable(false)
                .create()
                .apply {
                    setCanceledOnTouchOutside(false)
                    show()
                }
        }
    }

    private fun requestCameraPermission() {
        try {
            val permissions = arrayOf(Manifest.permission.CAMERA)
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CAMERA_CODE)
        } catch (e: Exception) {
            isCameraPermissionGranted()
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun startCamera() {
        val processCameraProvider = ProcessCameraProvider.getInstance(this)

        processCameraProvider.addListener({
            val cameraProvider = processCameraProvider.get()
            if (codeDetected) {
                cameraProvider.unbindAll()
            }

            val preview = Preview.Builder().build()
                .apply {
                    setSurfaceProvider(binding.barcodePreview.surfaceProvider)
                }

            val imageAnalyzer = ImageAnalysis.Builder()
                .setTargetResolution(Size(1080, 1920))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .apply {
                    setAnalyzer(cameraExecutor, QRCodeAnalyzer(this@PembayaranActivity))
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    override fun onScanSuccess(barcode: Barcode) {
        codeDetected = true
        binding.paymentDetectedContainer.visibility = View.VISIBLE
        binding.paymentCode.text = barcode.rawValue
    }
}