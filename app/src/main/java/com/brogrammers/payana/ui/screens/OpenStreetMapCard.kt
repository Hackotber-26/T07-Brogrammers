package com.brogrammers.payana.ui.screens

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.graphics.drawable.GradientDrawable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.viewinterop.AndroidView
import android.preference.PreferenceManager
import com.brogrammers.payana.ui.theme.MapSurface
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

internal data class DemoMapPoint(val x: Float, val y: Float)

private val StationCoordinates = mapOf(
    "indiranagar" to GeoPoint(12.9784, 77.6408), "hal" to GeoPoint(12.9591, 77.6487),
    "mg_road" to GeoPoint(12.9756, 77.6063), "cubbon" to GeoPoint(12.9763, 77.5929),
    "majestic" to GeoPoint(12.9767, 77.5713), "jayanagar" to GeoPoint(12.9299, 77.5825),
    "banashankari" to GeoPoint(12.9150, 77.5733), "silk_board" to GeoPoint(12.9175, 77.6227),
    "rv_road" to GeoPoint(12.9227, 77.5800), "lalbagh" to GeoPoint(12.9507, 77.5848),
)
private val DestinationCoordinates = mapOf(
    "koramangala" to GeoPoint(12.9352, 77.6245), "hsr" to GeoPoint(12.9116, 77.6389),
    "indiranagar_100" to GeoPoint(12.9719, 77.6412), "brigade" to GeoPoint(12.9719, 77.6080),
    "ub_city" to GeoPoint(12.9716, 77.5962), "jayanagar_4th" to GeoPoint(12.9250, 77.5938),
    "bsk_temple" to GeoPoint(12.9155, 77.5735), "jp_nagar" to GeoPoint(12.9063, 77.5850),
    "cubbon_gate" to GeoPoint(12.9765, 77.5924), "electronic_city" to GeoPoint(12.8452, 77.6602),
)

internal fun fallbackMapPoint(id: String, destination: Boolean): DemoMapPoint = if (destination) {
    when (id) {
        "hsr" -> DemoMapPoint(.76f, .72f); "brigade", "ub_city" -> DemoMapPoint(.68f, .34f)
        "jayanagar_4th", "bsk_temple", "jp_nagar" -> DemoMapPoint(.24f, .72f)
        "indiranagar_100", "cubbon_gate" -> DemoMapPoint(.70f, .20f)
        "electronic_city" -> DemoMapPoint(.88f, .84f)
        else -> DemoMapPoint(.82f, .76f)
    }
} else when (id) {
    "indiranagar", "hal" -> DemoMapPoint(.76f, .22f); "mg_road", "cubbon" -> DemoMapPoint(.62f, .26f)
    "majestic" -> DemoMapPoint(.24f, .20f); "jayanagar", "rv_road" -> DemoMapPoint(.28f, .66f)
    "banashankari" -> DemoMapPoint(.18f, .82f); "silk_board" -> DemoMapPoint(.72f, .68f)
    "lalbagh" -> DemoMapPoint(.46f, .52f); else -> DemoMapPoint(.50f, .48f)
}

/**
 * Online OpenStreetMap view for the demo. It uses the normal osmdroid tile cache,
 * never prefetches an area, and falls back to the local stylized map when offline.
 */
@Composable
fun DemoMapCard(
    modifier: Modifier = Modifier,
    showRoute: Boolean = true,
    height: Dp? = 238.dp,
    startStationId: String = "majestic",
    destinationId: String = "koramangala",
) {
    val context = LocalContext.current
    val online = remember { hasNetwork(context) }
    if (!online) {
        FallbackDemoMapCard(modifier = modifier, showRoute = showRoute, height = height, startStationId = startStationId, destinationId = destinationId)
        return
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .then(if (height == null) Modifier.fillMaxSize() else Modifier.height(height))
            .clip(RoundedCornerShape(26.dp))
            .background(MapSurface),
    ) {
        key(startStationId, destinationId, showRoute) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { viewContext -> createMap(viewContext, showRoute, startStationId, destinationId) },
                update = { map -> map.invalidate() },
            )
        }
        Surface(
            modifier = Modifier.padding(12.dp),
            shape = RoundedCornerShape(50),
            color = Color.White.copy(alpha = .94f),
        ) {
            Text("© OpenStreetMap contributors", modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
        }
    }
}

private fun createMap(context: Context, showRoute: Boolean, startStationId: String, destinationId: String): MapView {
    Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))
    Configuration.getInstance().userAgentValue = context.packageName

    return MapView(context).apply {
        setTileSource(TileSourceFactory.MAPNIK)
        setMultiTouchControls(true)
        setUseDataConnection(true)
        controller.setZoom(12.4)
        val pickup = StationCoordinates[startStationId] ?: StationCoordinates.getValue("majestic")
        val destination = DestinationCoordinates[destinationId] ?: DestinationCoordinates.getValue("koramangala")
        controller.setCenter(GeoPoint((pickup.latitude + destination.latitude) / 2, (pickup.longitude + destination.longitude) / 2))

        if (showRoute) {
            overlays.add(Polyline().apply {
                outlinePaint.color = android.graphics.Color.WHITE
                outlinePaint.strokeWidth = 17f
                setPoints(routePoints(pickup, destination))
            })
            overlays.add(Polyline().apply {
                outlinePaint.color = android.graphics.Color.rgb(0, 137, 123)
                outlinePaint.strokeWidth = 9f
                setPoints(routePoints(pickup, destination))
            })
        }
        addMarker(pickup, "${startStationId.replace('_', ' ')} pickup", android.graphics.Color.rgb(0, 121, 107))
        addMarker(GeoPoint((pickup.latitude + destination.latitude) / 2 + .004, (pickup.longitude + destination.longitude) / 2), "Shared rider", android.graphics.Color.rgb(224, 122, 63))
        addMarker(destination, "${destinationId.replace('_', ' ')} destination", android.graphics.Color.rgb(179, 58, 74))
        invalidate()
    }
}

private fun routePoints(pickup: GeoPoint, destination: GeoPoint): List<GeoPoint> {
    val midLat = (pickup.latitude + destination.latitude) / 2
    val midLon = (pickup.longitude + destination.longitude) / 2
    return listOf(pickup, GeoPoint(midLat + .006, midLon - .004), GeoPoint(midLat - .003, midLon + .006), destination)
}

private fun MapView.addMarker(point: GeoPoint, title: String, color: Int) {
    overlays.add(Marker(this).apply {
        position = point
        this.title = title
        icon = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(color)
            setStroke(4, android.graphics.Color.WHITE)
            setSize(44, 44)
        }
        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
    })
}

private fun hasNetwork(context: Context): Boolean {
    return try {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
        val network = manager.activeNetwork ?: return false
        val capabilities = manager.getNetworkCapabilities(network) ?: return false
        capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    } catch (_: SecurityException) {
        false
    }
}
