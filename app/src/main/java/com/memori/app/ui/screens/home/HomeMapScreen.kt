package com.memori.app.ui.screens.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.Path
import android.graphics.drawable.BitmapDrawable
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.memori.app.R
import com.memori.app.ui.screens.auth.LocalAuthViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

@Composable
fun HomeMapScreen(
    onNavigateToProfile: () -> Unit,
    onOpenTrail: () -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val authViewModel = LocalAuthViewModel.current

    val usuario by authViewModel.usuario.collectAsState()
    val avatarAtual = usuario?.avatar

    val nomeCurto = usuario?.nome
        ?.trim()
        ?.substringBefore(" ")
        ?.takeIf { it.isNotBlank() }
        ?: "Perfil"

    val abrirTrilhaAtual by rememberUpdatedState(onOpenTrail)

    val localKKKK = remember {
        GeoPoint(-24.487632, -47.840771)
    }

    val localGuaracui = remember {
        GeoPoint(-24.486953, -47.840548)
    }

    val localLanterna = remember {
        GeoPoint(-24.487176, -47.840396)
    }

    val localUsuarioMock = remember {
        GeoPoint(-24.488123, -47.839913)
    }

    val mapView = remember(context) {
        Configuration.getInstance().load(
            context,
            context.getSharedPreferences(
                "osmdroid",
                Context.MODE_PRIVATE
            )
        )

        Configuration.getInstance().userAgentValue = context.packageName

        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            setBuiltInZoomControls(false)

            controller.setZoom(19.0)
            controller.setCenter(localKKKK)

            val saturacao = ColorMatrix().apply {
                setSaturation(0.45f)
            }

            val clarear = ColorMatrix(
                floatArrayOf(
                    0.65f, 0f, 0f, 0f, 85f,
                    0f, 0.65f, 0f, 0f, 85f,
                    0f, 0f, 0.65f, 0f, 85f,
                    0f, 0f, 0f, 1f, 0f
                )
            )

            clarear.postConcat(saturacao)

            overlayManager.tilesOverlay.setColorFilter(
                ColorMatrixColorFilter(clarear)
            )
        }
    }

    val marcadorKKKK = remember(mapView) {
        Marker(mapView).apply {
            position = localKKKK

            icon = criarIconeDoMapa(
                context = context,
                recurso = R.drawable.kkkk,
                larguraDp = 280,
                alturaDp = 130
            )

            setAnchor(
                Marker.ANCHOR_CENTER,
                Marker.ANCHOR_CENTER
            )

            setOnMarkerClickListener { _, _ ->
                Toast.makeText(
                    context,
                    "Conjunto KKKK",
                    Toast.LENGTH_SHORT
                ).show()
                true
            }
        }
    }

    val marcadorGuaracui = remember(mapView) {
        Marker(mapView).apply {
            position = localGuaracui

            icon = criarIconeDoMapa(
                context = context,
                recurso = R.drawable.guaracui,
                larguraDp = 100,
                alturaDp = 130
            )

            setAnchor(
                Marker.ANCHOR_CENTER,
                Marker.ANCHOR_BOTTOM
            )

            setOnMarkerClickListener { _, _ ->
                Toast.makeText(
                    context,
                    "Guaracui",
                    Toast.LENGTH_SHORT
                ).show()
                true
            }
        }
    }

    val marcadorLanterna = remember(mapView) {
        Marker(mapView).apply {
            position = localLanterna

            icon = criarIconeDoMapa(
                context = context,
                recurso = R.drawable.lanterna_nao_iniciado,
                larguraDp = 100,
                alturaDp = 125
            )

            setAnchor(
                Marker.ANCHOR_CENTER,
                Marker.ANCHOR_CENTER
            )

            setOnMarkerClickListener { _, _ ->
                abrirTrilhaAtual()
                true
            }
        }
    }

    val marcadorUsuario = remember(mapView) {
        Marker(mapView).apply {
            position = localUsuarioMock

            setAnchor(
                Marker.ANCHOR_CENTER,
                Marker.ANCHOR_BOTTOM
            )

            setOnMarkerClickListener { _, _ ->
                Toast.makeText(
                    context,
                    "Posição de demonstração do usuário",
                    Toast.LENGTH_SHORT
                ).show()
                true
            }
        }
    }

    val iconeUsuario = remember(
        context,
        usuario?.id,
        avatarAtual
    ) {
        criarPinDoUsuario(
            context = context,
            avatar = avatarAtual
        )
    }

    DisposableEffect(
        mapView,
        marcadorKKKK,
        marcadorGuaracui,
        marcadorLanterna,
        marcadorUsuario
    ) {
        marcadorUsuario.icon = iconeUsuario

        mapView.overlays.add(marcadorKKKK)
        mapView.overlays.add(marcadorLanterna)
        mapView.overlays.add(marcadorUsuario)
        mapView.overlays.add(marcadorGuaracui)

        mapView.invalidate()

        onDispose {
            mapView.overlays.remove(marcadorKKKK)
            mapView.overlays.remove(marcadorGuaracui)
            mapView.overlays.remove(marcadorLanterna)
            mapView.overlays.remove(marcadorUsuario)
        }
    }

    LaunchedEffect(iconeUsuario) {
        marcadorUsuario.icon = iconeUsuario
        mapView.invalidate()
    }

    val locationOverlay = remember(mapView) {
        MyLocationNewOverlay(
            GpsMyLocationProvider(context),
            mapView
        )
    }

    var hasLocationPermission by remember {
        mutableStateOf(temPermissaoDeLocalizacao(context))
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission =
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            launcher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    DisposableEffect(
        mapView,
        locationOverlay,
        hasLocationPermission
    ) {
        if (hasLocationPermission) {
            mapView.overlays.add(0, locationOverlay)
            locationOverlay.enableMyLocation()
            mapView.invalidate()
        }

        onDispose {
            locationOverlay.disableMyLocation()
            mapView.overlays.remove(locationOverlay)
        }
    }

    DisposableEffect(lifecycleOwner, mapView) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> Unit
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        if (
            lifecycleOwner.lifecycle.currentState.isAtLeast(
                Lifecycle.State.RESUMED
            )
        ) {
            mapView.onResume()
        }

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView.onPause()
        }
    }

    DisposableEffect(mapView) {
        onDispose {
            mapView.onDetach()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9))
    ) {
        AndroidView(
            factory = { mapView },
            modifier = Modifier.fillMaxSize()
        )

        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(
                    top = 28.dp,
                    start = 20.dp,
                    end = 20.dp
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .clickable {
                        onNavigateToProfile()
                    },
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                border = BorderStroke(2.dp, HomeCoral),
                shadowElevation = 3.dp
            ) {
                Row(
                    modifier = Modifier.padding(
                        start = 5.dp,
                        end = 12.dp,
                        top = 4.dp,
                        bottom = 4.dp
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HomeAvatar(
                        avatar = avatarAtual,
                        modifier = Modifier.size(28.dp)
                    )

                    Spacer(Modifier.width(5.dp))

                    Text(
                        text = nomeCurto,
                        color = Color(0xFF888888),
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                border = BorderStroke(2.dp, HomeCoral),
                shadowElevation = 3.dp
            ) {
                Row(
                    modifier = Modifier.padding(
                        horizontal = 10.dp,
                        vertical = 5.dp
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "10",
                        color = Color(0xFF888888),
                        fontSize = 16.sp
                    )

                    Spacer(Modifier.width(5.dp))

                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Estrelas",
                        tint = Color(0xFFFFD83D),
                        modifier = Modifier.size(25.dp)
                    )
                }
            }
        }

        SmallFloatingActionButton(
            onClick = {
                if (!hasLocationPermission) {
                    launcher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                } else {
                    val localizacao = locationOverlay.myLocation

                    if (localizacao != null) {
                        mapView.controller.animateTo(localizacao)
                        mapView.controller.setZoom(19.0)
                    } else {
                        locationOverlay.enableMyLocation()

                        Toast.makeText(
                            context,
                            "Aguardando sua localização...",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 86.dp, end = 20.dp),
            shape = CircleShape,
            containerColor = Color.White,
            contentColor = HomeCoral
        ) {
            Icon(
                imageVector = Icons.Default.MyLocation,
                contentDescription = "Centralizar na localização GPS"
            )
        }

        Text(
            text = "© OpenStreetMap contributors",
            color = Color(0xFF555555),
            fontSize = 10.sp,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .background(Color.White.copy(alpha = 0.85f))
                .padding(horizontal = 5.dp, vertical = 2.dp)
        )
    }
}

private fun temPermissaoDeLocalizacao(
    context: Context
): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
}

private fun criarIconeDoMapa(
    context: Context,
    recurso: Int,
    larguraDp: Int,
    alturaDp: Int
): BitmapDrawable {
    val densidade = context.resources.displayMetrics.density
    val largura = (larguraDp * densidade).toInt().coerceAtLeast(1)
    val altura = (alturaDp * densidade).toInt().coerceAtLeast(1)

    val drawable = requireNotNull(
        ContextCompat.getDrawable(context, recurso)
    )

    val bitmap = Bitmap.createBitmap(
        largura,
        altura,
        Bitmap.Config.ARGB_8888
    )

    val larguraOriginal = drawable.intrinsicWidth.coerceAtLeast(1)
    val alturaOriginal = drawable.intrinsicHeight.coerceAtLeast(1)

    val escala = minOf(
        largura.toFloat() / larguraOriginal,
        altura.toFloat() / alturaOriginal
    )

    val larguraFinal = (larguraOriginal * escala).toInt()
    val alturaFinal = (alturaOriginal * escala).toInt()

    val esquerda = (largura - larguraFinal) / 2
    val topo = (altura - alturaFinal) / 2

    drawable.setBounds(
        esquerda,
        topo,
        esquerda + larguraFinal,
        topo + alturaFinal
    )

    drawable.draw(Canvas(bitmap))

    return BitmapDrawable(context.resources, bitmap)
}

private fun criarPinDoUsuario(
    context: Context,
    avatar: String?
): BitmapDrawable {
    val densidade = context.resources.displayMetrics.density
    val largura = (52 * densidade).toInt().coerceAtLeast(1)
    val altura = (64 * densidade).toInt().coerceAtLeast(1)

    val bitmap = Bitmap.createBitmap(
        largura,
        altura,
        Bitmap.Config.ARGB_8888
    )

    val canvas = Canvas(bitmap)

    canvas.scale(
        largura / 68f,
        altura / 84f
    )

    val preenchimento = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.WHITE
        style = Paint.Style.FILL
    }

    val contorno = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.rgb(255, 102, 107)
        style = Paint.Style.STROKE
        strokeWidth = 2.5f
        strokeJoin = Paint.Join.ROUND
    }

    val pin = Path().apply {
        moveTo(34f, 3f)

        cubicTo(
            17f, 3f,
            3f, 17f,
            3f, 34f
        )

        cubicTo(
            3f, 48f,
            11f, 59f,
            24f, 64f
        )

        lineTo(34f, 80f)
        lineTo(44f, 64f)

        cubicTo(
            57f, 59f,
            65f, 48f,
            65f, 34f
        )

        cubicTo(
            65f, 17f,
            51f, 3f,
            34f, 3f
        )

        close()
    }

    canvas.drawPath(pin, preenchimento)
    canvas.drawPath(pin, contorno)

    val recursoAvatar = when (avatar) {
        "azul" -> R.drawable.avatar_azul
        "vermelho" -> R.drawable.avatar_vermelho
        else -> R.drawable.avatar_amarelo
    }

    val drawable = requireNotNull(
        ContextCompat.getDrawable(context, recursoAvatar)
    )

    val larguraOriginal = drawable.intrinsicWidth.coerceAtLeast(1)
    val alturaOriginal = drawable.intrinsicHeight.coerceAtLeast(1)

    val escala = minOf(
        48f / larguraOriginal,
        48f / alturaOriginal
    )

    val larguraAvatar = (larguraOriginal * escala).toInt()
    val alturaAvatar = (alturaOriginal * escala).toInt()

    val esquerda = (34f - larguraAvatar / 2f).toInt()
    val topo = (34f - alturaAvatar / 2f).toInt()

    drawable.setBounds(
        esquerda,
        topo,
        esquerda + larguraAvatar,
        topo + alturaAvatar
    )

    drawable.draw(canvas)

    return BitmapDrawable(context.resources, bitmap)
}