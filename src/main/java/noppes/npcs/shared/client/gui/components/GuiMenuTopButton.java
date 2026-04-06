package noppes.npcs.shared.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.constants.GuiComponentType;
import noppes.npcs.shared.client.gui.listeners.IGuiInterface;

import javax.annotation.Nonnull;

public class GuiMenuTopButton extends GuiButtonNop {

   public static final ResourceLocation resource = new ResourceLocation(CustomNpcs.MODID, "textures/gui/menutopbutton.png");
   public boolean rotated = false;

   public GuiMenuTopButton(IGuiInterface gui, int id, Object label, int x, int y) {
      super(gui, id, label, x, y, null);
      active = false;
      height = 20;
      width = Minecraft.getInstance().font.width(getMessage()) + 12;
   }

   @Override
   public void renderWidget(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      if (visible) {
         PoseStack matrixStack = graphics.pose();
         matrixStack.pushPose();
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.setShaderTexture(0, resource);
         int h = height - (active ? 0 : 2);
         isHovered = mouseX >= getX() && mouseY >= getY() && mouseX < getX() + getWidth() && mouseY < getY() + h;
         int state = active ? 0 : (isHovered ? 2 : 1);
         graphics.blit(resource, getX(), getY(), 0, state * 20, getWidth() / 2, h);
         graphics.blit(resource, getX() + getWidth() / 2, getY(), 200 - getWidth() / 2, state * 20, getWidth() / 2, h);
         if (rotated) { matrixStack.mulPose(Axis.XP.rotationDegrees(90.0F)); }
         renderString(graphics, getMessage(), getX() + 2, getY(), getX() + getWidth() - 2, getY() + getHeight(),
                 getFGColor() | Mth.ceil(alpha * 255.0F) << 24, showShadow, true, customFont);
         matrixStack.popPose();
      }
   }

   @Override
   public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dx, double dy) { return false; }

   @Override
   public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) { return false; }

   @Override
   public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
      boolean bo = !active && visible && isHovered;
      if (bo) { onClick(mouseX, mouseY); }
      return bo;
   }

   @Override
   public void onClick(double x, double y) { listener.buttonEvent(this); }

   // New from Unofficial (BetaZavr)
   @Override
   public int getFGColor() {
      if (packedFGColor != -1) { return packedFGColor; }
      else if (isHovered) { return CustomNpcs.HoverColor.getRGB(); }
      return CustomNpcs.ButtonColor.getRGB();
   }

   @Override
   public GuiMenuTopButton setIsEnabled(boolean bo) {
      active = bo;
      return this;
   }

   @Override
   public GuiComponentType getElementType() { return GuiComponentType.TOP_BUTTON; }

}
