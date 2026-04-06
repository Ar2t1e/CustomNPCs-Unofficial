package noppes.npcs.shared.client.gui.components;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FastColor;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.constants.GuiComponentType;
import noppes.npcs.client.ClientProxy;
import noppes.npcs.client.gui.util.GuiTooltipUtils;
import noppes.npcs.mixin.client.IMouseHandlerMixin;
import noppes.npcs.shared.client.gui.listeners.IComponentGui;
import noppes.npcs.shared.client.gui.listeners.IGuiInterface;
import noppes.npcs.util.ValueUtil;

import javax.annotation.Nonnull;
import java.util.*;

public class GuiButtonNop extends Button implements IComponentGui {

   protected static final OnPress clicked = (button) -> {
      GuiButtonNop b = (GuiButtonNop)button;
      b.listener.buttonEvent(b);
   };

   protected OnRender render = null;

   public final IGuiInterface listener;
   protected Component[] display;
   protected int displayValue = 0;
   public int id;

   // Normal
   protected Component message;
   protected int textColor;
   public boolean showShadow = true;

   // New from Unofficial (BetaZavr)
   protected List<Component> hoverText = new ArrayList<>();
   protected ClientProxy.FontContainer customFont = null;
   protected static final double step = 60;

   protected ItemStack[] renderStacks = null;
   protected int ticks = 0;
   protected int wait = 0;

   public ResourceLocation texture = null;
   public ItemStack renderStack = ItemStack.EMPTY;
   public int renderStackID = -1;
   public int txrX = 0;
   public int txrY = 0;
   public int txrW = 200;
   public int txrH = 20;
   public int offsetHoverX = 0;
   public int offsetHoverY = 0;

   public int layerColor;
   public boolean dropShadow = true;
   public boolean hasDefBack;
   public boolean isSimple = false;
   public boolean isAnim = false;
   public boolean isScissor = true;

   public boolean hasSound = true;
   private ItemStack[] itemStacks = null;
   public ItemStack currentStack = ItemStack.EMPTY;
   public int currentStackID = -1;

   public static void renderString(@Nonnull GuiGraphics graphics, @Nonnull Component message,
                                   int left, int top, int right, int bottom, int color, boolean showShadow,
                                   boolean centered, ClientProxy.FontContainer customFont) {
      Font font = Minecraft.getInstance().font;
      int textWidth = customFont != null ? customFont.width(message) : font.width(message);
      int height = (top + bottom - 9) / 2 + 1;
      if (customFont != null) {
         textWidth++;
         height--;
      }
      int width = right - left;
      FormattedCharSequence format = message.getVisualOrderText();
      graphics.enableScissor(left, top, right, bottom);
      if (textWidth > width) { // moved
         int centerX = textWidth - width;
         double d0 = (double) Util.getMillis() / 1000.0;
         double d1 = Math.max((double) centerX * 0.5, 3.0);
         double d2 = Math.sin(Math.PI / 2.0d * Math.cos(Math.PI * 2.0d * d0 / d1)) / 2.0 + 0.5;
         double d3 = Mth.lerp(d2, 0.0, centerX);
         if (customFont != null) { customFont.draw(graphics, message, left - (int) d3, height, color); }
         else { graphics.drawString(font, format, left - (int) d3, height, color, showShadow); }
      }
      else {
         if (centered) {
            width = (left + right) / 2;
            if (customFont != null) { customFont.draw(graphics, message, width - textWidth / 2.0f, height, color); }
            else { graphics.drawString(font, format, width - textWidth / 2, height, color, showShadow); }

         }
         else {
            if (customFont != null) { customFont.draw(graphics, message, left, height, color); }
            else { graphics.drawString(font, format, left, height, color, showShadow); }
         }
      }
      graphics.disableScissor();
   }

   public GuiButtonNop(IGuiInterface gui, int buttonId, Object label, int x, int y, OnPress onPress) {
      super(x, y, 200, 20, label == null ? Component.empty() :
              label instanceof Component component ? component :
                      Component.translatable(label.toString()), onPress != null ? onPress : clicked, Button.DEFAULT_NARRATION);
      id = buttonId;
      listener = gui;
      setDisplayText(label);
   }

   public GuiButtonNop(IGuiInterface gui, int buttonId, int x, int y, OnPress onPress, int variant, Object ... variants) {
      super(x, y, 200, 20, Component.empty(), onPress, Button.DEFAULT_NARRATION);
      packedFGColor = CustomNpcs.MainColor.getRGB();
      id = buttonId;
      listener = gui;
      displayValue = variant;
      setVariants(variants);
   }

