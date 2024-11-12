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
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import com.example.maps.R
import com.google.maps.android.compose.Polyline

@Composable
fun MapScreen() {
    val context = LocalContext.current
    val ArequipaLocation = LatLng(-16.4040102, -71.559611) // Arequipa, Perú
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(ArequipaLocation, 12f)
    }

    // Lista de ubicaciones adicionales
    val locations = listOf(
        LatLng(-16.433415, -71.5442652), // JLByR
        LatLng(-16.4205151, -71.4945209), // Paucarpata
        LatLng(-16.3524187, -71.5675994) // Zamacola
    )

    // Escalar el icono a un tamaño más pequeño
    val originalBitmap = ResourcesCompat.getDrawable(context.resources, R.drawable.captura_de_pantalla_2024_11_11_202125, null)?.toBitmap()
    val scaledBitmap = originalBitmap?.let { Bitmap.createScaledBitmap(it, 50, 50, false) } // Ajusta el tamaño deseado

    // Coordenadas para polígonos
    val mallAventuraPolygon = listOf(
        LatLng(-16.432292, -71.509145),
        LatLng(-16.432757, -71.509626),
        LatLng(-16.433013, -71.509310),
        LatLng(-16.432566, -71.508853)
    )

    val parqueLambramaniPolygon = listOf(
        LatLng(-16.422704, -71.530830),
        LatLng(-16.422920, -71.531340),
        LatLng(-16.423264, -71.531110),
        LatLng(-16.423050, -71.530600)
    )

    val plazaDeArmasPolygon = listOf(
        LatLng(-16.398866, -71.536961),
        LatLng(-16.398744, -71.536529),
        LatLng(-16.399178, -71.536289),
        LatLng(-16.399299, -71.536721)
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Añadir GoogleMap al layout
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            // Añadir marcador principal en Arequipa, Perú con el icono escalado
            Marker(
                state = rememberMarkerState(position = ArequipaLocation),
                icon = scaledBitmap?.let { BitmapDescriptorFactory.fromBitmap(it) },
                title = "Arequipa, Perú"
            )

            // Añadir marcadores para cada ubicación en la lista
            locations.forEach { location ->
                Marker(
                    state = rememberMarkerState(position = location),
                    title = "Ubicación",
                    snippet = "Punto de interés"
                )
            }

            // Dibujar polígonos
            Polygon(
                points = plazaDeArmasPolygon,
                strokeColor = Color.Red,
                fillColor = Color.Blue.copy(alpha = 0.3f),
                strokeWidth = 5f
            )
            Polygon(
                points = parqueLambramaniPolygon,
                strokeColor = Color.Green,
                fillColor = Color.Yellow.copy(alpha = 0.3f),
                strokeWidth = 5f
            )
            Polygon(
                points = mallAventuraPolygon,
                strokeColor = Color.Blue,
                fillColor = Color.Cyan.copy(alpha = 0.3f),
                strokeWidth = 5f
            )
        }
    }

    // Mover la cámara a Yura
    LaunchedEffect(Unit) {
        cameraPositionState.animate(
            update = CameraUpdateFactory.newLatLngZoom(LatLng(-16.2520984, -71.6836503), 12f), // Mover a Yura
            durationMs = 3000
        )
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


