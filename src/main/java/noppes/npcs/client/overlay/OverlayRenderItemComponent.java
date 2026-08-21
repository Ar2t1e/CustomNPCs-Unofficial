package noppes.npcs.client.overlay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import noppes.npcs.api.overlay.IRenderItemOverlay;

public class OverlayRenderItemComponent implements IOverlayRenderComponent {

    private final int x;
    private final int y;
    private final int id;
    private final ItemStack item;

    public OverlayRenderItemComponent(IRenderItemOverlay itemIn) {
        x = itemIn.getPosX();
        y = itemIn.getPosY();
        id = itemIn.getId();
        item = itemIn.getItem().getMCItemStack();
    }

    public void render(int linkSide) {
        GlStateManager.pushMatrix();
        GlStateManager.translate((double)x / 1.2000000476837158D, (double)y / 1.2000000476837158D, (double)id / 1.2000000476837158D);
        GlStateManager.scale(1.2F, 1.2F, 1.0F);
        ScaledResolution sw = new ScaledResolution(Minecraft.getMinecraft());
        int width = (int)((float) sw.getScaledWidth_double() / 1.2F);
        int height = (int)((float) sw.getScaledHeight_double() / 1.2F);
        renderItemOverlay(linkSide, item, x, y, width, height);
        GlStateManager.popMatrix();
    }

    public void renderItemOverlay(int linkSide, ItemStack item, int x, int y, int width, int height) {
        int offsetX = width / 2 * ((linkSide - 1) % 3);
        int offsetY = height / 2 * ((linkSide - 1) / 3);
        Minecraft mc = Minecraft.getMinecraft();
        mc.getRenderItem().renderItemIntoGUI(item, x + offsetX, y + offsetY);
        mc.getRenderItem().renderItemAndEffectIntoGUI(mc.player, item, x + offsetX, y + offsetY);
    }

}