   public GuiButtonNop(IGuiInterface gui, int buttonId, Object label, int x, int y, OnPress onPress, OnRender onRender) {
      this(gui, buttonId, label, x, y, onPress);
      render = onRender;
   }

   public GuiButtonNop(IGuiInterface gui, int buttonId, int x, int y, int variant, Object ... variants) {
      this(gui, buttonId, x, y, clicked, variant, variants);
   }

   @Override
   protected boolean clicked(double mouseX, double mouseY) {
      return visible && active && mouseX >= getX() && mouseY >= getY() && mouseX < getX() + width && mouseY < getY() + height;
   }

   @Override
   public GuiButtonNop setSize(int widthIn, int heightIn) {
      width = widthIn;
      height = heightIn;
      return this;
   }

   @Override
   public @Nonnull Component getMessage() {
      if (message == null) { return super.getMessage(); }
      return message;
   }

   @Override
   public void render(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      isHovered = false;
      if (visible) {
         super.render(graphics, mouseX, mouseY, partialTicks);
         if (isHovered && !hoverText.isEmpty()) {
            if (listener != null) { listener.setHoverText(hoverText); }
            else { GuiTooltipUtils.renderTooltip(graphics, Minecraft.getInstance().font, hoverText, Optional.empty(), mouseX, mouseY); }
         }
      }
   }

   @Override
   public void onClick(double mouseX, double mouseY) {
      if (active && (listener == null || !listener.hasSubGui())) {
         if (display != null) { setDisplay((displayValue + 1) % display.length); }
         super.onPress();
      }
   }

   @Override
   public void renderWidget(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      if (offsetHoverX != 0 || offsetHoverY != 0) {
         mouseX -= offsetHoverX;
         mouseY -= offsetHoverY;
      }
      isHovered = visible && mouseX >= getX() && mouseY >= getY() && mouseX < getX() + width && mouseY < getY() + height;
      if (!visible) { return; }
      Minecraft mc = Minecraft.getInstance();
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
      RenderSystem.enableBlend();

      PoseStack matrixStack = graphics.pose();
      if (render != null) { render.onRender(this, graphics, mouseX, mouseY, partialTicks); }
      else {
         int x = getX();
         int y = getY();
         int state = getState();
         if (hasDefBack) {
            graphics.fill(x - 1, y - 1, x + width + 1, y + height + 1, 0xFF202020);
            graphics.fill(x, y, x + width, y + height, 0xFFA0A0A0);
         }
         if (layerColor != 0) {
            RenderSystem.setShaderColor((float) FastColor.ARGB32.red(layerColor) / 255.0f,
                    (float) FastColor.ARGB32.green(layerColor) / 255.0f,
                    (float) FastColor.ARGB32.blue(layerColor) / 255.0f,
                    (float) FastColor.ARGB32.alpha(layerColor) / 255.0f);
         }
         if (texture == null) {
            try {
               graphics.blitNineSliced(WIDGETS_LOCATION, x, y, width, height, 20, 4, 200, 20, 0, state);
            }
            catch (Exception ignored) { }
         }
         else {
            matrixStack.pushPose();
            if (isSimple) {
               int w0 = width / 2;
               int w1 = width - w0;
               graphics.blit(texture, x, y, txrX, txrY + state * height, w0, height);
               graphics.blit(texture, x + w0, y, txrX + txrW - w0, txrY + state * height, w1, height);
            }
            else {
               boolean isPrefabricated = txrW == 0;
               int tw = isPrefabricated ? 200 : txrW;
               int th = txrH == 0 ? 20 : txrH;
               float scaleH = height / (float) th;
               float scaleW = isPrefabricated ? scaleH : width / (float) tw;
               matrixStack.translate(x, y, 0.0f);
               matrixStack.scale(scaleW, scaleH, 1.0f);
               if (isPrefabricated) {
                  tw = (int) (((float) width / 2.0f) / scaleH);
                  graphics.blit(texture, 0, 0, txrX, txrY + state * th, tw, th);
                  graphics.blit(texture, tw, 0, txrX + 200 - tw, txrY + state * th, tw, th);
               } else {
                  graphics.blit(texture, 0, 0, txrX, txrY + state * th, tw, th);
               }
            }
            matrixStack.popPose();
         }
      }
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

      matrixStack.pushPose();
      @Nonnull Component label = getMessage();
      if (active && (label.getString().equals("<") || label.getString().equals(">") || label.getString().equals("<<") || label.getString().equals("<<<") || label.getString().equals(">>") || label.getString().equals(">>>"))) {
         int w = mc.font.width(label);
         float wm = width - 4 + 2 * w;
         float ox = (float) (System.currentTimeMillis() % 2000L) / 2000.0f * wm;
         if (label.getString().equals("<") || label.getString().equals("<<") || label.getString().equals("<<") || label.getString().equals("<<<")) { matrixStack.translate((width + w) / 2.0f - ox, 0.0f, 0.0f); }
         else { matrixStack.translate((width + w) / -2.0f + ox, 0.0f, 0.0f); }
      }
      if (!active) { RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.5F); }
      int color = getFGColor() | Mth.ceil(alpha * 255.0F) << 24;
      if (isScissor) {
         renderString(graphics, label, getX() + 2, getY(), getX() + getWidth() - 2, getY() + getHeight(),
                 color, showShadow, true, customFont);
      } else {
         if (customFont != null) { customFont.draw(graphics, label, getX() + 2, getY(), color); }
         else { graphics.drawString(mc.font, label, getX() + 2 - mc.font.width(label) / 2, getY(), color, showShadow); }
      }
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      matrixStack.popPose();

