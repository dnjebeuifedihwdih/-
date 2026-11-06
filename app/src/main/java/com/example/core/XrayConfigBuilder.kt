package com.example.core

import com.example.model.VpsServer
import org.json.JSONArray
import org.json.JSONObject

object XrayConfigBuilder {

    /**
     * Builds Xray JSON configuration for Method 1:
     * Full-system TUN capture + SOCKS/HTTP inbounds -> routed into Aether Core (127.0.0.1:1819).
     */
    fun buildAetherTunConfig(bypassIr: Boolean = true): String {
        val root = JSONObject()

        // Log config
        root.put("log", JSONObject().apply {
            put("loglevel", "warning")
        })

        // DNS config with encrypted DoH & fallback
        root.put("dns", JSONObject().apply {
            put("servers", JSONArray().apply {
                put("https://1.1.1.1/dns-query")
                put("https://dns.google/dns-query")
                put("1.1.1.1")
                put("8.8.8.8")
            })
        })

        // Inbounds:
        // 1. Native TUN Inbound to capture all Android IP traffic from tunFd (Google, Instagram, Chrome, etc.)
        // 2. SOCKS5 Inbound on 127.0.0.1:10808 (for apps like Telegram configured with manual proxy)
        // 3. HTTP Inbound on 127.0.0.1:10809
        val inbounds = JSONArray()

        inbounds.put(JSONObject().apply {
            put("tag", "tun-in")
            put("protocol", "tun")
            put("settings", JSONObject().apply {
                put("name", "tun0")
                put("mtu", 1500)
            })
            put("sniffing", JSONObject().apply {
                put("enabled", true)
                put("destOverride", JSONArray().apply {
                    put("http")
                    put("tls")
                })
            })
        })

        inbounds.put(JSONObject().apply {
            put("tag", "socks-in")
            put("port", 10808)
            put("listen", "127.0.0.1")
            put("protocol", "socks")
            put("settings", JSONObject().apply {
                put("auth", "noauth")
                put("udp", true)
            })
            put("sniffing", JSONObject().apply {
                put("enabled", true)
                put("destOverride", JSONArray().apply {
                    put("http")
                    put("tls")
                })
            })
        })

        inbounds.put(JSONObject().apply {
            put("tag", "http-in")
            put("port", 10809)
            put("listen", "127.0.0.1")
            put("protocol", "http")
            put("sniffing", JSONObject().apply {
                put("enabled", true)
                put("destOverride", JSONArray().apply {
                    put("http")
                    put("tls")
                })
            })
        })

        root.put("inbounds", inbounds)

        // Outbounds
        val outbounds = JSONArray()

        // 1. Primary outbound: SOCKS5 proxy to Aether Rust Core (127.0.0.1:1819)
        outbounds.put(JSONObject().apply {
            put("tag", "proxy")
            put("protocol", "socks")
            put("settings", JSONObject().apply {
                put("servers", JSONArray().apply {
                    put(JSONObject().apply {
                        put("address", "127.0.0.1")
                        put("port", 1819)
                    })
                })
            })
        })

        // 2. DNS outbound (hijacks port 53 DNS queries from Android apps to internal DoH engine)
        outbounds.put(JSONObject().apply {
            put("tag", "dns-out")
            put("protocol", "dns")
        })

        // 3. Direct outbound
        outbounds.put(JSONObject().apply {
            put("tag", "direct")
            put("protocol", "freedom")
            put("settings", JSONObject())
        })

        // 4. Block outbound
        outbounds.put(JSONObject().apply {
            put("tag", "block")
            put("protocol", "blackhole")
            put("settings", JSONObject().apply {
                put("response", JSONObject().apply {
                    put("type", "none")
                })
            })
        })

        root.put("outbounds", outbounds)

        // Routing rules
        val routing = JSONObject()
        routing.put("domainStrategy", "IPIfNonMatch")
        val rules = JSONArray()

        // Rule 1: Route all DNS queries (port 53) to internal dns-out engine
        rules.put(JSONObject().apply {
            put("type", "field")
            put("outboundTag", "dns-out")
            put("port", "53")
            put("network", "tcp,udp")
        })

        // Rule 2: Direct for private IPs (excluding 127.0.0.1)
        rules.put(JSONObject().apply {
            put("type", "field")
            put("outboundTag", "direct")
            put("ip", JSONArray().apply {
                put("10.0.0.0/8")
                put("172.16.0.0/12")
                put("192.168.0.0/16")
                put("100.64.0.0/10")
                put("fc00::/7")
                put("fe80::/10")
            })
        })

        if (bypassIr) {
            // Rule 3: Direct for .ir domains and key local portals
            rules.put(JSONObject().apply {
                put("type", "field")
                put("outboundTag", "direct")
                put("domain", JSONArray().apply {
                    put("domain:ir")
                    put("shaparak.ir")
                    put("zula.ir")
                    put("divar.ir")
                    put("snapp.ir")
                    put("torob.com")
                    put("digikala.com")
                })
            })
        }

        // Rule 4: Block UDP 443 (QUIC) to prevent packet-drop stalls on Instagram/YouTube, forcing instant TLS 1.3 TCP
        rules.put(JSONObject().apply {
            put("type", "field")
            put("outboundTag", "block")
            put("port", "443")
            put("network", "udp")
        })

        // Rule 5: All other traffic from tun-in and socks-in to Aether proxy
        rules.put(JSONObject().apply {
            put("type", "field")
            put("outboundTag", "proxy")
            put("network", "tcp,udp")
        })

        routing.put("rules", rules)
        root.put("routing", routing)

        return root.toString(2)
    }

