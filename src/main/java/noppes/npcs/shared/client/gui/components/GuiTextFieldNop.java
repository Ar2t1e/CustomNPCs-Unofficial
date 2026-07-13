package noppes.npcs.shared.client.gui.components;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.constants.GuiComponentType;
import noppes.npcs.client.ClientProxy;
import noppes.npcs.mixin.client.gui.components.IEditBoxMixin;
import noppes.npcs.shared.client.gui.GuiBasic;
import noppes.npcs.shared.client.gui.listeners.IComponentGui;
import noppes.npcs.shared.client.gui.listeners.IGuiInterface;
import noppes.npcs.shared.client.gui.listeners.ITextChangeListener;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;
import noppes.npcs.util.Util;
import noppes.npcs.util.ValueUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

public class GuiTextFieldNop extends EditBox implements IComponentGui {

   public static char[] filePath = new char[] { ':', '*', '?', '"', '<', '>', '&', '|' };

   private static GuiTextFieldNop activeTextfield = null;
   public static void unfocus() {
      GuiTextFieldNop prev = activeTextfield;
      activeTextfield = null;
      if (prev != null) { prev.unFocused(); }
   }

   private final int[] allowedSpecialChars = new int[] {14, 211, 203, 205};
   public boolean enabled = true;
   protected boolean numbersOnly = false;
   protected boolean doublesOnly = false;
   protected int enabledColor = CustomNpcs.MainColor.getRGB();
   public IGuiInterface listener;
   public int id;
   public long min = 0;
   public long max = Long.MAX_VALUE;
   public long def = 0;
   public double minD = 0.0;
   public double maxD = Double.MAX_VALUE;
   public double defD = 0.0;

   // New from Unofficial (BetaZavr)
   protected final List<Component> hoverText = new ArrayList<>();
   protected ClientProxy.FontContainer customFont = null;
   protected final String defaultValue;
   protected boolean latinAlphabetOnly = false;
   protected boolean allowUppercase = true;
   protected boolean isFileName = false;
   /**
    * 0: none; 1: full resource; 2: resource path; 3: resource domain
    */
   protected int resourceLocationType = 0;
   public char[] prohibitedSpecialChars = new char[] {};

   public GuiTextFieldNop(@Nullable IGuiInterface parent, int idIn, int x, int y, int width, int height, Object value) {
      super(Minecraft.getInstance().font, x, y, width, height, value == null ? Component.empty() :
              value instanceof Component component ? component :
                      value instanceof ResourceKey<?> resKey ? Component.literal(resKey.location().toString()) :
                      Component.literal(value.toString()));
      setMaxLength(500);
      Component mes = getMessage();
      if (!mes.getString().isEmpty()) { setValue(mes.getString()); }
      id = idIn;
      listener = parent;
      defaultValue = String.copyValueOf(getValue().toCharArray());
   }

   public static GuiTextFieldNop getActive() {
      return activeTextfield;
   }

   private boolean charAllowed(char typedChar, int keyCode) {
      for (char g : prohibitedSpecialChars) {
         if (g == typedChar) { return false; }
      }
      for (int j : allowedSpecialChars) {
         if (j == keyCode) { return true; }
      }
      String text = getValue();
      boolean selectAll = getHighlighted().equals(text);
      if (isFileName) {
         for (char g : SharedConstants.ILLEGAL_FILE_CHARACTERS) {
            if (g == typedChar) { return false; }
         }
      }
      if (numbersOnly) {
         return Character.isDigit(typedChar) || (typedChar == '-' && selectAll || getCursorPosition() == 0 && !text.contains("" + typedChar));
      }
      if (doublesOnly) {
         boolean hasDot = text.contains(".") || text.contains(",");
         return Character.isDigit(typedChar) || (typedChar == '-' && selectAll || getCursorPosition() == 0 && !text.contains("" + typedChar))
                 || (!hasDot || selectAll && (typedChar == '.' || typedChar == ','));
      }
      if (resourceLocationType != 0) {
         if (typedChar == ':' && (resourceLocationType != 1 || text.contains(":"))) { return false; }
         if (typedChar == '_' ||
                 typedChar == '-' ||
                 typedChar >= 'a' && typedChar <= 'z' ||
                 (!text.isEmpty() && typedChar >= '0' && typedChar <= '9') ||
                 typedChar == '.') {
            return true;
         }
         return (resourceLocationType == 1 || resourceLocationType == 2) && typedChar == '/';
      }
      if (!latinAlphabetOnly || Character.isLetterOrDigit(typedChar) || typedChar == '_') { return true; }
      return allowUppercase || Character.isLowerCase(typedChar);
   }

