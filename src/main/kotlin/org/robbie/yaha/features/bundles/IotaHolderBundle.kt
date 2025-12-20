package org.robbie.yaha.features.bundles

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.item.IotaHolderItem
import at.petrak.hexcasting.api.utils.getList
import net.minecraft.client.item.TooltipContext
import net.minecraft.client.item.TooltipData
import net.minecraft.entity.Entity
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.StackReference
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsage
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList
import net.minecraft.screen.slot.Slot
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundEvents
import net.minecraft.stat.Stats
import net.minecraft.text.Text
import net.minecraft.util.ClickType
import net.minecraft.util.Formatting
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import org.robbie.yaha.Yaha
import org.robbie.yaha.registry.YahaCardinalComponents
import java.util.Optional
import java.util.function.Predicate
import java.util.stream.Stream

val ITEM_BAR_COLOR = MathHelper.packRgb(0.4f, 0.4f, 1.0f)

/**
 * Holds 16 individual items that satisfy the given filter.
 * Reading this bundle picks and reads a random item inside, or null if no non-empty readable item can be found.
 */
class IotaHolderBundle(settings: Settings, val filter: Predicate<Item>) : Item(settings), IotaHolderItem {
    override fun onStackClicked(stack: ItemStack, slot: Slot, clickType: ClickType, player: PlayerEntity): Boolean {
        if (clickType != ClickType.RIGHT) return false
        val slotItem = slot.stack
        if (slotItem.isEmpty) {
            removeFirst(stack)?.let {
                slot.insertStack(it)
                playRemoveOneSound(player)
            }
        } else if (slotItem.item.canBeNested() && addOneToBundle(stack, slotItem)) {
            slotItem.decrement(1)
            playInsertSound(player)
        }
        return true
    }

