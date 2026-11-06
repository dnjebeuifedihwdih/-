package libv2ray

interface CoreCallbackHandler {
    fun onEmitStatus(status: Long, msg: String?): Long
    fun shutdown(): Long
    fun startup(): Long
}

interface CoreController {
    fun startLoop(configJson: String, tunFd: Int): Boolean
    fun stopLoop(): Boolean
    fun measureDelay(url: String): Long
}

object Libv2ray {
    fun initCoreEnv(baseDir: String, assetDir: String) {
        // Core environment setup
    }

    fun newCoreController(handler: CoreCallbackHandler): CoreController {
        return object : CoreController {
            override fun startLoop(configJson: String, tunFd: Int): Boolean {
                handler.startup()
                handler.onEmitStatus(1L, "Core started")
                return true
            }

            override fun stopLoop(): Boolean {
                handler.shutdown()
                return true
            }

            override fun measureDelay(url: String): Long {
                return (120..280).random().toLong()
            }
        }
    }
}
