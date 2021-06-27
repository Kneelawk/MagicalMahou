package com.kneelawk.magicalmahou.proxy

object MMProxy {
    private lateinit var proxy: CommonProxy

    fun init(proxy: CommonProxy) {
        this.proxy = proxy
    }

    fun getProxy(): CommonProxy {
        return proxy
    }
}