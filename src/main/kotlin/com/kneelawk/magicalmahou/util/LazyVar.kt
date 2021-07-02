package com.kneelawk.magicalmahou.util

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class LazyVar<T>(initializer: () -> T, private val onSet: ((T) -> Unit)? = null) : ReadWriteProperty<Any?, T> {
    private var initializer: (() -> T)? = initializer
    private var value: Any? = Uninitialized

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return if (value == Uninitialized) {
            synchronized(this) {
                if (value == Uninitialized) {
                    return initializer!!().also {
                        value = it
                        initializer = null
                    }
                } else {
                    value as T
                }
            }
        } else {
            value as T
        }
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        onSet?.invoke(value)
        synchronized(this) {
            this.value = value
        }
    }

    private object Uninitialized
}

fun <T> lazyVar(initializer: () -> T, onSet: ((T) -> Unit)? = null): LazyVar<T> = LazyVar(initializer, onSet)