    override fun onClicked(
        stack: ItemStack,
        otherStack: ItemStack,
        slot: Slot,
        clickType: ClickType,
        player: PlayerEntity,
        cursorStackReference: StackReference
    ): Boolean {
        if (clickType != ClickType.RIGHT) return false
        if (otherStack.isEmpty) {
            removeSelected(stack, player)?.let {
                cursorStackReference.set(it)
                playRemoveOneSound(player)
            }
        } else if (addOneToBundle(stack, otherStack)) {
            otherStack.decrement(1)
            playInsertSound(player)
        }
        return true
    }

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val bundle = user.getStackInHand(hand)
        if (dropAll(bundle, user)) {
            playDropContentsSound(user)
            user.incrementStat(Stats.USED.getOrCreateStat(this))
            return TypedActionResult.success(bundle, world.isClient)
        }
        return TypedActionResult.fail(bundle)
    }

    override fun isItemBarVisible(stack: ItemStack) = getBundleOccupancy(stack) > 0

    override fun getItemBarStep(stack: ItemStack) = (1 + 12 * getBundleOccupancy(stack) / MAX_COUNT)
        .coerceAtMost(13)

    override fun getItemBarColor(stack: ItemStack) = ITEM_BAR_COLOR

    override fun getTooltipData(stack: ItemStack): Optional<TooltipData> {
        val defaultedList = DefaultedList.of<ItemStack>()
        getBundledStacks(stack).forEach { defaultedList.add(it) }
        return Optional.of(IotaBundleTooltipData(defaultedList))
    }

    override fun appendTooltip(stack: ItemStack, world: World?, tooltip: List<Text>, context: TooltipContext) {
        (tooltip as MutableList).add(
            Text.translatable(
                "item.minecraft.bundle.fullness",
                getBundleOccupancy(stack),
                MAX_COUNT).formatted(Formatting.GRAY)
        )
    }

    override fun onItemEntityDestroyed(entity: ItemEntity) {
        ItemUsage.spawnItemContents(entity, getBundledStacks(entity.stack))
    }

    /**
     * Choose a random item in the bundle and return the iota tag it holds.
     * Items with no iota tag can be chosen, and return a NullIota.
     * If the bundle is empty, this returns null.
     */
    override fun readIotaTag(stack: ItemStack): NbtCompound? {
        val itemList = getBundledStacks(stack).toList()
        if (itemList.isEmpty()) return null
        val chosenItem = itemList.elementAt(Yaha.RANDOM.nextInt(itemList.count()))
        return (chosenItem.item as? IotaHolderItem)?.readIotaTag(chosenItem)
            ?: IotaType.serialize(NullIota())
    }

    override fun writeable(stack: ItemStack?) = false
    override fun canWrite(stack: ItemStack?, iota: Iota?) = false
    override fun writeDatum(stack: ItemStack?, iota: Iota?) {}

    /**
     * Attempts to add one item of the given stack to the bundle
     * and returns if it was successful. Items are added to the beginning of the list
     */
    fun addOneToBundle(bundle: ItemStack, stack: ItemStack): Boolean {
        if (getBundleOccupancy(bundle) == MAX_COUNT || !filter.test(stack.item)) return false
        val bundleNbt = bundle.orCreateNbt // kotlin removes the "get" lol
        if (!bundleNbt.contains("Items")) bundleNbt.put("Items", NbtList())
        val listNbt = bundleNbt.getList("Items", NbtElement.COMPOUND_TYPE)
        val item = stack.copyWithCount(1)
        val itemNbt = NbtCompound()
        item.writeNbt(itemNbt)
        listNbt.add(0, itemNbt)
        return true
    }

    /**
     * Removes the first item in the bundle and returns it.
     * Returns null if the bundle was empty.
     */
    fun removeFirst(bundle: ItemStack): ItemStack? {
        val bundleNbt = bundle.orCreateNbt
        val listNbt = bundleNbt.getList("Items", NbtElement.COMPOUND_TYPE)
        return listNbt.removeFirstOrNull()?.let { ItemStack.fromNbt(it as NbtCompound) }
    }

    /**
     * Removes the item selected by the player and returns it.
     * Returns null if either the bundle was empty
     * or the player has selected outside the list of items. somehow.
     */
    fun removeSelected(bundle: ItemStack, player: PlayerEntity): ItemStack? {
        val selected = YahaCardinalComponents.BUNDLE_SELECT.get(player).selected
        val bundleNbt = bundle.orCreateNbt
        val listNbt = bundleNbt.getList("Items", NbtElement.COMPOUND_TYPE)
        if (selected >= listNbt.size) return null
        return ItemStack.fromNbt(listNbt.removeAt(selected) as NbtCompound)
    }

    /**
     * Drops all items in the bundle. bleeeehh
     * Returns if dropping was successful (bundle wasn't empty)
     */
    fun dropAll(bundle: ItemStack, player: PlayerEntity): Boolean {
        val bundleNbt = bundle.orCreateNbt
        val listNbt = bundleNbt.getList("Items", NbtElement.COMPOUND_TYPE)
        if (listNbt.isEmpty()) return false
        if (player is ServerPlayerEntity) {
            for (itemNbt in listNbt) {
                val itemStack = ItemStack.fromNbt(itemNbt as NbtCompound)
                player.dropItem(itemStack, true)
            }
        }
        bundle.removeSubNbt("Items")
        return true
    }

    fun playRemoveOneSound(entity: Entity) {
        entity.playSound(
            SoundEvents.ITEM_BUNDLE_REMOVE_ONE,
            0.8f,
            0.8f + entity.world.random.nextFloat() * 0.4f
        )
    }

    fun playInsertSound(entity: Entity) {
        entity.playSound(
            SoundEvents.ITEM_BUNDLE_INSERT,
            0.8f,
            0.8f + entity.world.random.nextFloat() * 0.4f
        )
    }

    fun playDropContentsSound(entity: Entity) {
        entity.playSound(
            SoundEvents.ITEM_BUNDLE_DROP_CONTENTS,
            0.8f,
            0.8f + entity.world.random.nextFloat() * 0.4f
        )
    }

    companion object {
        // these functions are here so they can be used by the item model
        const val MAX_COUNT = 16

        fun getBundledStacks(bundle: ItemStack): Stream<ItemStack> {
            val bundleNbt = bundle.nbt ?: return Stream.empty()
            val listNbt = bundleNbt.getList("Items", NbtElement.COMPOUND_TYPE)
            return listNbt.stream().map { ItemStack.fromNbt(it as NbtCompound) }
        }

        fun getBundleOccupancy(bundle: ItemStack) = getBundledStacks(bundle).count().toInt()
    }
}