package com.kneelawk.magicalmahou.client

import com.kneelawk.magicalmahou.block.MMBlocksClient
import com.kneelawk.magicalmahou.client.particle.MMParticlesClient
import com.kneelawk.magicalmahou.client.render.player.MMFeatureRenderers
import com.kneelawk.magicalmahou.client.screen.MMScreens
import com.kneelawk.magicalmahou.client.skin.ClientSkinManagers
import com.kneelawk.magicalmahou.client.skin.MMPlayerSkinRenderer
import com.kneelawk.magicalmahou.proxy.ClientProxy
import com.kneelawk.magicalmahou.proxy.MMProxy

fun init() {
    MMProxy.init(ClientProxy)
    MMClientSettings.init()
    ClientSkinManagers.init()
    MMFeatureRenderers.init()
    MMPlayerSkinRenderer.init()
    MMBlocksClient.init()
    MMKeys.register()
    MMScreens.init()
    MMParticlesClient.init()
}
