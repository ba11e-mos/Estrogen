package dev.mayaqq.estrogen.features.fake

import com.mojang.authlib.GameProfile
import dev.mayaqq.cynosure.helpers.McClient
import net.minecraft.client.ClientRecipeBook
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.multiplayer.ClientPacketListener
import net.minecraft.client.multiplayer.PlayerInfo
import net.minecraft.client.player.LocalPlayer
import net.minecraft.resources.ResourceLocation
import net.minecraft.stats.StatsCounter
import net.minecraft.world.entity.player.PlayerModelPart

class FakePlayer(
    level: ClientLevel,
    connection: ClientPacketListener,
) : LocalPlayer(
    McClient,
    level,
    connection,
    StatsCounter(),
    ClientRecipeBook(),
    false,
    false
) {

    var skinLocation: ResourceLocation = McClient.skinManager.getInsecureSkinLocation(this.gameProfile)

    init {
        McClient.gameRenderer.mainCamera.setup(level, this, true, true, 0f)
        McClient.entityRenderDispatcher.prepare(level, McClient.gameRenderer.mainCamera, this)
    }

    override fun getSkinTextureLocation(): ResourceLocation = skinLocation
    override fun isModelPartShown(model: PlayerModelPart): Boolean = true
    override fun getPlayerInfo(): PlayerInfo? = null

    companion object {
        fun create(profile: GameProfile) = FakePacketListener(profile).let { FakePlayer(FakeLevel(it), it) }
    }
}