package com.brogrammers.payana.ui.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.brogrammers.payana.ui.theme.MapPark
import com.brogrammers.payana.ui.theme.MapRoad
import com.brogrammers.payana.ui.theme.MapSurface
import com.brogrammers.payana.ui.theme.MapWater
import com.brogrammers.payana.ui.theme.TealPrimary

/** A short, animated fictional map. It has no GPS, tiles, or navigation data. */
@Composable
fun FallbackDemoMapCard(
    modifier: Modifier = Modifier,
    showRoute: Boolean = true,
    height: Dp? = 238.dp,
    startStationId: String = "majestic",
    destinationId: String = "koramangala",
) {
    val transition = rememberInfiniteTransition(label = "demo-map-motion")
    val pulse by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1.22f,
        animationSpec = infiniteRepeatable(tween(950), RepeatMode.Reverse),
        label = "location-pulse",
    )
    val riderDrift by transition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(tween(1600), RepeatMode.Reverse),
        label = "rider-drift",
    )

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .then(if (height == null) Modifier.fillMaxSize() else Modifier.height(height))
            .clip(RoundedCornerShape(26.dp))
            .background(MapSurface),
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            drawRect(MapPark, topLeft = Offset(w * .66f, h * .50f), size = Size(w * .30f, h * .34f))
            drawOval(MapWater, topLeft = Offset(-w * .08f, h * .54f), size = Size(w * .38f, h * .36f))

            val roads = listOf(
                listOf(Offset(0f, h * .22f), Offset(w * .22f, h * .16f), Offset(w * .48f, h * .25f), Offset(w, h * .15f)),
                listOf(Offset(0f, h * .78f), Offset(w * .24f, h * .62f), Offset(w * .48f, h * .70f), Offset(w, h * .56f)),
                listOf(Offset(w * .16f, 0f), Offset(w * .28f, h * .30f), Offset(w * .21f, h * .60f), Offset(w * .34f, h)),
                listOf(Offset(w * .73f, 0f), Offset(w * .62f, h * .26f), Offset(w * .75f, h * .54f), Offset(w * .64f, h)),
            )
            roads.forEach { points ->
                for (i in 0 until points.lastIndex) drawLine(MapRoad, points[i], points[i + 1], strokeWidth = 6f)
            }

            if (showRoute) {
                val start = fallbackMapPoint(startStationId, false)
                val destination = fallbackMapPoint(destinationId, true)
                val route = Path().apply {
                    moveTo(w * start.x, h * start.y)
                    lineTo(w * ((start.x + destination.x) / 2f), h * ((start.y + destination.y) / 2f - .12f))
                    lineTo(w * destination.x, h * destination.y)
                }
                drawPath(route, TealPrimary, style = Stroke(width = 9f))
                drawCircle(Color.White, radius = 13f, center = Offset(w * .54f, h * .43f))
                drawCircle(TealPrimary, radius = 7f, center = Offset(w * .54f, h * .43f))
            }
        }

        Surface(
            modifier = Modifier.align(Alignment.TopStart).padding(14.dp),
            shape = RoundedCornerShape(50),
            color = Color.White.copy(alpha = .94f),
        ) {
            Text(
                "DEMO AREA  •  ROUTE PREVIEW",
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                color = TealPrimary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        val start = fallbackMapPoint(startStationId, false)
        val destination = fallbackMapPoint(destinationId, true)
        MapMarker(startStationId.replace('_', ' '), Modifier.align(Alignment.TopStart).offset(x = maxWidth * start.x, y = maxHeight * start.y), TealPrimary, pulse)
        MapMarker("Shared rider", Modifier.align(Alignment.TopStart).offset(x = maxWidth * ((start.x + destination.x) / 2f) + riderDrift.dp, y = maxHeight * ((start.y + destination.y) / 2f)), Color(0xFFE07A3F), pulse)
        MapMarker(destinationId.replace('_', ' '), Modifier.align(Alignment.TopStart).offset(x = maxWidth * destination.x, y = maxHeight * destination.y), Color(0xFFB33A4A), 1f)
    }
}

@Composable
private fun MapMarker(label: String, modifier: Modifier, color: Color, pulse: Float) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(shape = CircleShape, color = color, shadowElevation = 4.dp) {
            Box(modifier = Modifier.padding(7.dp).scale(pulse).size(10.dp))
        }
        Surface(shape = RoundedCornerShape(8.dp), color = Color.White.copy(alpha = .95f)) {
            Text(
                label,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF263633),
            )
        }
    }
}
