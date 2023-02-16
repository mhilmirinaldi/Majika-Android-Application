package com.example.majika.ui.twibbon

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.majika.databinding.FragmentTwibbonBinding
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.OutputStream
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class TwibbonFragment : Fragment() {

    private var _binding: FragmentTwibbonBinding? = null

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var imageCapture: ImageCapture
    private lateinit var safeContext: Context
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        safeContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {
        _binding = FragmentTwibbonBinding.inflate(inflater, container, false)

        val root: View = binding.root
        cameraExecutor = Executors.newSingleThreadExecutor()
        requestPermissions()

        binding.buttonTakePicture.setOnClickListener {
            takePhoto()
        }

        return root
    }

    private fun takePhoto(){
        val outputStream = ByteArrayOutputStream()

// Create an OutputFileOptions object with the ByteArrayOutputStream
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(outputStream).build()

        val imageoutput: ImageView = binding.imageOutput
        imageCapture.takePicture(outputFileOptions, cameraExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(error: ImageCaptureException)
                {

                }
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {

                    activity?.runOnUiThread(java.lang.Runnable {
                        imageoutput.visibility = View.VISIBLE
                        val byteArray = outputStream.toByteArray()

                        // Create a Bitmap from the byte array
                        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)

                        // Display the Bitmap in the ImageView
                        imageoutput.setImageBitmap(bitmap)
                    })

                }
            })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun requestPermissions(){
        requestCameraPermissionIfMissing { granted ->
            if (granted)
                startCamera()
            else{
                Toast.makeText(safeContext,
                    "We have Permission",
                    Toast.LENGTH_SHORT).show()
            }

        }
    }
    private fun requestCameraPermissionIfMissing(onResult: ((Boolean)-> Unit)){
        if(ContextCompat.checkSelfPermission(safeContext, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED)
            onResult(true)
        else
            registerForActivityResult(ActivityResultContracts.RequestPermission()){
                onResult(it)
            }.launch(Manifest.permission.CAMERA)

    }


    private fun startCamera(){
        val processCameraProvider = ProcessCameraProvider.getInstance(safeContext)
        processCameraProvider.addListener({
            try{
                val cameraProvider = processCameraProvider.get()
                val preview = buildPreviewUseCase()
                imageCapture = buildImageCapture()
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, CameraSelector.DEFAULT_FRONT_CAMERA, preview, imageCapture
                )
            }
            catch(e: Exception){
            }
        }, ContextCompat.getMainExecutor(safeContext))


    }
    fun buildImageCapture(): ImageCapture{
        return ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
    }
    fun buildPreviewUseCase(): Preview {
        return Preview.Builder().build().also{ it.setSurfaceProvider(binding.viewFinder.surfaceProvider)}

    }
}