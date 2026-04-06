package noppes.npcs.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.shared.client.gui.GuiBasic;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;
import noppes.npcs.util.Util;

import java.util.*;

// Change from Unofficial (BetaZavr)
public class SubGuiEditText extends GuiBasic implements ITextfieldListener {

   public boolean cancelled;
   public int id;

   // New from Unofficial (BetaZavr)
   protected String oldName = "";
   public final Map<Integer, List<Component>> hovers = new TreeMap<>();
   public int[] numbersOnly; // min, max, def
   public String label;
   public String[] text;
   public boolean latinAlphabetOnly = false;
   public boolean allowUppercase = true;

   public SubGuiEditText(String text) { this(0, new String[] { text }); }

   public SubGuiEditText(int idIn, String text) {
      this(new String[] { text });
      id = idIn;
   }

   public SubGuiEditText(int idIn, String[] texts) {
      this(texts);
      id = idIn;
   }

   public SubGuiEditText(String[] texts) {
      super();
      numbersOnly = null;
      label = null;
      cancelled = true;
      text = new String[Math.min(texts.length, 5)];
      for (int i = 0; i < texts.length && i < 5; i++) {
         text[i] = Util.instance.deleteColor(texts[i]);
         hovers.put(i, Collections.singletonList(Component.empty()));
      }
      setBackground("smallbg.png");
      closeOnEsc = true;
      imageWidth = 176;
      imageHeight = 49 + text.length * 22;
   }

   @Override
   public void init() {
      super.init();
      GuiTextFieldNop textField;
      for (int i = 0; i < text.length && i < 5; i++) {
         textField = new GuiTextFieldNop(this, i, guiLeft + 4, guiTop + 16 + i * 22 + (label != null ? 2 : 0), 168, 20, text[i])
                 .setLatinAlphabetOnly(latinAlphabetOnly)
                 .setAllowUppercase(allowUppercase);
         if (numbersOnly != null) { textField.setMinMaxDefault(numbersOnly[0], numbersOnly[1], numbersOnly[2]); }
         add(textField);
      }
      addButton(0, guiLeft + 4, guiTop + 22 + text.length * 22, "gui.done").setSize(80, 20);
      addButton(1, guiLeft + 90, guiTop + 22 + text.length * 22, "gui.cancel").setSize(80, 20);
      if (label != null && !label.isEmpty()) { addLabel(0, guiLeft + 7, guiTop + 5, label); }
   }

   @Override
   public void buttonEvent(GuiButtonNop button) {
      if (button.id == 0) {
         cancelled = false;
         for (int i = 0; i < text.length; i++) { text[i] = getTextField(i).getValue(); }
         onClose();
      }
   }

   @Override
   public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      synchronized (hovers) {
         for (int i : hovers.keySet()) {
            if (hovers.get(i) == null || hovers.get(i).isEmpty() || getTextField(i) == null) { continue; }
            if (getTextField(i).isHovered()) {
               setHoverText(hovers.get(i));
               break;
            }
         }
      }
      super.render(graphics, mouseX, mouseY, partialTicks);
      PoseStack matrixStack = graphics.pose();
      matrixStack.pushPose();
      matrixStack.translate(guiLeft, guiTop, 0.0f);
      matrixStack.scale(bgScale, bgScale, bgScale);
      RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
      if (imageWidth > 256) {
         graphics.blit(background, 0, imageHeight - 1, 0, 218, 250, imageHeight);
         graphics.blit(background, 250, imageHeight - 1, 256 - (imageWidth - 250), 218, imageWidth - 250, imageHeight);
      }
      else { graphics.blit(background, 0, imageWidth - 1, 0, 218, imageWidth, 4); }
      matrixStack.popPose();
   }

   @Override
   public void unFocused(GuiTextFieldNop textField) {
      if (!textField.getValue().isEmpty() &&
              hovers.containsKey(0) && !hovers.get(0).isEmpty() &&
              hovers.get(0).get(0).getContents() instanceof TranslatableContents tr &&
              tr.getKey().equals("hover.player")) {
         for (String name : PlayerDataController.instance.getPlayerNames()) {
            if (textField.getValue().equalsIgnoreCase(name)) {
               textField.setValue(name);
               oldName = name;
               return;
            }
         }
         textField.setValue(oldName);
      }
      oldName = textField.getValue();
   }

   public SubGuiEditText setHoverTexts(Component ... newHovers) {
      for (int i : hovers.keySet()) {
         setHoverText(i < newHovers.length ? newHovers[i].getString() : "");
         hovers.put(i, new ArrayList<>(hoverText));
         hoverText.clear();
      }
      return this;
   }

}
