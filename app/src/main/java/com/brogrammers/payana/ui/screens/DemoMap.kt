package com.brogrammers.payana.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brogrammers.payana.ui.theme.MapRoad
import com.brogrammers.payana.ui.theme.MapSurface
import com.brogrammers.payana.ui.theme.TealPrimary

/** A deliberately fake map for the hackathon demo. It has no GPS, tiles, or navigation data. */
@Composable
fun DemoMapCard(
    modifier: Modifier = Modifier,
    showRoute: Boolean = true,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(228.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(MapSurface),
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val roads = listOf(
                listOf(Offset(0f, h * .25f), Offset(w * .26f, h * .18f), Offset(w * .52f, h * .30f), Offset(w, h * .20f)),
                listOf(Offset(0f, h * .72f), Offset(w * .22f, h * .58f), Offset(w * .52f, h * .66f), Offset(w, h * .54f)),
                listOf(Offset(w * .18f, 0f), Offset(w * .28f, h * .32f), Offset(w * .22f, h * .62f), Offset(w * .34f, h)),
                listOf(Offset(w * .70f, 0f), Offset(w * .62f, h * .28f), Offset(w * .74f, h * .56f), Offset(w * .64f, h)),
            )
            roads.forEach { points ->
                for (i in 0 until points.lastIndex) {
                    drawLine(MapRoad, points[i], points[i + 1], strokeWidth = 5f)
                }
            }
            if (showRoute) {
                val route = Path().apply {
                    moveTo(w * .18f, h * .78f)
                    lineTo(w * .34f, h * .62f)
                    lineTo(w * .56f, h * .43f)
                    lineTo(w * .82f, h * .25f)
                }
                drawPath(route, TealPrimary, style = Stroke(width = 8f))
            }
        }

        Surface(
            modifier = Modifier.align(Alignment.TopStart).padding(14.dp),
            shape = RoundedCornerShape(50),
            color = Color.White.copy(alpha = .92f),
        ) {
            Text(
                "DEMO MAP  •  NOT LIVE",
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                color = TealPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        MapMarker("You", Alignment.BottomStart, TealPrimary)
        MapMarker("Pickup", Alignment.TopEnd, Color(0xFF0E8F74))
        MapMarker("Shared rider", Alignment.Center, Color(0xFFE07A3F))
        MapMarker("Destination", Alignment.BottomEnd, Color(0xFFB33A4A))
    }
}

@Composable
private fun MapMarker(label: String, alignment: Alignment, color: Color) {
    Column(
        modifier = Modifier.align(alignment).padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Surface(shape = CircleShape, color = color, shadowElevation = 3.dp) {
            Box(modifier = Modifier.padding(7.dp)) {
                Box(modifier = Modifier.size(10.dp))
            }
        }
        Surface(shape = RoundedCornerShape(8.dp), color = Color.White.copy(alpha = .94f)) {
            Text(
                label,
                modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF263633),
            )
        }
    }
}
