package com.example.maps.Vistas


import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getDrawable
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Marker
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.Dash
import com.google.android.gms.maps.model.Dot
import com.google.android.gms.maps.model.Gap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.Polygon
import android.Manifest
import android.content.ContextWrapper
import android.location.Location
import androidx.activity.ComponentActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.shadow
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import com.example.maps.R
import com.google.maps.android.compose.Polyline

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen() {
    var mapType by remember { mutableStateOf(MapType.NORMAL) }
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    //Convertir el recurso drawable a BitmapDescriptor
    val customMarker = bitmapDescriptorFromVector(
        context = context,
        vectorResId = R.drawable.captura_de_pantalla_2024_11_11_202125// Asegúrate que este es el nombre correcto de tu imagen
    )



    // Verificar permisos iniciales
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val activity = context.findActivity()
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                1
            )
        } else {
            hasLocationPermission = true
            getUserLocation(context) { location ->
                userLocation = location
            }
        }
    }

    // Obtener ubicación cuando se tienen los permisos
    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            getUserLocation(context) { location ->
                userLocation = location
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(
                    userLocation ?: LatLng(-16.4040102, -71.559611),
                    15f
                )
            },
            properties = MapProperties(
                mapType = mapType,
                isMyLocationEnabled = hasLocationPermission
            )
        ) {
            // Mostrar marcador en la ubicación del usuario si está disponible
            userLocation?.let { location ->
                Marker(
                    state = rememberMarkerState(position = location),
                    icon = customMarker,
                    title = "Mi ubicación",
                    snippet = "Estás aquí"
                )
            }
        }

        // Menú desplegable para cambiar el tipo de mapa
        Box(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)
        ) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedButton(
                    onClick = { expanded = true },
                    modifier = Modifier
                        .menuAnchor()
                        .background(Color.White.copy(alpha = 0.95f)) // Fondo blanco con opacidad
                        .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp)), // Sombra sutil y esquinas redondeadas
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White, // Color de fondo blanco para un contraste limpio
                        contentColor = Color(0xFF1565C0) // Azul más oscuro para texto, acorde a Material Design
                    ),
                    shape = RoundedCornerShape(16.dp) , // Redondeado en los bordes
                    border = BorderStroke(1.dp, Color(0xFF1565C0)) // Borde azul oscuro
                )  {
                    Text(
                        text = when (mapType) {
                            MapType.NORMAL -> "Normal"
                            MapType.SATELLITE -> "Satélite"
                            MapType.HYBRID -> "Híbrido"
                            MapType.TERRAIN -> "Terreno"
                            else -> "Seleccionar tipo"
                        },
                        color = Color(0xFF1976D2)
                    )
                }

                // Opciones de tipo de mapa
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Normal") },
                        onClick = {
                            mapType = MapType.NORMAL
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Satélite") },
                        onClick = {
                            mapType = MapType.SATELLITE
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Híbrido") },
                        onClick = {
                            mapType = MapType.HYBRID
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Terreno") },
                        onClick = {
                            mapType = MapType.TERRAIN
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

// Función para solicitar permisos de ubicación
private suspend fun requestLocationPermission(context: Context) {
    val permission = Manifest.permission.ACCESS_FINE_LOCATION
    if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
        val activity = context.findActivity()
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            1
        )
    }
}

// Función para obtener la ubicación actual
private fun getUserLocation(context: Context, onLocationReceived: (LatLng) -> Unit) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    try {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    onLocationReceived(LatLng(it.latitude, it.longitude))
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

// Extensión útil para encontrar la Activity desde un Context
fun Context.findActivity(): ComponentActivity {
    var context = this
    while (context is ContextWrapper) {
        if (context is ComponentActivity) return context
        context = context.baseContext
    }
    throw IllegalStateException("No se encontró la Activity")
}

//Función para convertir el vector/imagen a BitmapDescriptor
fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
    return try {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
        vectorDrawable?.let {
            val width = it.intrinsicWidth/4
            val height = it.intrinsicHeight/4

            // Asegura que el Bitmap usa ARGB_8888 para canal alfa (transparente)
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = android.graphics.Canvas(bitmap)

            // Dibuja el vector en el Canvas del Bitmap
            it.setBounds(0, 0, width, height)
            it.draw(canvas)

            // Convierte el Bitmap en BitmapDescriptor
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
@Composable
fun DrawPolylineExample() {
    val arequipaLocation = LatLng(-16.4040102, -71.559611) // Coordenadas de Arequipa, Perú
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(arequipaLocation, 12f)
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        // Polilínea 1: Ruta en el centro de Arequipa
        Polyline(
            points = listOf(
                LatLng(-16.4040102, -71.559611),
                LatLng(-16.4050102, -71.558611),
                LatLng(-16.4060102, -71.557611),
                LatLng(-16.4070102, -71.556611)
            ),
            color = Color.Blue,
            width = 8f
        )

        // Polilínea 2: Ruta en otra zona de Arequipa
        Polyline(
            points = listOf(
                LatLng(-16.4205151, -71.4945209), // Paucarpata
                LatLng(-16.4225151, -71.4935209),
                LatLng(-16.4245151, -71.4925209),
                LatLng(-16.4265151, -71.4915209)
            ),
            color = Color.Red,
            width = 8f
        )

        Polyline(
            points = listOf(
                LatLng(-16.4205151, -71.4945209),
                LatLng(-16.4215151, -71.4955209),
                LatLng(-16.4225151, -71.4945209),
                LatLng(-16.4235151, -71.4955209),
                LatLng(-16.4245151, -71.4945209)
            ),
            color = Color.Green,
            width = 8f
        )
        Polyline(
            points = listOf(
                LatLng(-16.4040102, -71.559611),
                LatLng(-16.4042102, -71.559611),
                LatLng(-16.4044102, -71.559811),
                LatLng(-16.4046102, -71.560011),
                LatLng(-16.4048102, -71.560211),
                LatLng(-16.4050102, -71.560411)
            ),
            color = Color.Magenta,
            width = 8f
        )
        Polyline(
            points = listOf(
                LatLng(-16.3524187, -71.5675994),
                LatLng(-16.3544187, -71.5675994),
                LatLng(-16.3534187, -71.5655994),
                LatLng(-16.3524187, -71.5675994) // Volver al punto inicial
            ),
            color = Color.Cyan,
            width = 8f
        )
        Polyline(
            points = listOf(
                LatLng(-16.432292, -71.509145),
                LatLng(-16.432292, -71.507145),
                LatLng(-16.430292, -71.507145),
                LatLng(-16.430292, -71.509145),
                LatLng(-16.432292, -71.509145) // Volver al punto inicial
            ),
            color = Color.Blue,
            width = 8f
        )

        Polyline(
            points = listOf(
                LatLng(-16.399508, -71.536861),
                LatLng(-16.398508, -71.536061),
                LatLng(-16.397508, -71.536861),
                LatLng(-16.396508, -71.536061),
                LatLng(-16.395508, -71.536861)
            ),
            color = Color.Yellow,
            width = 8f
        )
        Polyline(
            points = listOf(
                LatLng(-16.432292, -71.509145),
                LatLng(-16.432292, -71.507145),
                LatLng(-16.430292, -71.507145),
                LatLng(-16.430292, -71.509145),
                LatLng(-16.432292, -71.509145) // Volver al punto inicial
            ),
            color = Color.Blue,
            width = 8f
        )





    }
}


