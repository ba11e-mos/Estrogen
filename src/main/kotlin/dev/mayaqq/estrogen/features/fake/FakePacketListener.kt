package dev.mayaqq.estrogen.features.fake

import com.mojang.authlib.GameProfile
import dev.mayaqq.cynosure.helpers.McClient
import net.minecraft.client.multiplayer.ClientPacketListener
import net.minecraft.core.RegistryAccess
import net.minecraft.core.registries.Registries
import net.minecraft.data.registries.VanillaRegistries
import net.minecraft.network.Connection
import net.minecraft.network.protocol.PacketFlow
import net.minecraft.network.protocol.game.ClientboundLoginPacket
import net.minecraft.server.RegistryLayer
import net.minecraft.world.damagesource.DamageTypes
import net.minecraft.world.level.biome.Biomes

class FakePacketListener(profile: GameProfile) : ClientPacketListener(
    McClient,
    null,
    Connection(PacketFlow.CLIENTBOUND),
    null,
    profile,
    McClient.telemetryManager.createWorldSessionManager(false, null, null)
) {
    override fun registryAccess(): RegistryAccess {
        val vanilla = VanillaRegistries.createLookup()
        val damageTypes = StaticRegistry(Registries.DAMAGE_TYPE, DamageTypes.FELL_OUT_OF_WORLD, vanilla.lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(DamageTypes.FELL_OUT_OF_WORLD).value())
        val biomes = StaticRegistry(Registries.BIOME, Biomes.THE_VOID, vanilla.lookupOrThrow(Registries.BIOME).getOrThrow(Biomes.THE_VOID).value())

        return RegistryLayer.createRegistryAccess()
            .replaceFrom(RegistryLayer.WORLDGEN, listOf(StaticRegistry.Frozen(biomes)))
            .replaceFrom(RegistryLayer.RELOADABLE, listOf(StaticRegistry.Frozen(damageTypes)))
            .compositeAccess()
    }

    override fun handleLogin(packet: ClientboundLoginPacket) {}
}