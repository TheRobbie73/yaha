package org.robbie.yaha.features.spells

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getBlockPos
import at.petrak.hexcasting.api.casting.getItemEntity
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadBlock
import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.command.argument.BlockStateArgument
import net.minecraft.entity.ItemEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.state.property.Property
import net.minecraft.util.math.BlockPos

object OpSussifyBlock : SpellAction {
    override val argc = 2

    override fun execute(
        args: List<Iota>,
        env: CastingEnvironment
    ): SpellAction.Result {
        val pos = args.getBlockPos(0, argc)
        env.assertPosInRangeForEditing(pos)
        val item = args.getItemEntity(1, argc)
        env.assertEntityInRange(item)

        val block = env.world.getBlockState(pos).block
        val brushBlock = when (block) {
            Blocks.SAND -> Blocks.SUSPICIOUS_SAND
            Blocks.GRAVEL -> Blocks.SUSPICIOUS_GRAVEL
            else -> throw MishapBadBlock.of(pos, "yaha:sussifiable")
        }

        return SpellAction.Result(
            Spell(pos, brushBlock, item),
            MediaConstants.DUST_UNIT / 8,
            listOf(ParticleSpray.cloud(pos.toCenterPos(), 1.0))
        )
    }

    private data class Spell(val pos: BlockPos, val brushBlock: Block, val item: ItemEntity) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            if (!env.canEditBlockAt(pos)) return

            if (!IXplatAbstractions.INSTANCE.isPlacingAllowed(
                    env.world,
                    pos,
                    ItemStack(brushBlock),
                    env.castingEntity as? ServerPlayerEntity
            )) return

            val blockNbt = NbtCompound()
            blockNbt.put("item", item.stack.writeNbt(NbtCompound()))

            val blockWithNbt = BlockStateArgument(
                brushBlock.defaultState,
                mutableSetOf<Property<*>>(),
                blockNbt
            )

            if (blockWithNbt.setBlockState(env.world, pos, Block.NOTIFY_ALL))
                item.discard()
        }
    }
}