   @Override
   public boolean charTyped(char c, int i) { return charAllowed(c, i) && super.charTyped(c, i); }

   public boolean isEmpty() { return getValue().trim().isEmpty(); }

   public int getInteger() { return isEmpty() ? 0 : Integer.parseInt(getValue()); }

   public long getLong() { return isEmpty() ? 0L :Long.parseLong(getValue()); }

   public float getFloat() { return isEmpty() ? 0.0f :Float.parseFloat(getValue()); }

   public double getDouble() { return isEmpty() ? 0.0d :Double.parseDouble(getValue()); }

   public boolean isInteger() {
      try {
         Integer.parseInt(getValue());
         return true;
      } catch (NumberFormatException var2) {
         return false;
      }
   }

   public boolean isLong() {
      try {
         Long.parseLong(getValue());
         return true;
      } catch (NumberFormatException var2) {
         return false;
      }
   }

   public boolean isFloat() {
      try {
         Float.parseFloat(getValue());
         return true;
      }
      catch (NumberFormatException var2) { return false; }
   }

   public boolean isDouble() {
      try {
         Double.parseDouble(getValue());
         return true;
      }
      catch (NumberFormatException var2) { return false; }
   }

   @Override
   public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
      if (!enabled || !visible) { return false; }
      boolean wasFocused = isFocused();
      setFocused(isHovered);
      boolean clicked = super.mouseClicked(mouseX, mouseY, mouseButton);
      if (!wasFocused && isFocused()) {
         unfocus();
         activeTextfield = this;
      }
      if (wasFocused && !isFocused()) { unFocused(); }
      return clicked;
   }

   public void unFocused() {
      if (numbersOnly) {
         if (isEmpty() || !isInteger()) { setValue(def + ""); }
         else if (getInteger() < min) { setValue(min + ""); }
         else if (getInteger() > max) { setValue(max + ""); }
      }
      else if (doublesOnly) {
         if (isEmpty() || !isDouble()) { setValue(defD + ""); }
         else if (getDouble() < minD) { setValue(minD + ""); }
         else if (getDouble() > maxD) { setValue(maxD + ""); }
      }
      else if (resourceLocationType != 0) {
         if (isEmpty()) { setValue(defaultValue); }
      }
      if (listener instanceof ITextfieldListener parent) { parent.unFocused(this); }
      if (this == activeTextfield) { activeTextfield = null; }
   }

   public int getTextColor() {
      if (numbersOnly || doublesOnly) {
         if (numbersOnly && (!isLong() || getLong() < min || getLong() > max)) { return new Color(0xFC0345).getRGB(); }
         if (doublesOnly && (!isDouble() || getDouble() < minD || getDouble() > maxD)) { return new Color(0xFC0345).getRGB(); }
      }
      return enabled ? enabledColor : CustomNpcs.NotEnableColor.getRGB();
   }

   @Override
   public void render(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      isHovered = false;
      super.render(graphics, mouseX, mouseY, partialTicks);
      if (visible && isHovered && listener != null) {
         if (resourceLocationType != 0) {
            List<Component> hovers = new ArrayList<>(hoverText);
            hovers.add(Component.translatable("text.field.is.resource.location"));
            listener.setHoverText(hovers);
         }
         else if (Arrays.equals(prohibitedSpecialChars, filePath)) {
            List<Component> hovers = new ArrayList<>(hoverText);
            hovers.add(Component.translatable("text.field.is.fine.name"));
            listener.setHoverText(hovers);
         }
         else if (!hoverText.isEmpty()) { listener.setHoverText(hoverText); }
      }
   }

   @Override
   public void renderWidget(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      if (!visible) { return; }
      if (isFocused() && activeTextfield != this) { setFocused(false); }
      setTextColor(getTextColor());

      boolean bordered = ((IEditBoxMixin) this).getBordered();
      boolean isEditable = ((IEditBoxMixin) this).getIsEditable();
      int cursorPos = ((IEditBoxMixin) this).getCursorPos();
      int textColor = ((IEditBoxMixin) this).getTextColor();
      int textColorUneditable = ((IEditBoxMixin) this).getTextColorUneditable();
      int displayPos = ((IEditBoxMixin) this).getDisplayPos();
      int highlightPos = ((IEditBoxMixin) this).getHighlightPos();
      int frame = ((IEditBoxMixin) this).getFrame();
      int alpha = 0xFF000000;
      Font font = ((IEditBoxMixin) this).getFont();
      BiFunction<String, Integer, FormattedCharSequence> formatter = ((IEditBoxMixin) this).getFormatter();
      Component hint = ((IEditBoxMixin) this).getHint();
      String suggestion = ((IEditBoxMixin) this).getSuggestion();
      String value = getValue();
      int color;
      PoseStack matrixStack = graphics.pose();
      if (customFont != null) {
         matrixStack.pushPose();
         matrixStack.translate(getX(), getY(), 0.0f);
         matrixStack.scale(0.5f, 0.5f, 0.5f);
         if (bordered) {
            color = (isFocused() ? (new Color(0xFFFFFF).getRGB() & 0xFFFFFF) :
                    (new Color(0xA0A0A0).getRGB() & 0xFFFFFF))  | alpha;
            graphics.fill(0, 0, width * 2, height * 2, color);
            graphics.fill(1, 1, width * 2 - 1, height * 2 - 1,
                    (new Color(0x000000).getRGB() & 0xFFFFFF)  | alpha);
         }
         matrixStack.popPose();
      }
      else if (bordered) {
         color = (isFocused() ? (new Color(0xFFFFFF).getRGB() & 0xFFFFFF) :
                 (new Color(0xA0A0A0).getRGB() & 0xFFFFFF))  | alpha;
         graphics.fill(getX() - 1, getY() - 1, getX() + width + 1, getY() + height + 1, color);
         graphics.fill(getX(), getY(), getX() + width, getY() + height,
                 (new Color(0x000000).getRGB() & 0xFFFFFF)  | alpha);
      }
      color = isEditable ? textColor : textColorUneditable;
      int j = cursorPos - displayPos;
      String subStrByWidth = customFont != null ? customFont.getFont().plainSubstrByWidth(value.substring(displayPos), getInnerWidth()) :
              font.plainSubstrByWidth(value.substring(displayPos), getInnerWidth());
      boolean flag = j >= 0 && j <= subStrByWidth.length();
      boolean showLine = isFocused() && frame / 8 % 2 == 0 && flag;
      int l = getX() + (customFont != null ? 2 : 4);
      int i1 = bordered ? getY() + (height - 8) / 2 : getY();
      int j1 = l;
      int k = ValueUtil.correctInt(highlightPos - displayPos, 0, subStrByWidth.length());
      if (!subStrByWidth.isEmpty()) {
         String s1 = flag ? subStrByWidth.substring(0, j) : subStrByWidth;
         if (customFont != null) { j1 = customFont.draw(graphics, s1, l, i1, color); }
         else { j1 = graphics.drawString(font, formatter.apply(s1, displayPos), l, i1, color); }
      }
      boolean isMaxEndPos = cursorPos < getValue().length() || value.length() >= ((IEditBoxMixin) this).invokeGetMaxLength();
      int k1 = j1;
      if (!flag) { k1 = j > 0 ? l + width : l; }
      else if (isMaxEndPos) {
         k1 = j1 - 1;
         --j1;
      }
      if (!subStrByWidth.isEmpty() && flag && j < subStrByWidth.length()) {
         if (customFont != null) { customFont.draw(graphics, subStrByWidth.substring(j), j1, i1, color); }
         else { graphics.drawString(font, formatter.apply(subStrByWidth.substring(j), cursorPos), j1, i1, color); }
      }
      if (hint != null && subStrByWidth.isEmpty() && !isFocused()) {
         if (customFont != null) { customFont.draw(graphics, hint, j1, i1, color); }
         else { graphics.drawString(font, hint, j1, i1, color); }
      }
      if (!isMaxEndPos && suggestion != null) {
         int c = (new Color(0x808080).getRGB() & 0xFFFFFF)  | alpha;
         if (customFont != null) { customFont.draw(graphics, suggestion, k1 - 1, i1, c); }
         else { graphics.drawString(font, suggestion, k1 - 1, i1, c); }
      }
      if (showLine) {
         if (isMaxEndPos) {
            graphics.fill(RenderType.guiOverlay(), k1, i1 - 1, k1 + 1, i1 + 1 + 9,
                    (new Color(0xD0D0D0).getRGB() & 0xFFFFFF)  | alpha);
         } else {
            if (customFont != null) { customFont.draw(graphics, "_", k1, i1, color); }
            else { graphics.drawString(font, "_", k1, i1, color); }
         }
      }
      if (k != j) {
         String str = subStrByWidth.substring(0, k);
         int l1;
         if (customFont != null) { l1 = l + customFont.width(str); } else { l1 = l + font.width(str); }
         renderHighlight(graphics, k1, i1 - 1, l1 - 1, i1 + 1 + 9);
      }
   }

   protected void renderHighlight(GuiGraphics graphics, int left, int top, int right, int bottom) {
      if (left < right) {
         int i = left;
         left = right;
         right = i;
      }
      if (top < bottom) {
         int j = top;
         top = bottom;
         bottom = j;
      }
      if (right > getX() + width) { right = getX() + width; }
      if (left > getX() + width) { left = getX() + width; }
      graphics.fill(RenderType.guiTextHighlight(), left, top, right, bottom,
              (new Color(0x0000FF).getRGB() & 0xFFFFFF) | 0xFF000000);
   }

   @Override
   public void setHighlightPos(int pos) {
      String value = getValue();
      int size = value.length();
      int highlightPos = Mth.clamp(pos, 0, size);
      int displayPos = ((IEditBoxMixin) this).getDisplayPos();
      Font font = ((IEditBoxMixin) this).getFont();
      if (customFont != null || font != null) {
         if (displayPos > size) { displayPos = size; }
         int j = getInnerWidth();
         String s = customFont != null ? customFont.getFont().plainSubstrByWidth(value.substring(displayPos), j) :
                 font.plainSubstrByWidth(value.substring(displayPos), j);
         int k = s.length() + displayPos;
         if (highlightPos == displayPos) {
            displayPos -= customFont != null ? customFont.getFont().plainSubstrByWidth(value, j, true).length() :
                    font.plainSubstrByWidth(value, j, true).length();
         }
         if (highlightPos > k) { displayPos += highlightPos - k; }
         else if (highlightPos <= displayPos) { displayPos -= displayPos - highlightPos; }
         ((IEditBoxMixin) this).setDisplayPos(Mth.clamp(displayPos, 0, size));
      }
      ((IEditBoxMixin) this).setHighLPos(highlightPos);
   }

   @Override
   public void onClick(double mouseX, double mouseY) {
      int i = Mth.floor(mouseX) - getX();
      if (((IEditBoxMixin) this).getBordered()) { i -= (customFont != null ? 2 : 4); }
      Font font = ((IEditBoxMixin) this).getFont();
      int displayPos = ((IEditBoxMixin) this).getDisplayPos();
      String value = getValue();
      String s = customFont != null ? customFont.getFont().plainSubstrByWidth(value.substring(displayPos), getInnerWidth()) :
              font.plainSubstrByWidth(value.substring(displayPos), getInnerWidth());

      moveCursorTo((customFont != null ? customFont.getFont().plainSubstrByWidth(s, i) :
              font.plainSubstrByWidth(s, i)).length() + displayPos);
   }

   @Override
   public int getScreenX(int pos) {
      String value = getValue();
      return pos > value.length() ? getX() : getX() + (customFont != null ? customFont.width(value.substring(0, pos)) :
              ((IEditBoxMixin) this).getFont().width(value.substring(0, pos)));
   }

   public GuiTextFieldNop setMinMaxDefault(double minValue, double maxValue, double defaultValue) {
      numbersOnly = false;
      doublesOnly = true;
      if (minValue > maxValue) {
         double i = minValue;
         minValue = maxValue;
         maxValue = i;
      }
      minD = minValue;
      maxD = maxValue;
      defD = defaultValue;
      return this;
   }

   public GuiTextFieldNop setMinMaxDefault(long minValue, long maxValue, long defaultValue) {
      numbersOnly = true;
      doublesOnly = false;
      if (minValue > maxValue) {
         long i = minValue;
         minValue = maxValue;
         maxValue = i;
      }
      min = minValue;
      max = maxValue;
      def = defaultValue;
      return this;
   }

   @SuppressWarnings("UnusedReturnValue")
   public GuiTextFieldNop setNumbersOnly() {
      numbersOnly = true;
      doublesOnly = false;
      return this;
   }

   @SuppressWarnings("unused")
   public GuiTextFieldNop setDoublesOnly() {
      numbersOnly = false;
      doublesOnly = true;
      return this;
   }

   // New from Unofficial (BetaZavr)
   /**
    * @param type - 0: none; 1: full resource; 2: resource path; 3: resource domain
    */
   public GuiTextFieldNop setResourceLocationType(int type) {
      resourceLocationType = ValueUtil.correctInt(type, 0, 3);
      return this;
   }

   public GuiTextFieldNop setEditableIn(boolean editable) {
      setEditable(editable);
      return this;
   }

   @SuppressWarnings("unused")
   public boolean isLatinAlphabetOnly() { return latinAlphabetOnly; }

   public GuiTextFieldNop setLatinAlphabetOnly(boolean isLatinAlphabetOnly) {
      latinAlphabetOnly = isLatinAlphabetOnly;
      return this;
   }

   @SuppressWarnings("unused")
   public boolean isAllowUppercase() { return allowUppercase; }

   public GuiTextFieldNop setAllowUppercase(boolean isAllowUppercase) {
      allowUppercase = isAllowUppercase;
      return this;
   }

   public GuiTextFieldNop setMaxStringLength(int length) {
      if (length < 0) { length *= -1; }
      setMaxLength(length);
      return this;
   }

   public GuiTextFieldNop setColor(int color) {
      setTextColor(color);
      return this;
   }

   @Override
   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (canConsumeInput() && isFocused() && GuiBasic.isEnterKey(keyCode)) { // Enter
         unFocused();
         return true;
      }
      boolean bo = super.keyPressed(keyCode, scanCode, modifiers);
      return bo || activeTextfield == this;
   }

   @Override
   public int[] getCenter() { return new int[] { getX() + width / 2, getY() + height / 2}; }

   @Override
   public List<Component> getHoversText() { return hoverText; }

   @Override
   public int getId() { return id; }

   @Override
   public boolean isEnabled() { return enabled; }

   @Override
   public void moveTo(int addX, int addY) {
      setX(getX() + addX);
      setY(getY() + addY);
   }

   @Override
   public void insertText(@Nonnull String text) {
      String oldText = getValue();
      super.insertText(text);
      if (listener instanceof ITextChangeListener gui && !oldText.equals(getValue())) { gui.textUpdate(this, getValue()); }
   }

   @Override
   public void deleteChars(int pos) {
      String oldText = getValue();
      super.deleteChars(pos);
      if (listener instanceof ITextChangeListener gui && !oldText.equals(getValue())) { gui.textUpdate(this, getValue()); }
   }

   public void setValue(@Nonnull Object object) {
      setValue(object instanceof Component component ? component.getString() :
              object instanceof ResourceKey<?> resKey ? resKey.location().toString() :
              object.toString());
   }

   @Override
   public void setValue(@Nonnull String text) {
      String oldText = getValue();
      super.setValue(text);
      if (listener instanceof ITextChangeListener gui && !oldText.equals(getValue())) { gui.textUpdate(this, getValue()); }
   }

   @Override
   public GuiTextFieldNop setHoverTexts(Object... components) {
      hoverText.clear();
      if (components == null) { return this; }
      Util.instance.putHovers(hoverText, components);
      return this;
   }

   @Override
   public GuiTextFieldNop setIsEnabled(boolean isEnabled) {
      enabled = isEnabled;
      return this;
   }

   @Override
   public GuiTextFieldNop setIsVisible(boolean bo) {
      visible = bo;
      return this;
   }

   @Override
   public GuiTextFieldNop setIsFocused(boolean isFocused) {
      setFocused(isFocused);
      return this;
   }

   @Override
   public GuiTextFieldNop setSize(int widthIn, int heightIn) {
      width = widthIn;
      height = heightIn;
      return this;
   }

   @Override
   public GuiTextFieldNop setCustomFont(ClientProxy.FontContainer font) {
      customFont = font;
      return this;
   }

   @Override
   public GuiComponentType getElementType() { return GuiComponentType.TEXT_FIELD; }

   public void setIsFile(boolean bo) { isFileName = bo; }


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
   public boolean mouseScrolled(double mouseX, double mouseY, double mouseScrolled) {
      if (isHovered && (doublesOnly || numbersOnly) && mouseScrolled != 0) {
         if (doublesOnly) {
            double d = getDouble();
            double v = maxD - minD;
            double f = (mouseScrolled < 0 ? -v : v) / (double) width;
            double t = d + f;
            if (t < minD) { t = t - minD + maxD; }
            else if (t > maxD) { t = t - maxD + minD; }
            setValue("" + ValueUtil.correctDouble(Math.round(t * 1000.0d) / 1000.0d, minD, maxD));
         }
         else {
            int i = getInteger();
            int v = (int) (max - min);
            int f = (mouseScrolled < 0 ? -v : v) / width;
            int t = i + f;
            if (t < min) { t = t - (int) (min + max); }
            else if (t > max) { t = t - (int) (max + min); }
            setValue("" + ValueUtil.correctInt((int) (Math.round((double) t * 1000.0d) / 1000.0d), (int) min, (int) max));
         }
         if (listener instanceof ITextfieldListener) { ((ITextfieldListener) listener).unFocused(this); }
         return true;
      }
      return false;
   }

   public @Nonnull ResourceLocation getResourceLocation(@Nullable String domain) {
      if (domain == null || domain.isEmpty()) { domain = "minecraft"; }
      if (isEmpty()) { return new ResourceLocation(domain, "empty"); }
      String value = getValue();
      if (resourceLocationType == 2 || !value.contains(":")) {
         String path = NoppesUtilServer.validPath(value);
         if (!path.equals(getValue())) { setValue(path); }
         return new ResourceLocation(domain, path);
      }
      ResourceLocation location = new ResourceLocation(NoppesUtilServer.validLocation(value));
      if (!location.toString().equals(getValue())) { setValue(location.toString()); }
      return location;
   }

}
