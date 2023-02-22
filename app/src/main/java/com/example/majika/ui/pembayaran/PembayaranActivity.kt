package com.example.majika.ui.pembayaran

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Size
import android.view.View
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.majika.R
import com.example.majika.databinding.ActivityPembayaranBinding
import com.example.majika.network.BackendApi
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.mlkit.vision.barcode.common.Barcode
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class PembayaranViewModel : ViewModel() {
    var detectedCode: MutableLiveData<String?> = MutableLiveData(null)
}

class PembayaranActivity : AppCompatActivity(), ScanSuccess {


    private lateinit var binding: ActivityPembayaranBinding
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var viewModel: PembayaranViewModel

    val REQUEST_CAMERA_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPembayaranBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setBackgroundDrawable(ColorDrawable(getColor(R.color.navbar)))

        viewModel = ViewModelProvider(this).get(PembayaranViewModel::class.java)

        viewModel.detectedCode.observe( this, {
            if (it != null) {
                binding.paymentCode.text = it
                binding.paymentDetectedContainer.visibility = View.VISIBLE
            } else {
                binding.paymentCode.text = ""
                binding.paymentDetectedContainer.visibility = View.GONE
            }
        })

        binding.payButton.setOnClickListener {
            Toast.makeText(this, "Pembayaran dimulai...", Toast.LENGTH_SHORT).show()
            viewModel.viewModelScope.launch {
                try {
                    val code = viewModel.detectedCode.value
                    if (code == null) {
                        throw Exception("Error: Payment code is empty")
                    }

                    val result = BackendApi.service.pay(code)
                    Toast.makeText(this@PembayaranActivity, result.status, Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    Toast.makeText(this@PembayaranActivity, e.message, Toast.LENGTH_LONG).show()
                }
                viewModel.detectedCode.postValue(null)
            }
        }

        binding.retakeButton.setOnClickListener {
            viewModel.detectedCode.postValue(null)
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
            if (viewModel.detectedCode.value != null) {
                cameraProvider.unbindAll()
            } else {
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
            }
        }, ContextCompat.getMainExecutor(this))
    }

    override fun onScanSuccess(barcode: Barcode) {
        viewModel.detectedCode.postValue(barcode.rawValue)
        binding.paymentDetectedContainer.visibility = View.VISIBLE
    }
}