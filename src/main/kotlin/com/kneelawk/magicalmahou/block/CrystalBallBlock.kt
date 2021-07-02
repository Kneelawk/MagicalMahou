package com.kneelawk.magicalmahou.block

import com.kneelawk.magicalmahou.screenhandler.CrystalBallScreenHandler
import com.kneelawk.magicalmahou.screenhandler.MMScreenHandlers
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.particle.ParticleTypes
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.SimpleNamedScreenHandlerFactory
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.*

class CrystalBallBlock(settings: Settings) : Block(settings) {
    override fun onUse(
        state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult
    ): ActionResult {
        return if (world.isClient) {
            ActionResult.SUCCESS
        } else {
            player.openHandledScreen(state.createScreenHandlerFactory(world, pos))
            ActionResult.CONSUME
        }
    }

    override fun createScreenHandlerFactory(
        state: BlockState, world: World, pos: BlockPos
    ): NamedScreenHandlerFactory {
        return MMScreenHandlers.createCrystalBallScreenHandlerFactory()
    }

    override fun randomDisplayTick(state: BlockState, world: World, pos: BlockPos, random: Random) {
        val x = pos.x.toDouble() + 0.45 + random.nextDouble() * 0.1
        val y = pos.y.toDouble() + 11.0 / 16.0 - 0.05 + random.nextDouble() * 0.1
        val z = pos.z.toDouble() + 0.45 + random.nextDouble() * 0.1
        if (random.nextInt(5) == 0) {
            world.addParticle(
                ParticleTypes.END_ROD, x, y, z, random.nextGaussian() * 0.005, random.nextGaussian() * 0.005,
                random.nextGaussian() * 0.005
            )
        }
    }
}