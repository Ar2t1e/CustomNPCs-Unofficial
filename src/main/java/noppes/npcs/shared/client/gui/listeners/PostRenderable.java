package noppes.npcs.shared.client.gui.listeners;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public interface PostRenderable {

    void postRender(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks);

}
