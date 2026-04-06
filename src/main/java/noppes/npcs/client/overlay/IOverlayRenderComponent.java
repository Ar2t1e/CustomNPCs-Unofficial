package noppes.npcs.client.overlay;

import net.minecraft.client.gui.GuiGraphics;

interface IOverlayRenderComponent {

   void render(GuiGraphics graphics, int linkSide);

}
