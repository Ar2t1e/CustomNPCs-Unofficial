package noppes.npcs.client.overlay;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Queue;
import java.util.stream.Collectors;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noppes.npcs.api.gui.ILabel;
import noppes.npcs.api.gui.ITexturedRect;
import noppes.npcs.api.overlay.*;

@SideOnly(Side.CLIENT)
public class Overlay {

    private final Queue<IOverlayRenderComponent> components = new ArrayDeque<>();
    private final NBTTagCompound nbt;
    private final int linkSide;

    public Overlay(IOverlay overlay) {
        linkSide = overlay.getLinkSide();
        nbt = overlay.save().getMCNBT();
        for (IOverlayComponent component : overlay.getComponents().stream().sorted(Comparator.comparingInt(IOverlayComponent::getId)).collect(Collectors.toList())) {
            if (component instanceof ILabel) { components.add(new OverlayLabelComponent((ILabel) component)); }
            else if (component instanceof IRenderItemOverlay) { components.add(new OverlayRenderItemComponent((IRenderItemOverlay) component)); }
            else if (component instanceof ITexturedRect) { components.add(new OverlayTexturedRectComponent((ITexturedRect) component)); }
        }
    }

    public void render(Minecraft mc, int mouseX, int mouseY, int mouseWheel, float partialTicks) {
        GlStateManager.pushMatrix();
        for (IOverlayRenderComponent component : components) { component.render(linkSide); }
        GlStateManager.popMatrix();
    }

    public NBTTagCompound getNBT() { return nbt; }

}
