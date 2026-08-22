package noppes.npcs.client.overlay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.network.chat.Component;
import noppes.npcs.api.constants.AlignmentType;
import noppes.npcs.api.overlay.IOverlayLabel;

import javax.annotation.Nonnull;

public class OverlayLabelComponent implements IOverlayRenderComponent {

    protected final String text;
    protected final int x;
    protected final int y;
    protected final int id;
    protected final int color;
    protected final float scale;
    protected AlignmentType alignment;
    protected final @Nonnull Minecraft minecraft;

    public OverlayLabelComponent(IOverlayLabel component) {
        String textIn = component.getText();
        x = component.getPosX();
        y = component.getPosY();
        id = component.getId();
        color = component.getColor();
        scale = component.getScale();
        alignment = AlignmentType.get(component.getAlignment());
        Component tempText = Component.empty();
        if (textIn.contains("<br>") || textIn.contains("&t")) {
            String nl = textIn.contains("<br>") ? "<br>" : "&t";
            for (String s : textIn.split(nl)) { tempText.append(Component.translatable(s)); }
        }
        else { tempText.append(Component.translatable(textIn)); }
        text = tempText.getFormattedText();
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
        minecraft.fontRenderer.drawString(text, 0, 0, color);
        GlStateManager.popMatrix();
    }

}
