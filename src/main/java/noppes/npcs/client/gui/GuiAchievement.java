package noppes.npcs.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.awt.*;

@OnlyIn(Dist.CLIENT)
public class GuiAchievement implements Toast {

   protected final String title;
   protected final String subtitle;
   protected final int type;
   protected long firstDrawTime;
   protected boolean newDisplay;

   public GuiAchievement(Component titleIn, Component subtitleComponent, int typeIn) {
      super();
      title = titleIn.getString();
      subtitle = subtitleComponent == null ? null : subtitleComponent.getString();
      type = typeIn;
   }

   @Override
   public @Nonnull Visibility render(@Nonnull GuiGraphics graphics, @Nonnull ToastComponent toastGui, long delta) {
      if (newDisplay) {
         firstDrawTime = delta;
         newDisplay = false;
      }
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, TEXTURE);
      graphics.blit(TEXTURE, 0, 0, 0, 32 * type, 160, 32);
      int color1 = new Color(0xFFFFFF00).getRGB();
      int color2 = new Color(0xFFFFFFFF).getRGB();
      if (type == 1 || type == 3) {
         color1 = new Color(0xFF500050).getRGB();
         color2 = new Color(0xFF000000).getRGB();
      }
      graphics.drawString(toastGui.getMinecraft().font, title, 18, 7, color1);
      graphics.drawString(toastGui.getMinecraft().font, subtitle, 18, 18, color2);
      return delta - firstDrawTime < 5000L ? Visibility.SHOW : Visibility.HIDE;
   }

}
