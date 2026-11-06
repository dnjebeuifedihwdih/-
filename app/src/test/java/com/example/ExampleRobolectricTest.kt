package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("جیم وی پی ان", appName)
  }

  @Test
  fun `verify aether warp default routing mode and countries`() {
    val defaultMode = com.example.model.RoutingMode.AETHER_WARP_DEFAULT
    assertEquals("aether_warp", defaultMode.id)
    org.junit.Assert.assertTrue(com.example.model.SupportedCountries.list.isNotEmpty())
  }

  @Test
  fun `verify public ip info and split tunnel modes`() {
    val ipInfo = com.example.model.PublicIpInfo(ip = "1.1.1.1", country = "Cloudflare")
    assertEquals("1.1.1.1", ipInfo.ip)
    val modes = com.example.model.SplitTunnelMode.values()
    assertEquals(3, modes.size)
  }
}
