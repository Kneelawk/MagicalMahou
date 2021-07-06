package com.kneelawk.magicalmahou.net

import net.minecraft.network.PacketByteBuf
import net.minecraft.util.math.Vec3d

fun PacketByteBuf.writeVec3d(vec: Vec3d) {
    writeDouble(vec.x)
    writeDouble(vec.y)
    writeDouble(vec.z)
}

fun PacketByteBuf.readVec3d(): Vec3d {
    return Vec3d(readDouble(), readDouble(), readDouble())
}
