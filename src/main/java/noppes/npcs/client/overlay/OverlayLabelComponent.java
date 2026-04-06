package noppes.npcs.client.overlay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.network.chat.Component;
import noppes.npcs.api.gui.ILabel;

import java.awt.*;

public class OverlayLabelComponent implements IOverlayRenderComponent {

    private final String text;
    private final int x;
    private final int y;
    private final int id;
    private final float scale;

    public OverlayLabelComponent(ILabel label) {
        String textIn = label.getText();
        x = label.getPosX();
        y = label.getPosY();
        id = label.getId();
        scale = label.getScale();
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : textIn.split("&t")) {
            stringBuilder.append(Component.translatable(s));
        }
        text = stringBuilder.toString();
    }

    public void render(int linkSide) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, (double)id);
        GlStateManager.scale(scale, scale, scale);
        ScaledResolution sw = new ScaledResolution(Minecraft.getMinecraft());
        renderString(text, x, y, linkSide, sw.getScaledWidth(), sw.getScaledHeight());
        GlStateManager.popMatrix();
    }

    public void renderString(String text, int x, int y, int linkSide, int width, int height) {
        int offsetX = width / 2 * ((linkSide - 1) % 3);
        int offsetY = height / 2 * ((linkSide - 1) / 3);
        Minecraft.getMinecraft().fontRenderer.drawString(text, x + offsetX, y + offsetY, new Color(0xFFFFFF).getRGB());
    }

}
