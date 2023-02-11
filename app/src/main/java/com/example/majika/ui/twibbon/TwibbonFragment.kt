package com.example.majika.ui.twibbon

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.majika.databinding.FragmentTwibbonBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class TwibbonFragment : Fragment() {

    private var _binding: FragmentTwibbonBinding? = null

    private lateinit var cameraExecutor: ExecutorService

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
        return root
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
                val previewUseCase = buildPreviewUseCase()

                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, CameraSelector.DEFAULT_FRONT_CAMERA, previewUseCase)
            }
            catch(e: Exception){
            }
        }, ContextCompat.getMainExecutor(safeContext))
    }

    fun buildPreviewUseCase(): Preview {
        return Preview.Builder().build().also{ it.setSurfaceProvider(binding.viewFinder.surfaceProvider)}

    }
}