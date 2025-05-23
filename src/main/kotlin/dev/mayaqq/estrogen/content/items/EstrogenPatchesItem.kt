package dev.mayaqq.estrogen.content.items

import dev.mayaqq.estrogen.config.EstrogenServerConfig
import dev.mayaqq.estrogen.content.EstrogenEffects
import dev.mayaqq.estrogen.utils.EstrogenColors
import earth.terrarium.baubly.common.Bauble
import earth.terrarium.baubly.common.SlotInfo
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.level.Level


class EstrogenPatchesItem(properties: Properties) : Item(properties), Bauble {
    override fun tick(stack: ItemStack, slot: SlotInfo) {
        val itemFluidManager: ItemFluidContainer = getFluidContainer(stack)
        val level: Level = slot.wearer().level()
        if (!level.isClientSide && slot.wearer() is Player && itemFluidManager.getFluids().get(0)
                .getFluidAmount() > 0
        ) {
            if ((level.gameTime % TRIGGER_EVERY_X_TICKS).toInt() == 0) {
                addEffect(slot.wearer() as Player, level)
            }
            if (EstrogenServerConfig.Patch.drain && level.gameTime % EstrogenServerConfig.Patch.patchDrainAmount.get() === 0 && !player.isCreative()) {
                itemFluidManager.extractFromSlot(
                    0,
                    FluidHolder.of(EstrogenFluids.LIQUID_ESTROGEN.get(), FluidConstants.getBucketAmount() / 1000),
                    false
                )
                itemFluidManager.serialize(stack.getOrCreateTag())
            }
        }
    }

    private fun addEffect(player: Player, level: Level) {
        player.addEffect(
            MobEffectInstance(
                EstrogenEffects.Estrogen,
                EFFECT_DURATION,
                EstrogenServerConfig.Patch.girlPowerLevel - 1,
                false,
                false,
                false
            )
        )
    }

    fun getMaxCapacity(stack: ItemStack): Long {
        return FluidConstants.getBucketAmount() + ((FluidConstants.getBucketAmount() / 2) * EnchantmentHelper.getEnchantments(
            stack
        ).getOrDefault(AllEnchantments.CAPACITY.get(), 0))
    }

    override fun appendHoverText(
        stack: ItemStack,
        level: Level,
        tooltipComponents: MutableList<Component>,
        isAdvanced: TooltipFlag
    ) {
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced)
        val holder: ItemStackHolder = ItemStackHolder(stack)
        val itemFluidManager: ItemFluidContainer = FluidContainer.of(holder)
        if (itemFluidManager != null) {
            val amount: Long = FluidConstants.toMillibuckets(itemFluidManager.getFluids().get(0).getFluidAmount())
            val amountCapacity: Long = FluidConstants.toMillibuckets(itemFluidManager.getTankCapacity(0))
            val fluidString: String = Component.translatable("fluid_type.estrogen.liquid_estrogen").getString()
            tooltipComponents.add(Component.literal(" "))
            tooltipComponents.add(
                Component.literal(String.format("%s: %smb / %smb", fluidString, amount, amountCapacity)).setStyle(
                    Style.EMPTY.withColor(
                        ChatFormatting.GRAY
                    )
                )
            )
            tooltipComponents.add(Component.literal(" "))
        }
    }


    override fun onEquip(stack: ItemStack, slot: SlotInfo) {
        val level: Level = slot.wearer().level()
        val itemFluidManager: ItemFluidContainer = getFluidContainer(stack)
        if (!level.isClientSide && slot.wearer() is Player && itemFluidManager.getFluids().get(0)
                .getFluidAmount() > 0
        ) {
            addEffect(slot.wearer() as Player, level)
        }
    }

    fun getFullStack(): ItemStack {
        val stack = this.defaultInstance
        val itemFluidManager: ItemFluidContainer = getFluidContainer(stack)
        itemFluidManager.insertFluid(
            FluidHolder.of(
                EstrogenFluids.LIQUID_ESTROGEN.get(),
                FluidConstants.getBucketAmount()
            ), false
        )
        itemFluidManager.serialize(stack.getOrCreateTag())
        return stack
    }

    private fun getAmount(stack: ItemStack): Long {
        val holder: ItemStackHolder = ItemStackHolder(stack)
        val itemFluidManager: ItemFluidContainer = FluidContainer.of(holder)
        return itemFluidManager.getFluids().get(0).getFluidAmount()
    }

    override fun getFluidContainer(stack: ItemStack): WrappedItemFluidContainer {
        return WrappedItemFluidContainer(stack, SimpleFluidContainer(getMaxCapacity(stack), 1) { amount, fluid ->
            fluid.`is`(
                EstrogenFluids.LIQUID_ESTROGEN.get()
            )
        })
    }

    override fun isBarVisible(stack: ItemStack): Boolean = getAmount(stack) != getMaxCapacity(stack)
    override fun getBarWidth(stack: ItemStack): Int = (getAmount(stack).toDouble() / getMaxCapacity(stack) * 13).toInt()
    override fun getBarColor(stack: ItemStack): Int = EstrogenColors.ESTROGEN_PATCHES_BAR.toInt()

    companion object {
        private const val TRIGGER_EVERY_X_TICKS: Int = 300
        private const val EFFECT_DURATION: Int = TRIGGER_EVERY_X_TICKS + 220
    }
}