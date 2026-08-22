package noppes.npcs.client.overlay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import noppes.npcs.api.constants.AlignmentType;
import noppes.npcs.api.overlay.IRenderItemOverlay;

import javax.annotation.Nonnull;

public class OverlayRenderItemComponent implements IOverlayRenderComponent {

    protected final int x;
    protected final int y;
    protected final int id;
    protected final float scale;
    protected final ItemStack item;
    protected AlignmentType alignment;
    protected final @Nonnull Minecraft minecraft;

    public OverlayRenderItemComponent(IRenderItemOverlay component) {
        x = component.getPosX();
        y = component.getPosY();
        id = component.getId();
        scale = component.getScale();
        item = component.getItem().getMCItemStack();
        alignment = AlignmentType.get(component.getAlignment());
        minecraft = Minecraft.getMinecraft();
    }

    public void render(int linkSide) {
        ScaledResolution sw = new ScaledResolution(Minecraft.getMinecraft());
        int widthWin = sw.getScaledWidth();
        int heightWin = sw.getScaledHeight();
        float xPos = x;
        float yPos = y;
        AlignmentType type = alignment != AlignmentType.NONE ? alignment : AlignmentType.get(linkSide);
        if (type != AlignmentType.NONE) {
            xPos += alignment.getOffsetX(widthWin / 2);
            yPos += alignment.getOffsetY(heightWin / 2);
        }
        GlStateManager.pushMatrix();
        GlStateManager.translate(xPos, yPos, (float) id * 0.01F);
        GlStateManager.scale(scale, scale, scale);
        minecraft.getRenderItem().renderItemIntoGUI(item, 0, 0);
        minecraft.getRenderItem().renderItemAndEffectIntoGUI(minecraft.player, item, 0, 0);
        GlStateManager.popMatrix();
    }

}
