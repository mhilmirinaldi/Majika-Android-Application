package com.example.majika.ui.twibbon

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.opengl.Visibility
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
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
    private var front = true;
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

        binding.buttonToggle.setOnClickListener {
            toggleCamera()
        }

        return root
    }

    private fun changeTwibbon(right: Boolean){
        val twibbon :ImageView = binding.twibbon
        val textTwibbon: TextView = binding.textTwibbon

        val arrayText = arrayOf("Aku Masuk ITB!!!", "OSKM ITB 2022", "Wisjul 2021", "IMPACT", "Google Cuyy", "Spartans Init", "Yakin Mas", "OSKM ITB 2022", "Uro 2021")
        val array = arrayOf(R.drawable.twibbon1,R.drawable.twibbon2,R.drawable.twibbon3,R.drawable.twibbon4,R.drawable.twibbon5,R.drawable.twibbon6,R.drawable.twibbon7,R.drawable.twibbon8,R.drawable.twibbon9)
        if (right){
            idxImageTwibbon =(idxImageTwibbon +1 +array.size) % array.size
        }
        else {
            idxImageTwibbon = (idxImageTwibbon - 1 + array.size) % array.size
        }
        activity?.runOnUiThread(java.lang.Runnable {
            twibbon.setImageResource(array[idxImageTwibbon])
            textTwibbon.setText(arrayText[idxImageTwibbon])
        })
    }
    private fun takePhoto(){
        val textTakePicture: TextView = binding.textTakePicture
        val buttonToggleCamera: ImageButton = binding.buttonToggle
        if(!takePicture){
            activity?.runOnUiThread(java.lang.Runnable {
                textTakePicture.setText(R.string.on_take)
                buttonToggleCamera.visibility = View.VISIBLE
            })
            takePicture = true
            startCamera()
        }
        else{
            imageCapture.takePicture(cameraExecutor,
                object : ImageCapture.OnImageCapturedCallback() {
                    override fun onCaptureSuccess(image: ImageProxy){
                        val bitmap = imageProxyToBitmap(image)
                        super.onCaptureSuccess(image);

                    }

                    override fun onError(exception: ImageCaptureException) {
                        super.onError(exception)
                    }

                })
            takePicture = false
            activity?.runOnUiThread(java.lang.Runnable {
                textTakePicture.setText(R.string.no_take)
                buttonToggleCamera.visibility = View.GONE
            })
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
                if (front) {
                    cameraProvider.bindToLifecycle(
                        this, CameraSelector.DEFAULT_FRONT_CAMERA, preview, imageCapture
                    )
                }
                else{
                    cameraProvider.bindToLifecycle(
                        this, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageCapture
                    )
                }

            }
            catch(e: Exception){
            }
        }, ContextCompat.getMainExecutor(safeContext))


    }

    private fun toggleCamera(){
        stopCamera()
        front = !front
        startCamera()
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