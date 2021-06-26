package com.kneelawk.magicalmahou.net

import alexiil.mc.lib.net.*

fun <T> NetIdDataK<T>.setC2SReadWrite(
    receiver: T.(NetByteBuf, IMsgReadCtx) -> Unit, writer: T.(NetByteBuf, IMsgWriteCtx) -> Unit
): NetIdDataK<T> {
    return setReadWrite({ obj, buf, ctx ->
        ctx.assertServerSide()
        obj.receiver(buf, ctx)
    }, { obj, buf, ctx ->
        ctx.assertClientSide()
        obj.writer(buf, ctx)
    })
}

fun <T> NetIdDataK<T>.setS2CReadWrite(
    receiver: T.(NetByteBuf, IMsgReadCtx) -> Unit, writer: T.(NetByteBuf, IMsgWriteCtx) -> Unit
): NetIdDataK<T> {
    return setReadWrite({ obj, buf, ctx ->
        ctx.assertClientSide()
        obj.receiver(buf, ctx)
    }, { obj, buf, ctx ->
        ctx.assertServerSide()
        obj.writer(buf, ctx)
    })
}

fun <T> NetIdDataK<T>.setS2CReceiver(receiver: T.(NetByteBuf, IMsgReadCtx) -> Unit): NetIdDataK<T> {
    return setReceiver { obj, buf, ctx ->
        ctx.assertClientSide()
        obj.receiver(buf, ctx)
    }
}

fun <T> NetIdDataK<T>.setC2SReceiver(receiver: T.(NetByteBuf, IMsgReadCtx) -> Unit): NetIdDataK<T> {
    return setReceiver { obj, buf, ctx ->
        ctx.assertServerSide()
        obj.receiver(buf, ctx)
    }
}

fun <T> NetIdSignalK<T>.setC2SReceiver(receiver: T.(IMsgReadCtx) -> Unit): NetIdSignalK<T> {
    return setReceiver { obj, ctx ->
        ctx.assertServerSide()
        obj.receiver(ctx)
    }
}