    /**
     * Builds Xray JSON configuration for Method 2:
     * Full-system TUN capture + SOCKS/HTTP inbounds -> Real VLESS Reality / TLS with TLS Fragment evasion.
     */
    fun buildVpsConfig(
        server: VpsServer,
        coverageDomain: String = "shaparak.ir",
        enableFragment: Boolean = true,
        bypassIr: Boolean = true
    ): String {
        val root = JSONObject()

        root.put("log", JSONObject().apply {
            put("loglevel", "warning")
        })

        root.put("dns", JSONObject().apply {
            put("servers", JSONArray().apply {
                put("https://1.1.1.1/dns-query")
                put("https://dns.google/dns-query")
                put("1.1.1.1")
                put("8.8.8.8")
            })
        })

        val inbounds = JSONArray()

        // 1. Native TUN Inbound
        inbounds.put(JSONObject().apply {
            put("tag", "tun-in")
            put("protocol", "tun")
            put("settings", JSONObject().apply {
                put("name", "tun0")
                put("mtu", 1500)
            })
            put("sniffing", JSONObject().apply {
                put("enabled", true)
                put("destOverride", JSONArray().apply {
                    put("http")
                    put("tls")
                })
            })
        })

        // 2. SOCKS5 Inbound on 10808
        inbounds.put(JSONObject().apply {
            put("tag", "socks-in")
            put("port", 10808)
            put("listen", "127.0.0.1")
            put("protocol", "socks")
            put("settings", JSONObject().apply {
                put("auth", "noauth")
                put("udp", true)
            })
            put("sniffing", JSONObject().apply {
                put("enabled", true)
                put("destOverride", JSONArray().apply {
                    put("http")
                    put("tls")
                })
            })
        })

        // 3. HTTP Inbound on 10809
        inbounds.put(JSONObject().apply {
            put("tag", "http-in")
            put("port", 10809)
            put("listen", "127.0.0.1")
            put("protocol", "http")
            put("sniffing", JSONObject().apply {
                put("enabled", true)
                put("destOverride", JSONArray().apply {
                    put("http")
                    put("tls")
                })
            })
        })

        root.put("inbounds", inbounds)

        val outbounds = JSONArray()

        // 1. Primary VPS Outbound (VLESS)
        val vlessOutbound = JSONObject()
        vlessOutbound.put("tag", "proxy")
        vlessOutbound.put("protocol", "vless")

        val vlessSettings = JSONObject()
        val vnext = JSONArray()
        val serverObj = JSONObject()
        serverObj.put("address", server.address)
        serverObj.put("port", server.port)

        val users = JSONArray()
        val userObj = JSONObject()
        userObj.put("id", server.uuid)
        userObj.put("encryption", "none")
        if (server.flow.isNotBlank()) {
            userObj.put("flow", server.flow)
        }
        users.put(userObj)

        serverObj.put("users", users)
        vnext.put(serverObj)
        vlessSettings.put("vnext", vnext)
        vlessOutbound.put("settings", vlessSettings)

        // Stream Settings
        val streamSettings = JSONObject()
        streamSettings.put("network", "tcp")

        val hasValidRealityKey = server.pbk.isNotBlank() && server.pbk.length in 43..44
        val effectiveSecurity = if (hasValidRealityKey) "reality" else "tls"
        streamSettings.put("security", effectiveSecurity)

        if (effectiveSecurity == "reality") {
            val realitySettings = JSONObject()
            realitySettings.put("show", false)
            realitySettings.put("fingerprint", if (server.fp.isNotBlank()) server.fp else "chrome")
            realitySettings.put("serverName", if (server.sni.isNotBlank()) server.sni else "www.cloudflare.com")
            realitySettings.put("publicKey", server.pbk)
            realitySettings.put("shortId", server.sid)
            streamSettings.put("realitySettings", realitySettings)
        } else {
            val tlsSettings = JSONObject()
            tlsSettings.put("serverName", if (server.sni.isNotBlank()) server.sni else server.address)
            tlsSettings.put("fingerprint", if (server.fp.isNotBlank()) server.fp else "chrome")
            tlsSettings.put("allowInsecure", true)
            streamSettings.put("tlsSettings", tlsSettings)
        }

        // Vision (xtls-rprx-vision) requires direct TCP splicing; fragment dialerProxy is applied only when vision is not active
        val hasVision = server.flow.isNotBlank() && server.flow.contains("vision", ignoreCase = true)
        if (!hasVision && enableFragment) {
            val sockopt = JSONObject()
            sockopt.put("dialerProxy", "fragment")
            streamSettings.put("sockopt", sockopt)
        }

        vlessOutbound.put("streamSettings", streamSettings)
        outbounds.put(vlessOutbound)

        // 2. DNS outbound
        outbounds.put(JSONObject().apply {
            put("tag", "dns-out")
            put("protocol", "dns")
        })

        // 3. Freedom outbound with TLS Hello Fragment
        if (enableFragment) {
            val fragmentOutbound = JSONObject()
            fragmentOutbound.put("tag", "fragment")
            fragmentOutbound.put("protocol", "freedom")
            val fragSettings = JSONObject()
            val fragmentObj = JSONObject()
            fragmentObj.put("packets", "tlshello")
            fragmentObj.put("length", "10-30")
            fragmentObj.put("interval", "10-20")
            fragSettings.put("fragment", fragmentObj)
            fragmentOutbound.put("settings", fragSettings)
            outbounds.put(fragmentOutbound)
        }

        // 4. Direct outbound
        outbounds.put(JSONObject().apply {
            put("tag", "direct")
            put("protocol", "freedom")
            put("settings", JSONObject())
        })

        // 5. Block outbound
        outbounds.put(JSONObject().apply {
            put("tag", "block")
            put("protocol", "blackhole")
            put("settings", JSONObject().apply {
                put("response", JSONObject().apply {
                    put("type", "none")
                })
            })
        })

        root.put("outbounds", outbounds)

        // Routing
        val routing = JSONObject()
        routing.put("domainStrategy", "IPIfNonMatch")
        val rules = JSONArray()

        // Rule 1: DNS interception to internal DoH engine
        rules.put(JSONObject().apply {
            put("type", "field")
            put("outboundTag", "dns-out")
            put("port", "53")
            put("network", "tcp,udp")
        })

        // Rule 2: Private IPs direct
        rules.put(JSONObject().apply {
            put("type", "field")
            put("outboundTag", "direct")
            put("ip", JSONArray().apply {
                put("10.0.0.0/8")
                put("172.16.0.0/12")
                put("192.168.0.0/16")
                put("100.64.0.0/10")
                put("fc00::/7")
                put("fe80::/10")
            })
        })

        // Rule 3: Iranian domains direct
        if (bypassIr) {
            val directDomains = JSONArray().apply {
                put("domain:ir")
                put("shaparak.ir")
                put("zula.ir")
                put("divar.ir")
                put("snapp.ir")
                put("torob.com")
                put("digikala.com")
                if (coverageDomain.isNotBlank() && !coverageDomain.equals("shaparak.ir", true)) {
                    put(coverageDomain)
                }
            }
            rules.put(JSONObject().apply {
                put("type", "field")
                put("outboundTag", "direct")
                put("domain", directDomains)
            })
        }

        // Rule 4: Block UDP 443 (QUIC) to prevent packet-drop stalls on Instagram/YouTube, forcing instant TLS 1.3 TCP
        rules.put(JSONObject().apply {
            put("type", "field")
            put("outboundTag", "block")
            put("port", "443")
            put("network", "udp")
        })

        // Rule 5: All other traffic to proxy
        rules.put(JSONObject().apply {
            put("type", "field")
            put("outboundTag", "proxy")
            put("network", "tcp,udp")
        })

        routing.put("rules", rules)
        root.put("routing", routing)

        return root.toString(2)
    }
}
