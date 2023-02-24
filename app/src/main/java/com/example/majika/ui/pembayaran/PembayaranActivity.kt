package com.example.majika.ui.pembayaran

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.icu.text.NumberFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Size
import android.view.View
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.majika.MainActivity
import com.example.majika.R
import com.example.majika.database.Database
import com.example.majika.databinding.ActivityPembayaranBinding
import com.example.majika.network.BackendApiKeranjang
import com.example.majika.repository.KeranjangRepository
import com.example.majika.ui.keranjang.KeranjangFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.mlkit.vision.barcode.common.Barcode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

enum class State {
    SCANNING,
    CODE_DETECTED,
    PAYING,
    PAYMENT_SUCCESS,
    PAYMENT_FAILED
}

class PembayaranViewModel : ViewModel() {
    val state: MutableLiveData<State> = MutableLiveData(State.SCANNING)

    var detectedCode: String? = null
    var detailMessage: String = ""

    var currency: String = ""
    var hargaTotal: Float = 0.0F
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
        viewModel.currency = intent.getStringExtra(KeranjangFragment.CURRENCY_KEY) ?: ""
        viewModel.hargaTotal = intent.getFloatExtra(KeranjangFragment.HARGA_TOTAL_KEY, 0.0F)

        val formattedNumber = NumberFormat.getNumberInstance(resources.configuration.locales[0]).format(viewModel.hargaTotal)
        binding.pembayaranHargatotal.text = "Total Harga: ${viewModel.currency} ${formattedNumber}"

        viewModel.state.observe(this) {
            binding.pembayaranProgressbar.visibility = View.GONE
            binding.pembayaranTomenutext.visibility = View.GONE
            binding.pembayaranStatuscontainer.visibility = View.INVISIBLE
            binding.pembayaranButtonscontainer.visibility = View.GONE
            binding.pembayaranRetakeinfailed.visibility = View.GONE
            stopCamera()
            when (it) {
                State.SCANNING -> {
                    startCamera()
                }
                State.CODE_DETECTED -> {
                    binding.pembayaranButtonscontainer.visibility = View.VISIBLE
                }
                State.PAYING -> {
                    binding.pembayaranButtonscontainer.visibility = View.GONE
                    binding.pembayaranProgressbar.visibility = View.VISIBLE
                }
                State.PAYMENT_SUCCESS -> {
                    // Setting back to menu text
                    binding.pembayaranStatuscontainer.visibility = View.VISIBLE
                    binding.pembayaranStatustext.text = "Berhasil"
                    binding.pembayaranStatusdetail.text = "Sudah dibayar"
                    binding.pembayaranStatusicon.setImageResource(R.drawable.ic_ok)
                    binding.pembayaranTomenutext.visibility = View.VISIBLE

                    lifecycleScope.launch(Dispatchers.IO) {
                        // Clear keranjang
                        val repo = KeranjangRepository(Database.getDatabase(this@PembayaranActivity))
                        repo.deleteAllInKeranjang()

                        // Wait for 5 seconds before go to Menu
                        repeat(5) {
                            runOnUiThread {
                                binding.pembayaranTomenutext.text = "menuju Menu...(${5 - it})"
                            }
                            delay(1000)
                        }
                        runOnUiThread {
                            binding.pembayaranTomenutext.text = "menuju Menu...(0)"

                            // Go to Menu, clearing up the activity stack
                            val intent = Intent(this@PembayaranActivity, MainActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                    }
                }
                else -> {
                    binding.pembayaranStatuscontainer.visibility = View.VISIBLE
                    binding.pembayaranStatustext.text = "Gagal"
                    binding.pembayaranStatusdetail.text = viewModel.detailMessage
                    binding.pembayaranStatusicon.setImageResource(R.drawable.ic_cancel)

                    binding.pembayaranRetakeinfailed.visibility = View.VISIBLE
                }
            }
        }

        binding.pembayaranPay.setOnClickListener {
            viewModel.state.postValue(State.PAYING)
            lifecycleScope.launch {
                try {
                    val code = viewModel.detectedCode
                    if (code == null) {
                        throw Exception("Error: Payment code is empty")
                    }

                    val result = BackendApiKeranjang.keranjangApi.pay(code)
                    if (result.status == "SUCCESS") {
                        viewModel.state.postValue(State.PAYMENT_SUCCESS)
                    } else {
                        throw Exception("Belum dibayar")
                    }
                } catch (e: Exception) {
                    viewModel.detailMessage = e.message ?: "Belum dibayar"
                    viewModel.state.postValue(State.PAYMENT_FAILED)
                }
                viewModel.detectedCode = null
            }
        }

        binding.pembayaranRetake.setOnClickListener { viewModel.state.postValue(State.SCANNING) }
        binding.pembayaranRetakeinfailed.setOnClickListener { viewModel.state.postValue(State.SCANNING) }

        cameraExecutor = Executors.newSingleThreadExecutor()

        isCameraPermissionGranted()
    }

    override fun onDestroy() {
        super.onDestroy()

        cameraExecutor.shutdown()
    }

    private fun isCameraPermissionGranted() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            MaterialAlertDialogBuilder(this)
                .setTitle("Izin diperlukan")
                .setMessage("Aplikasi perlu akses ke kamera untuk memindai QR Code")
                .setPositiveButton("Siap") { _, _ ->
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

    private fun stopCamera() {
        val processCameraProvider = ProcessCameraProvider.getInstance(this)
        processCameraProvider.addListener({
            val cameraProvider = processCameraProvider.get()
            cameraProvider.unbindAll()
        }, ContextCompat.getMainExecutor(this))
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun startCamera() {
        val processCameraProvider = ProcessCameraProvider.getInstance(this)

        processCameraProvider.addListener({
            val cameraProvider = processCameraProvider.get()
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
        viewModel.detectedCode = barcode.rawValue
        viewModel.state.postValue(State.CODE_DETECTED)
    }
}
