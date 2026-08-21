package noppes.npcs.shared.client.gui.components.custom;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.network.chat.Component;
import noppes.npcs.api.gui.ICustomGuiComponent;
import noppes.npcs.api.wrapper.gui.CustomGuiColoredLineWrapper;
import noppes.npcs.client.gui.custom.GuiCustom;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import org.lwjgl.opengl.GL11;

public class CustomGuiColoredLine extends GuiLabel implements IComponentCustomGui {

   public CustomGuiColoredLineWrapper component;

   public CustomGuiColoredLine(GuiCustom parent, CustomGuiColoredLineWrapper componentIn) {
      super(parent, componentIn.getId(), Component.empty(), componentIn.getPosX(), componentIn.getPosY());
      component = componentIn;
      init();
   }

   @Override
   public void init() {
      id = component.getId();
      setX(component.getPosX());
      setY(component.getPosY());
      setWidth(component.getXEnd() - component.getPosX());
      setHeight(component.getYEnd() - component.getPosY());
      enabled = true;
      visible = component.getVisible();
      hoverText.clear();
   }

   @Override
   public void render(int mouseX, int mouseY, float partialTicks) {
      if (!enabled || !visible) { return; }
      int color = component.getColor();
      int r = color >> 24 & 255;
      int g = color >> 16 & 255;
      int b = color >> 8 & 255;
      int a = color & 255;
      double dx = component.getXEnd() - getX();
      double dy = component.getYEnd() - getY();
      double length = Math.sqrt(dx * dx + dy * dy);
      double nx = -dy / length * component.getThickness() / 2.0D;
      double ny = dx / length * component.getThickness() / 2.0D;
      GlStateManager.enableBlend();
      GlStateManager.disableTexture2D();
      GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder buffer = tessellator.getBuffer();
      buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
      buffer.pos(component.getXEnd() + nx, component.getYEnd() + ny, id).color(r, g, b, a).endVertex();
      buffer.pos(component.getXEnd() - nx, component.getYEnd() - ny, id).color(r, g, b, a).endVertex();
      buffer.pos((double) getX() - nx, (double) getY() - ny, id).color(r, g, b, a).endVertex();
      buffer.pos((double) getX() + nx, (double) getY() + ny, id).color(r, g, b, a).endVertex();
      tessellator.draw();
      GlStateManager.enableTexture2D();
      GlStateManager.disableBlend();
   }

   @Override
   public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dx, double dy) {
      return true;
   }

   @Override
   public ICustomGuiComponent component() { return component; }

}
