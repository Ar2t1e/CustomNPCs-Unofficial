package noppes.npcs.shared.client.gui.components;

import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.constants.GuiComponentType;
import noppes.npcs.client.ClientProxy;
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
      setTextColor(getTextColor());
      super.renderWidget(graphics, mouseX, mouseY, partialTicks);
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

   public GuiTextFieldNop setNumbersOnly() {
      numbersOnly = true;
      doublesOnly = false;
      return this;
   }

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
      resourceLocationType = ValueUtil.correctInt(type, 0, 2);
      return this;
   }

   public GuiTextFieldNop setEditableIn(boolean editable) {
      setEditable(editable);
      return this;
   }

   public boolean isLatinAlphabetOnly() { return latinAlphabetOnly; }

   public GuiTextFieldNop setLatinAlphabetOnly(boolean isLatinAlphabetOnly) {
      latinAlphabetOnly = isLatinAlphabetOnly;
      return this;
   }


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
      return super.keyPressed(keyCode, scanCode, modifiers) || activeTextfield != null;
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
      if (listener instanceof ITextChangeListener gui && !oldText.equals(getValue())) { gui.textUpdate(getValue()); }
   }

   @Override
   public void deleteChars(int pos) {
      String oldText = getValue();
      super.deleteChars(pos);
      if (listener instanceof ITextChangeListener gui && !oldText.equals(getValue())) { gui.textUpdate(getValue()); }
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
      if (listener instanceof ITextChangeListener gui && !oldText.equals(getValue())) { gui.textUpdate(getValue()); }
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
