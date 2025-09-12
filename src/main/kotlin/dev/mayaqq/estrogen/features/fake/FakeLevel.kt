package dev.mayaqq.estrogen.features.fake

import dev.mayaqq.cynosure.helpers.McClient
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.RegistryAccess
import net.minecraft.core.registries.Registries
import net.minecraft.data.registries.VanillaRegistries
import net.minecraft.server.RegistryLayer
import net.minecraft.world.Difficulty
import net.minecraft.world.damagesource.DamageType
import net.minecraft.world.damagesource.DamageTypes
import net.minecraft.world.level.biome.Biomes
import net.minecraft.world.level.dimension.BuiltinDimensionTypes

class FakeLevel(listener: FakePacketListener) : ClientLevel(
    listener,
    ClientLevelData(Difficulty.NORMAL, false, false),
    OVERWORLD,
    VanillaRegistries.createLookup().lookupOrThrow(Registries.DIMENSION_TYPE).getOrThrow(BuiltinDimensionTypes.OVERWORLD),
    0,
    0,
    { McClient.profiler },
    McClient.levelRenderer,
    false,
    0
)