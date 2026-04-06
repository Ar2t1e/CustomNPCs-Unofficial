package noppes.npcs.client.gui.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import noppes.npcs.api.gui.ICustomGuiComponent;
import noppes.npcs.api.gui.IItemSlot;
import noppes.npcs.api.wrapper.gui.CustomGuiTexturedRectWrapper;
import noppes.npcs.api.wrapper.gui.GuiComponentsScrollableWrapper;
import noppes.npcs.mixin.client.IMouseHandlerMixin;
import noppes.npcs.shared.client.gui.components.custom.CustomGuiTexturedRect;
import noppes.npcs.shared.client.gui.listeners.PostRenderable;
import noppes.npcs.util.ValueUtil;

public class GuiCustomScrollingPanel extends GuiCustomComponents {

   protected int maxSize = 0;
   protected int scrollMaxHeight = 0;
   protected int scrollPercentage = 0;
   protected GuiCustom gui;
   protected boolean isScrolling = false;
   protected final CustomGuiTexturedRect scrollbar;
   protected final CustomGuiTexturedRect button;
   public GuiComponentsScrollableWrapper comps;

   public GuiCustomScrollingPanel() {
      scrollbar = new CustomGuiTexturedRect(null, (new CustomGuiTexturedRectWrapper(-1, resource.toString(), 0, 0, 14, 64, 65, 0)).setRepeatingTexture(14, 64, 1));
      button = new CustomGuiTexturedRect(null, new CustomGuiTexturedRectWrapper(-1, resource.toString(), 0, 0, 12, 15, 0, 214));
   }

   public void setComponents(GuiCustom guiIn, GuiComponentsScrollableWrapper compsIn) {
      super.setComponents(guiIn, compsIn);
      gui = guiIn;
      comps = compsIn;
      button.setX(compsIn.width - 13);
      scrollbar.setX(compsIn.width - 14);
      scrollbar.setHeight(compsIn.height);
      scrollMaxHeight = compsIn.height - 17;
      maxSize = compsIn.getComponents().stream().mapToInt((v) -> v.getPosY() + v.getHeight()).max().orElse(0);
      if (!canScroll()) {
         scrollPercentage = 0;
         compsIn.scrollAmount = 0;
      }
      else { setScrollAmount(scrollPercentage * (maxSize - compsIn.height) / 100); }
   }

