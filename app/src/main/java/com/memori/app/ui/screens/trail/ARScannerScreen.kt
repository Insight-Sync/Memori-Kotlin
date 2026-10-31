package com.memori.app.ui.screens.trail

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.ar.core.AugmentedImage
import com.google.ar.core.AugmentedImageDatabase
import com.google.ar.core.Config
import com.google.ar.core.TrackingState
import io.github.sceneview.ar.ARSceneView
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.node.ModelNode
import kotlinx.coroutines.launch

@Composable
fun ARScannerScreen(onFinishedAR: () -> Unit) {
    val context = LocalContext.current
    var hasCameraPermission by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        hasCameraPermission = it
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) launcher.launch(Manifest.permission.CAMERA)
    }

    var isObjectDetected by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        if (hasCameraPermission) {
            AndroidView(
                factory = { ctx ->
                    ARSceneView(ctx).apply {
                        // Limpa logs de erro de toque/chão

                        configureSession { session, config ->
                            val database = AugmentedImageDatabase(session)
                            try {
                                context.assets.open("marker.jpg").use { isr ->
                                    val bitmap = BitmapFactory.decodeStream(isr)
                                    // 0.2f = 20cm físico. Essencial para o 3D aparecer.
                                    database.addImage("monumento", bitmap, 0.2f)
                                    Log.d("AR_MEMORI", "DATABASE: Marcador marker.jpg carregado")
                                }
                            } catch (e: Exception) {
                                Log.e("AR_MEMORI", "DATABASE: Erro ao carregar marcador", e)
                            }
                            config.augmentedImageDatabase = database
                            config.focusMode = Config.FocusMode.AUTO

                            // Desativa o que causa erro no Logcat
                            config.planeFindingMode = Config.PlaneFindingMode.DISABLED
                            config.depthMode = Config.DepthMode.DISABLED
                            config.lightEstimationMode = Config.LightEstimationMode.DISABLED
                        }

                        onSessionUpdated = { _, frame ->
                            val augmentedImages = frame.getUpdatedTrackables(AugmentedImage::class.java)
                            for (image in augmentedImages) {
                                if (image.trackingState == TrackingState.TRACKING && !isObjectDetected) {
                                    Log.d("AR_MEMORI", "DETECÇÃO: Imagem reconhecida!")
                                    isObjectDetected = true

                                    scope.launch {
                                        try {
                                            Log.d("AR_MEMORI", "MODELO: Tentando carregar model.glb")
                                            val modelInstance = modelLoader.loadModelInstance("model.glb")
                                            if (modelInstance != null) {
                                                val modelNode = ModelNode(
                                                    modelInstance = modelInstance,
                                                    scaleToUnits = 0.3f,
                                                    centerOrigin = io.github.sceneview.math.Position(0f, 0f, 0f)
                                                ).apply {
                                                    rotation = io.github.sceneview.math.Rotation(x = -90f)
                                                }

                                                val anchor = image.createAnchor(image.centerPose)
                                                val anchorNode = AnchorNode(engine, anchor)
                                                anchorNode.addChildNode(modelNode)
                                                addChildNode(anchorNode)
                                                Log.d("AR_MEMORI", "SUCESSO: Objeto 3D renderizado!")
                                            } else {
                                                Log.e("AR_MEMORI", "ERRO: modelInstance nulo. O arquivo .glb pode estar com problema.")
                                                isObjectDetected = false
                                            }
                                        } catch (e: Exception) {
                                            Log.e("AR_MEMORI", "ERRO: Falha crítica na renderização", e)
                                            isObjectDetected = false
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            // Interface
            Box(modifier = Modifier.fillMaxSize()) {
                if (!isObjectDetected) {
                    Box(
                        modifier = Modifier
                            .size(260.dp)
                            .align(Alignment.Center)
                            .border(2.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    )
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 120.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        color = Color.Black.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text(
                            if (isObjectDetected) "3D Carregado!" else "Aponte para o marcador",
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (isObjectDetected) {
                        Spacer(Modifier.height(24.dp))
                        Button(
                            onClick = onFinishedAR,
                            modifier = Modifier.height(56.dp).width(220.dp),
                            shape = RoundedCornerShape(28.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                        ) {
                            Text("Continuar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
