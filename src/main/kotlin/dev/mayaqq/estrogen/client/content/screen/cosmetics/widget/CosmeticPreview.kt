package dev.mayaqq.estrogen.client.content.screen.cosmetics.widget

import dev.mayaqq.cynosure.helpers.McClient
import dev.mayaqq.cynosure.text.CommonText
import dev.mayaqq.cynosure.text.unaryMinus
import dev.mayaqq.estrogen.client.content.screen.EstrogenButton
import dev.mayaqq.estrogen.client.content.screen.EstrogenMenuScreen
import dev.mayaqq.estrogen.features.fake.FakePlayer
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.InventoryScreen
import net.minecraft.util.Mth
import org.joml.Quaternionf

class CosmeticPreview(x: Int, y: Int, width: Int, height: Int) : EstrogenButton(
    x, y, width, height,
    arrayOf(),
    OnPress {},
    DEFAULT_NARRATION,
    EstrogenMenuScreen.transBlue,
    false, true
) {

    private var rotation = Mth.PI / 4

    override fun renderWidget(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        super.renderWidget(graphics, mouseX, mouseY, partialTick)

        if (this.message == CommonText.EMPTY) this.message = -"gui.estrogen.cosmetics.no_preview"
        var og = McClient.player
        McClient.player = fakePlayer

        val quaternion = Quaternionf().rotateZ(Mth.PI).rotateY(rotation)
        val yHeadRot: Float = fakePlayer.yBodyRot
        val yRot: Float = fakePlayer.yRot
        val xRot: Float = fakePlayer.xRot
        val yHeadRotO: Float = fakePlayer.yHeadRotO
        val yBodyRot: Float = fakePlayer.yHeadRot
        fakePlayer.yBodyRot = 180.0f
        fakePlayer.yRot = 180.0f
        fakePlayer.xRot = 0f
        fakePlayer.yHeadRot = fakePlayer.yRot
        fakePlayer.yHeadRotO = fakePlayer.yRot
        InventoryScreen.renderEntityInInventory(
            graphics,
            (x + getWidth() / 2f).toInt(), y + getHeight() - 20,
            (getHeight() / 2.5f).toInt(), quaternion, null, McClient.player
        )
        fakePlayer.yBodyRot = yHeadRot
        fakePlayer.yRot = yRot
        fakePlayer.xRot = xRot
        fakePlayer.yHeadRot = yHeadRotO
        fakePlayer.yHeadRotO = yBodyRot
        McClient.player = og
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean = isMouseOver(mouseX, mouseY) && isValidClickButton(button)


    override fun mouseDragged(d: Double, e: Double, i: Int, f: Double, g: Double): Boolean {
        this.rotation += f.toFloat() * 0.15f
        return true
    }

    companion object {
        val fakePlayer = FakePlayer.create(McClient.user.gameProfile)
    }
}