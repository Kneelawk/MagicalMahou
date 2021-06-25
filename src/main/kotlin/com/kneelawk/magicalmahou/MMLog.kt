package com.kneelawk.magicalmahou

import org.apache.logging.log4j.LogManager

object MMLog {
    val log = LogManager.getLogger(MMConstants.MOD_ID)

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