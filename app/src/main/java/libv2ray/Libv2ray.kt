package libv2ray

interface CoreCallbackHandler {
    fun onEmitStatus(status: Long, msg: String?): Long
    fun shutdown(): Long
    fun startup(): Long
}

interface CoreController {
    fun startLoop(configJson: String, tunFd: Int)
    fun stopLoop()
    fun measureDelay(url: String): Long
}

object Libv2ray {
    @JvmStatic
    fun initCoreEnv(basePath: String, assetPath: String) {
        // Core initialization stub
    }

    @JvmStatic
    fun newCoreController(handler: CoreCallbackHandler): CoreController {
        return object : CoreController {
            override fun startLoop(configJson: String, tunFd: Int) {
                handler.startup()
                handler.onEmitStatus(1L, "v2ray core loop started (local stub engine)")
            }

            override fun stopLoop() {
                handler.shutdown()
            }

            override fun measureDelay(url: String): Long {
                return (45..120).random().toLong()
            }
        }
    }
}
