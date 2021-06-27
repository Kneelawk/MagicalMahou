package com.kneelawk.magicalmahou.image

enum class PlayerSkinModel(val modelStr: String) {
    DEFAULT("default"),
    SLIM("slim");

    companion object {
        private val VALUES = values()
        private const val MIN_VALUE = 0
        private val MAX_VALUE = VALUES.size - 1

        fun byId(id: Int): PlayerSkinModel {
            return VALUES[id.coerceIn(MIN_VALUE, MAX_VALUE)]
        }

        private val MODEL_STRS = hashMapOf<String, PlayerSkinModel>()

        init {
            for (value in VALUES) {
                MODEL_STRS[value.modelStr] = value
            }
        }

        fun byModelStr(modelStr: String): PlayerSkinModel? {
            return MODEL_STRS[modelStr]
        }
    }

    val id = ordinal
}