package com.example.majika.ui.twibbon

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.majika.R
import com.example.majika.databinding.FragmentTwibbonBinding
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class TwibbonFragment : Fragment() {

    private var _binding: FragmentTwibbonBinding? = null

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var imageCapture: ImageCapture
    private lateinit var safeContext: Context
    private var takePicture = true
    private var idxImageTwibbon = 0;
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

        binding.rightButton.setOnClickListener {
            changeTwibbon(true);
        }
        binding.leftButton.setOnClickListener {
            changeTwibbon(false);
        }

        return root
    }

    private fun changeTwibbon(right: Boolean){
        val twibbon :ImageView = binding.twibbon
        val array = arrayOf(R.drawable.twibbon1,R.drawable.twibbon2,R.drawable.twibbon3,R.drawable.twibbon4,R.drawable.twibbon5,R.drawable.twibbon6,R.drawable.twibbon7,R.drawable.twibbon8,R.drawable.twibbon9)
        if (right){
            idxImageTwibbon =(idxImageTwibbon +1 +array.size) % array.size
        }
        else {
            idxImageTwibbon = (idxImageTwibbon - 1 + array.size) % array.size
        }
        activity?.runOnUiThread(java.lang.Runnable {
            twibbon.setImageResource(array[idxImageTwibbon])

        })
    }
    private fun takePhoto(){
//        val imageOutput: ImageView = binding.imageOutput
        val textTakePicture: TextView = binding.textTakePicture
        if(!takePicture){
            activity?.runOnUiThread(java.lang.Runnable {
//                imageOutput.visibility = View.GONE
//                imageOutput.setImageBitmap(null)
                textTakePicture.setText(R.string.on_take)
            })
            takePicture = true
            startCamera()
        }
        else{
            val outputStream = ByteArrayOutputStream()
            // Create an OutputFileOptions object with the ByteArrayOutputStream
//            val outputFileOptions = ImageCapture.OutputFileOptions.Builder(outputStream).build()
            imageCapture.takePicture(cameraExecutor,
                object : ImageCapture.OnImageCapturedCallback() {
                    override fun onCaptureSuccess(image: ImageProxy){
                        val bitmap = imageProxyToBitmap(image)
                        super.onCaptureSuccess(image);

                    }

                    override fun onError(exception: ImageCaptureException) {
                        super.onError(exception)
                    }

//                object : ImageCapture.OnImageSavedCallback {
//                    override fun onError(error: ImageCaptureException)
//                    {
//
//                    }
//
//
//                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
//
//                        activity?.runOnUiThread(java.lang.Runnable {
//                            imageOutput.visibility = View.VISIBLE
//                            textTakePicture.setText(R.string.no_take)
//                            val byteArray = outputStream.toByteArray()
//
//                            // Create a Bitmap from the byte array
//                            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
//
//                            //flip image
//                            val matrix = Matrix()
//                            matrix.preScale(-1f, 1f)
//                            val flipBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
//                            // Display the Bitmap in the ImageView
//                            imageOutput.setImageBitmap(flipBitmap)
//
//                        })
//
//                    }
                })
            takePicture = false
            stopCamera()
        }
    }

    private fun imageProxyToBitmap(image: ImageProxy) : Bitmap{
        val planeProxy = image.planes[0]
        val buffer: ByteBuffer = planeProxy.buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        return BitmapFactory.decodeByteArray(bytes,0, bytes.size)
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

    private fun stopCamera(){
        val processCameraProvider = ProcessCameraProvider.getInstance(safeContext)
        processCameraProvider.addListener({
            try{
                val cameraProvider = processCameraProvider.get()
                cameraProvider.unbindAll()

            }
            catch(e: Exception){
            }
        }, ContextCompat.getMainExecutor(safeContext))
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