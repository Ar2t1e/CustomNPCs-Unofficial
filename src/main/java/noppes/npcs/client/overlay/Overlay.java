package noppes.npcs.client.overlay;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Queue;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import noppes.npcs.api.overlay.IOverlayLabel;
import noppes.npcs.api.overlay.IOverlay;
import noppes.npcs.api.overlay.IOverlayComponent;
import noppes.npcs.api.overlay.IRenderItemOverlay;
import noppes.npcs.api.overlay.IOverlayTexturedRect;

public class Overlay {

   private final Queue<IOverlayRenderComponent> components = new ArrayDeque<>();
   private final CompoundTag nbt;
   private final int linkSide;

   public Overlay(IOverlay overlay) {
      linkSide = overlay.getLinkSide();
      nbt = overlay.save().getMCNBT();
      for (IOverlayComponent component : overlay.getComponents().stream().sorted(Comparator.comparingInt(IOverlayComponent::getId)).toList()) {
         if (component instanceof IOverlayLabel) { components.add(new OverlayLabelComponent((IOverlayLabel) component)); }
         else if (component instanceof IRenderItemOverlay) { components.add(new OverlayRenderItemComponent((IRenderItemOverlay) component)); }
         else if (component instanceof IOverlayTexturedRect) { components.add(new OverlayTexturedRectComponent((IOverlayTexturedRect) component)); }
      }
   }

   public void render(GuiGraphics graphics) {
      graphics.pose().pushPose();
      for (IOverlayRenderComponent component : components) { component.render(graphics, linkSide); }
      graphics.pose().popPose();
   }

   public CompoundTag getNBT() { return nbt; }

}