   public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      PoseStack matrixStack = graphics.pose();
      mouseX -= comps.x;
      mouseY -= comps.y;
      matrixStack.pushPose();
      PoseStack posestack = RenderSystem.getModelViewStack();
      posestack.pushPose();
      posestack.translate((float)comps.x, (float)comps.y, 10.0F);
      RenderSystem.applyModelViewMatrix();
      if (canScroll()) {
         scrollbar.render(graphics, mouseX, mouseY, partialTicks);
         if (isScrolling) {
            if (((IMouseHandlerMixin) Minecraft.getInstance().mouseHandler).getActiveButton() == 0) {
               scrollPercentage = ValueUtil.correctInt((mouseY - 7) * 100 / scrollMaxHeight, 0, 100);
            }
            else { isScrolling = false; }
         }

         button.textureX = 0;
         if (scrollButtonHovered(mouseX, mouseY) || isScrolling) {
            button.textureX = 24;
         }

         button.setY(1 + scrollPercentage * scrollMaxHeight / 100);
         button.render(graphics, mouseX, mouseY, partialTicks);
         setScrollAmount(scrollPercentage * (maxSize - comps.height) / 100);
         matrixStack.translate(0.0F, (float)(-comps.scrollAmount), 0.0F);
         for (ICustomGuiComponent component : comps.getComponents()) {
            if (comps.isVisible(component)) {
               if (components.get(component.getId()) instanceof Renderable renderable) { renderable.render(graphics, mouseX, mouseY + comps.scrollAmount, partialTicks); }
            }
         }
         for (IItemSlot slot : slots) {
            if (comps.isVisible(slot) && slot.getGuiType() > 0) {
               renderSlot(graphics, slot);
            }
         }
         for (ICustomGuiComponent component : comps.getComponents()) {
            if (comps.isVisible(component)) {
               if (components.get(component.getId()) instanceof PostRenderable renderable) { renderable.postRender(graphics, mouseX, mouseY + comps.scrollAmount, partialTicks); }
            }
         }
      } else {
         super.render(graphics, mouseX, mouseY, partialTicks);
      }
      matrixStack.popPose();
      posestack.popPose();
      RenderSystem.applyModelViewMatrix();
   }

   private void setScrollAmount(int amount) {
      if (amount != comps.scrollAmount) {
         comps.scrollAmount = amount;
         gui.getMenu().update();
      }
   }

   public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
      mouseX -= comps.x;
      mouseY -= comps.y;
      if (!canScroll()) { return super.mouseClicked(mouseX, mouseY, mouseButton); }
      if (scrollBarHovered(mouseX, mouseY) && mouseButton == 0) {
         isScrolling = true;
         scrollPercentage = ValueUtil.correctInt((int)(mouseY - 7.0D) * 100 / scrollMaxHeight, 0, 100);
         return true;
      }
      boolean clicked = false;
      for (ICustomGuiComponent component : comps.getComponents()) {
         if (comps.isVisible(component) && components.get(component.getId()) instanceof GuiEventListener guiEvent
                 && guiEvent.mouseClicked(mouseX, mouseY + (double) comps.scrollAmount, mouseButton)) {
            if (mouseButton == 0) { draggingId = component.getId(); }
            clicked = true;
         }
      }
      return clicked;
   }

   public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dx, double dy) {
      if (!isScrolling && draggingId >= 0) {
         mouseX -= comps.x;
         mouseY -= comps.y;
         if (!canScroll()) { return super.mouseDragged(mouseX, mouseY, mouseButton, dx, dy); }
         for (ICustomGuiComponent component : comps.getComponents()) {
            if (comps.isVisible(component) && components.get(component.getId()) instanceof GuiEventListener guiEvent
                    && component.getId() == draggingId && guiEvent.mouseDragged(mouseX, mouseY + (double) comps.scrollAmount, mouseButton, dx, dy)) {
               return true;
            }
         }
      }
      return false;
   }

   public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
      mouseX -= comps.x;
      mouseY -= comps.y;
      if (!canScroll()) { return super.mouseReleased(mouseX, mouseY, mouseButton); }
      for (ICustomGuiComponent component : comps.getComponents()) {
         if (comps.isVisible(component) && components.get(component.getId()) instanceof GuiEventListener guiEvent
                 && component.getId() == draggingId && guiEvent.mouseReleased(mouseX, mouseY + (double) comps.scrollAmount, mouseButton)) {
            draggingId = -1;
            return true;
         }
      }
      draggingId = -1;
      return false;
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double mouseScrolled) {
      if (mouseScrolled != 0.0D && panelHovered(mouseX - (double)comps.x, mouseY - (double)comps.y)) {
         scrollPercentage += mouseScrolled > 0.0D ? -4 : 4;
         scrollPercentage = ValueUtil.correctInt(scrollPercentage, 0, 100);
         return true;
      }
      return false;
   }

   public boolean canScroll() { return maxSize > comps.height; }

   public boolean panelHovered(double x, double y) {
      return canScroll() && x >= 0.0D && y >= 0.0D && x < (double)comps.width && y < (double)comps.height;
   }

   private boolean scrollBarHovered(double x, double y) {
      return panelHovered(x, y) && x >= (double)scrollbar.getX() && y >= (double)scrollbar.getY() && x < (double)(scrollbar.getY() + scrollbar.getWidth()) && y < (double)(scrollbar.getY() + scrollbar.getHeight());
   }

   private boolean scrollButtonHovered(double x, double y) {
      return scrollBarHovered(x, y) && y > (double)button.getY() && y < (double)(button.getY() + 15);
   }

   public void setMaxSize(int size) { maxSize = size; }

}
