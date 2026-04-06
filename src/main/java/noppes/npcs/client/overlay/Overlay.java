package noppes.npcs.client.overlay;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Queue;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.overlay.ILabel;
import noppes.npcs.api.overlay.IOverlay;
import noppes.npcs.api.overlay.IOverlayComponent;
import noppes.npcs.api.overlay.IRenderItemOverlay;
import noppes.npcs.api.overlay.ITexturedRect;
import noppes.npcs.shared.client.gui.listeners.custom.IComponentCustomGui;

public class Overlay {

   private final Queue<IOverlayRenderComponent> components = new ArrayDeque<>();
   private final CompoundTag nbt;
   private final int linkSide;

   public Overlay(IOverlay overlay) {
      linkSide = overlay.getLinkSide();
      nbt = overlay.save().getMCNBT();
      for (IOverlayComponent component : overlay.getComponents().stream().sorted(Comparator.comparingInt(IOverlayComponent::getId)).toList()) {
         if (component instanceof ILabel) { components.add(new OverlayLabelComponent((ILabel) component)); }
         else if (component instanceof IRenderItemOverlay) { components.add(new OverlayRenderItemComponent((IRenderItemOverlay) component)); }
         else if (component instanceof ITexturedRect) { components.add(new OverlayTexturedRectComponent((ITexturedRect) component)); }
      }
   }

   public void render(GuiGraphics graphics) {
      graphics.pose().pushPose();
      for (IOverlayRenderComponent component : components) { component.render(graphics, linkSide); }
      graphics.pose().popPose();
   }

   public CompoundTag getNBT() { return nbt; }

}