      if (renderStacks != null && renderStacks.length != 0) {
         renderStack = renderStacks[0];
         renderStackID = 0;
         if (renderStacks.length > 1) {
            if (wait > 0) { wait --; }
            else {
               renderStackID = (int) Math.floor(((double) ticks % (step * (double) renderStacks.length - 1.0d)) / step);
               renderStack = renderStacks[renderStackID];
            }
         }
         if (renderStack != null && !renderStack.isEmpty()) {
            matrixStack.pushPose();
            matrixStack.translate((float) getX() + (float) getWidth() / 2.0f - 8.0f, (float) getY() + (float) getHeight() / 2.0f - 8.0f, 30.0F);
            graphics.renderItem(renderStack, 0, 0);
            graphics.renderItemDecorations(mc.font, renderStack, 0, 0, null);
            matrixStack.popPose();
         }
         if (wait == 0) {
            ticks++;
            if (ticks > step * renderStacks.length) { ticks = 0; }
         }
      }
   }

   @Override
   public boolean mouseScrolled(double mouseX, double mouseY, double mouseScrolled) {
      if (display != null && display.length != 0 && isHovered) {
         setDisplay((displayValue + (mouseScrolled > 0 ? -1 : 1)) % display.length);
         return true;
      }
      return false;
   }

   public int getValue() { return displayValue; }

   public void setDisplayText(Object label) {
      message = label == null ? Component.empty() :
              label instanceof Component component ? component :
                      Component.translatable("" + label);
      setMessage(message);
   }

   public GuiButtonNop setDisplay(int value) {
      displayValue = ValueUtil.onlyPositiveInt(value, display.length);
      setDisplayText(display[displayValue]);
      return this;
   }

   public GuiButtonNop setDefBack(boolean bo) {
      hasDefBack = bo;
      return this;
   }

   public GuiButtonNop setVariants(Object ... variants) {
      Set<Component> lines = new LinkedHashSet<>();
      for (Object o : variants) {
         if (o == null) { lines.add(Component.empty()); }
         else if (o instanceof Component component) { lines.add(component); }
         else if (o instanceof List<?> list) {
            for (Object line : list) {
               if (line == null) { lines.add(Component.empty()); }
               else if (line instanceof Component component) { lines.add(component); }
               else { lines.add(Component.translatable("" + line)); }
            }
         }
         else { lines.add(Component.translatable("" + o)); }
      }
      display = lines.toArray(new Component[0]);
      displayValue = variants.length == 0 ? 0 : displayValue % variants.length;
      if (displayValue >= 0 && displayValue < display.length) {
         setDisplayText(display[displayValue]);
      }
      return this;
   }

   // New from Unofficial (BetaZavr)
   @OnlyIn(Dist.CLIENT)
   public interface OnRender {
      void onRender(GuiButtonNop button, GuiGraphics graphics, int mouseX, int mouseY, float partialTicks);
   }

   @Override
   public int getFGColor() {
      if (packedFGColor != -1) { return packedFGColor; }
      else if (!active) { return CustomNpcs.NotEnableColor.getRGB(); }
      else if (isHovered) { return CustomNpcs.HoverColor.getRGB(); }
      return CustomNpcs.ButtonColor.getRGB();
   }

   @Override
   public int[] getCenter() { return new int[] { getX() + width / 2, getY() + height / 2}; }

   @Override
   public List<Component> getHoversText() { return hoverText; }

   @Override
   public int getId() { return id; }

   @Override
   public boolean isEnabled() { return active; }

   @Override
   public boolean isVisible() { return visible; }

   @Override
   public void moveTo(int addX, int addY) {
      setX(getX() + addX);
      setY(getY() + addY);
   }

   public Component[] getVariants() { return display; }

   @Override
   public GuiButtonNop setHoverTexts(Object... components) {
      hoverText.clear();
      if (components == null) { return this; }
      noppes.npcs.util.Util.instance.putHovers(hoverText, components);
      return this;
   }

   @Override
   public GuiButtonNop setIsEnabled(boolean isEnabled) {
      active = isEnabled;
      return this;
   }

   @Override
   public GuiButtonNop setIsVisible(boolean isVisible) {
      visible = isVisible;
      return this;
   }

   @Override
   public GuiButtonNop setIsFocused(boolean isFocused) {
      setFocused(isFocused);
      return this;
   }

   @Override
   public GuiButtonNop setCustomFont(ClientProxy.FontContainer font) {
      customFont = font;
      return this;
   }

   @Override
   public GuiComponentType getElementType() { return GuiComponentType.BUTTON; }

   @Override
   public void playDownSound(@Nonnull SoundManager soundManager) {
      if (hasSound) {
         soundManager.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
      }
   }

   @Override
   public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
      if (active && visible) {
         if (isValidClickButton(mouseButton)) {
            boolean flag = clicked(mouseX, mouseY);
            if (flag) {
               playDownSound(Minecraft.getInstance().getSoundManager());
               onClick(mouseX, mouseY);
               if (listener != null) { listener.mouseButtonEvent(this, mouseButton); }
               return true;
            }
         }
      }
      return false;
   }

   @Override
   protected boolean isValidClickButton(int mouseButton) { return true; }

   @Override
   public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dx, double dy) {
      super.mouseDragged(mouseX, mouseY, mouseButton, dx, dy);
      return false;
   }

   @Override
   public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
      super.mouseReleased(mouseX, mouseY, mouseButton);
      return false;
   }

   @Override
   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (active && visible) {
         if (keyCode == InputConstants.KEY_RETURN || keyCode == InputConstants.KEY_NUMPADENTER) {
            playDownSound(Minecraft.getInstance().getSoundManager());
            onPress();
            return true;
         }
      }
      return false;
   }

   public int getState() {
      boolean lbm = ((IMouseHandlerMixin) Minecraft.getInstance().mouseHandler).getActiveButton() == 0;
      if (texture == null) {
         int i = 1;
         if (!active) { i = 0; }
         else if (isHoveredOrFocused() && (listener == null || !listener.hasSubGui())) { i = lbm ? 0 : 2; }
         return 46 + i * 20;
      }
      if (isAnim) {
         if (!active) { return 1; }
         return isHoveredOrFocused() && (listener == null || !listener.hasSubGui()) ? lbm ? 3 : 2 : 0;
      }
      if (isSimple) {
         int i = 0;
         if (!active) { i = 2; }
         else if (isHoveredOrFocused() && (listener == null || !listener.hasSubGui())) { i = lbm ? 2 : 1; }
         return i;
      }
      if (isHoveredOrFocused() && (listener == null || !listener.hasSubGui())) {
         return (active ? 1 : 4) + (lbm ? 1 : 0);
      }
      return active ? 0 : 3;
   }

   public GuiButtonNop setColor(int color) {
      setFGColor(color);
      return this;
   }

   public GuiButtonNop setStacks(ItemStack... stacks) {
      if (renderStacks != null && stacks != null) { wait = 160; }
      renderStacks = stacks;
      renderStackID = renderStacks != null ? 0 : -1;
      ticks = 0;
      return this;
   }

   public ItemStack[] getStacks() { return renderStacks; }

   public GuiButtonNop setCurrentStackPos(int pos) {
      if (renderStacks != null && pos >= 0 && pos < renderStacks.length) {
         renderStackID = pos;
         wait = 160;
         ticks = 0;
      }
      return this;
   }

   public GuiButtonNop setShowShadow(boolean shadow) {
      showShadow = shadow;
      return this;
   }

   public GuiButtonNop setTexture(ResourceLocation resource) {
      texture = resource;
      return this;
   }

   public GuiButtonNop setUV(int u, int v, int width, int height) {
      txrX = u;
      txrY = v;
      txrW = width;
      txrH = height;
      return this;
   }

   public GuiButtonNop setIsAnim(boolean bo) {
      isAnim = bo;
      isSimple = !bo;
      return this;
   }

   public void offsetHover(int x, int y) {
      offsetHoverX = x;
      offsetHoverY = y;
   }

}
