package com.kneelawk.magicalmahou

import net.fabricmc.loader.api.FabricLoader
import org.apache.logging.log4j.LogManager

object MMLog {
    private val isDevEnvironment = FabricLoader.getInstance().isDevelopmentEnvironment
    val log = LogManager.getLogger(MMConstants.MOD_ID)

    fun debug(msg: Any) {
        if (isDevEnvironment) {
            log.info(msg)
        } else {
            log.debug(msg)
        }
    }

    fun info(msg: Any) {
        log.info(msg)
    }

    fun warn(msg: Any) {
        log.warn(msg)
    }

    fun warn(msg: Any, t: Throwable) {
        log.warn(msg, t)
    }
}