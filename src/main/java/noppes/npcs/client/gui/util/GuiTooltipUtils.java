package noppes.npcs.client.gui.util;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.RenderTooltipEvent.Color;
import net.minecraftforge.client.event.RenderTooltipEvent.Pre;
import noppes.npcs.mixin.client.gui.screens.inventory.tooltip.IClientTextTooltipMixin;
import org.joml.Vector2ic;

@SuppressWarnings("all")
public class GuiTooltipUtils {

   protected static ItemStack tooltipStack;

   public static void renderTooltip(GuiGraphics graphics, Font font, ItemStack stack, int mouseX, int mouseY) {
      tooltipStack = stack;
      renderTooltip(graphics, font, Screen.getTooltipFromItem(Minecraft.getInstance(), stack), stack.getTooltipImage(), mouseX, mouseY);
      tooltipStack = ItemStack.EMPTY;
   }

   public static void renderTooltip(GuiGraphics graphics, Font font, List<Component> textComponents, Optional<TooltipComponent> tooltipComponent, ItemStack stack, int mouseX, int mouseY) {
      tooltipStack = stack;
      renderTooltip(graphics, font, textComponents, tooltipComponent, mouseX, mouseY);
      tooltipStack = ItemStack.EMPTY;
   }

   public static void renderTooltip(GuiGraphics graphics, Font font, List<Component> textComponents, Optional<TooltipComponent> tooltipComponent, int mouseX, int mouseY) {
      List<ClientTooltipComponent> list = ForgeHooksClient.gatherTooltipComponents(tooltipStack, textComponents, tooltipComponent, mouseX, graphics.guiWidth(), graphics.guiHeight(), font);
      renderTooltipInternal(graphics, font, list, mouseX, mouseY, DefaultTooltipPositioner.INSTANCE);
   }

   public static void renderTooltip(GuiGraphics graphics, Font font, Component textComponent, int mouseX, int mouseY) {
      renderTooltip(graphics, font, List.of(textComponent.getVisualOrderText()), mouseX, mouseY);
   }

   public static void renderComponentTooltip(GuiGraphics graphics, Font font, List<Component> textComponents, int mouseX, int mouseY) {
      List<ClientTooltipComponent> components = ForgeHooksClient.gatherTooltipComponents(tooltipStack, textComponents, mouseX, graphics.guiWidth(), graphics.guiHeight(), font);
      renderTooltipInternal(graphics, font, components, mouseX, mouseY, DefaultTooltipPositioner.INSTANCE);
   }

   public static void renderComponentTooltip(GuiGraphics graphics, Font font, List<? extends FormattedText> tooltips, int mouseX, int mouseY, ItemStack stack) {
      tooltipStack = stack;
      List<ClientTooltipComponent> components = ForgeHooksClient.gatherTooltipComponents(stack, tooltips, mouseX, graphics.guiWidth(), graphics.guiHeight(), font);
      renderTooltipInternal(graphics, font, components, mouseX, mouseY, DefaultTooltipPositioner.INSTANCE);
      tooltipStack = ItemStack.EMPTY;
   }

   public static void renderTooltip(GuiGraphics graphics, Font font, List<? extends FormattedCharSequence> textComponents, int mouseX, int mouseY) {
      renderTooltipInternal(graphics, font, textComponents.stream().map(ClientTooltipComponent::create).collect(Collectors.toList()), mouseX, mouseY, DefaultTooltipPositioner.INSTANCE);
   }

   public static void renderTooltip(GuiGraphics graphics, Font font, List<FormattedCharSequence> components, ClientTooltipPositioner positioner, int mouseX, int mouseY) {
      renderTooltipInternal(graphics, font, components.stream().map(ClientTooltipComponent::create).collect(Collectors.toList()), mouseX, mouseY, positioner);
   }

   private static void renderTooltipInternal(GuiGraphics graphics, Font font, List<ClientTooltipComponent> components, int mouseX, int mouseY, ClientTooltipPositioner positioner) {
      if (!components.isEmpty()) {
         Pre preEvent = ForgeHooksClient.onRenderTooltipPre(tooltipStack, graphics, mouseX, mouseY, graphics.guiWidth(), graphics.guiHeight(), components, font, positioner);
         if (preEvent.isCanceled()) {
            return;
         }

         int i = 0;
         int j = components.size() == 1 ? -2 : 0;

         ClientTooltipComponent clienttooltipcomponent;
         for(Iterator<ClientTooltipComponent> var9 = components.iterator(); var9.hasNext(); j += clienttooltipcomponent.getHeight()) {
            clienttooltipcomponent = var9.next();
            int k = clienttooltipcomponent.getWidth(preEvent.getFont());
            if (k > i) {
               i = k;
            }
         }
         Vector2ic vector2ic = positioner.positionTooltip(graphics.guiWidth(), graphics.guiHeight(), preEvent.getX(), preEvent.getY(), i, j);
         int l = vector2ic.x();
         int i1 = vector2ic.y();
         graphics.pose().pushPose();
         int finalI = i;
         int finalJ = j;
         graphics.pose().translate(0.0F, 0.0F, 3600.0F);
         Runnable runa = () -> {
            Color colorEvent = ForgeHooksClient.onRenderTooltipColor(tooltipStack, graphics, l, i1, preEvent.getFont(), components);
            TooltipRenderUtil.renderTooltipBackground(graphics, l, i1, finalI, finalJ, 400, colorEvent.getBackgroundStart(), colorEvent.getBackgroundEnd(), colorEvent.getBorderStart(), colorEvent.getBorderEnd());
         };
         graphics.drawManaged(runa);
         graphics.pose().translate(0.0F, 0.0F, 400.0F);
         int k1 = i1;

         int k2;
         ClientTooltipComponent component2;
         for(k2 = 0; k2 < components.size(); ++k2) {
            component2 = components.get(k2);
            if (component2 instanceof ClientTextTooltip) {
               graphics.drawString(preEvent.getFont(), ((IClientTextTooltipMixin) component2).getText(), l, k1, 0xFFFFFF, false);
            } else {
               component2.renderText(preEvent.getFont(), l, k1, graphics.pose().last().pose(), graphics.bufferSource());
            }

            k1 += component2.getHeight() + (k2 == 0 ? 2 : 0);
         }

         k1 = i1;

         for(k2 = 0; k2 < components.size(); ++k2) {
            component2 = components.get(k2);
            component2.renderImage(preEvent.getFont(), l, k1, graphics);
            k1 += component2.getHeight() + (k2 == 0 ? 2 : 0);
         }

         graphics.pose().popPose();
      }

   }

   static {
      tooltipStack = ItemStack.EMPTY;
   }
}
