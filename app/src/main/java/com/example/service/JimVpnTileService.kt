package com.example.service

import android.content.Intent
import android.graphics.drawable.Icon
import android.net.VpnService
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import com.example.R
import com.example.model.AetherProtocol
import com.example.model.NoiseProfile
import com.example.model.RoutingMode
import com.example.model.ScanMode
import com.example.model.VpnStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.N)
class JimVpnTileService : TileService() {

    private val serviceScope = CoroutineScope(Dispatchers.Main)
    private var stateJob: Job? = null

    override fun onStartListening() {
        super.onStartListening()
        updateTileState()

        stateJob?.cancel()
        stateJob = serviceScope.launch {
            AetherPaperVpnService.vpnState.collect {
                updateTileState()
            }
        }
    }

    override fun onStopListening() {
        stateJob?.cancel()
        stateJob = null
        super.onStopListening()
    }

    override fun onClick() {
        super.onClick()
        val currentState = AetherPaperVpnService.vpnState.value.status

        if (currentState == VpnStatus.CONNECTED || currentState == VpnStatus.CONNECTING) {
            AetherPaperVpnService.stop(this)
        } else {
            // Check if VpnService permission is prepared
            val prepareIntent = VpnService.prepare(this)
            if (prepareIntent != null) {
                // Needs user permission in activity
                val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
                if (launchIntent != null) {
                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivityAndCollapse(launchIntent)
                }
                return
            }

            // Start default fast Aether tunnel
            val params = AetherPaperVpnService.AetherParams(
                protocol = AetherProtocol.MASQUE_H2,
                routingMode = RoutingMode.AETHER_WARP_DEFAULT,
                countryCode = "DE",
                noiseProfile = NoiseProfile.FIREWALL,
                scanMode = ScanMode.TURBO,
                bypassIr = true
            )
            AetherPaperVpnService.startAether(this, params)
        }
    }

    private fun updateTileState() {
        val tile = qsTile ?: return
        val vpnState = AetherPaperVpnService.vpnState.value

        tile.icon = Icon.createWithResource(this, R.drawable.ic_vpn_tile)

        when (vpnState.status) {
            VpnStatus.CONNECTED -> {
                tile.state = Tile.STATE_ACTIVE
                tile.label = "جیم وی پی ان"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    tile.subtitle = "متصل • امن"
                }
            }
            VpnStatus.CONNECTING -> {
                tile.state = Tile.STATE_UNAVAILABLE
                tile.label = "جیم وی پی ان"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    tile.subtitle = "در حال اتصال..."
                }
            }
            VpnStatus.DISCONNECTED, VpnStatus.ERROR -> {
                tile.state = Tile.STATE_INACTIVE
                tile.label = "جیم وی پی ان"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    tile.subtitle = "قطع • لمس برای اتصال"
                }
            }
        }
        tile.updateTile()
    }
}
