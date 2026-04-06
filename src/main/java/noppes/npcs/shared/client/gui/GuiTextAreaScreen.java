package noppes.npcs.shared.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiTextArea;
import noppes.npcs.shared.client.gui.listeners.ITextChangeListener;
import noppes.npcs.shared.client.gui.util.NoppesStringUtils;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.util.Util;

@OnlyIn(Dist.CLIENT)
public class GuiTextAreaScreen extends GuiBasic implements ITextChangeListener {

   protected boolean highlighting = false;
   protected GuiTextArea textarea;
   public final int id;
   public String text;
   public String originalText;

   public GuiTextAreaScreen(int idIn, String textIn) {
      super();
      setBackground("bgfilled.png");
      imageWidth = 256;
      imageHeight = 256;

      id = idIn;
      text = textIn;
      originalText = text;
   }

   public GuiTextAreaScreen(int id, String originalTextIn, String textIn) {
      this(id, textIn);
      originalText = originalTextIn;
   }

   @Override
   public void buttonEvent(GuiButtonNop button) {
      switch (button.id) {
         case 0: text = textarea.getText(); onClose(); break;
         case 100: NoppesStringUtils.setClipboardContents(textarea.getText()); break;
         case 101: textarea.setText(NoppesStringUtils.getClipboardContents()); break;
         case 102: textarea.setText(""); break;
         case 103: textarea.setText(originalText); break;
      }
      text = textarea.getText();
   }

   @Override
   public void init() {
      imageWidth = (int)((double) width * 0.95D);
      imageHeight = (int)((double) imageWidth * 0.56D);
      if ((double) imageHeight > (double) height * 0.975D) {
         imageHeight = (int)((double) height * 0.975D);
         imageWidth = (int)((double) imageHeight / 0.56D);
      }
      super.init();
      if (textarea != null) { text = textarea.getText(); }
      int w = 80;
      int x = guiLeft + imageWidth - w - 6;
      textarea = new GuiTextArea(2, guiLeft + 5, guiTop + 5, imageWidth - w - 14, imageHeight - 10, text)
              .setListener(this);
      if (highlighting) { textarea.enableCodeHighlighting(); }
      add(textarea);
      int y = guiTop + 20;
      addButton(102,  x, y, "gui.clear")
              .setSize(w, 20);
      addButton(101, x, y += 23, "gui.paste")
              .setSize(w, 20);
      addButton(100, x, y += 23, "gui.copy")
              .setSize(w, 20);
      addButton(103, x, y += 23, "gui.reset")
              .setSize(w, 20);
      addLabel(0, x, y + 23, "_[?]_");
      addButton(0, x, guiTop + imageHeight - 24, "gui.close")
              .setSize(w, 20);
   }

   @Override
   public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      GuiLabel label = getLabel(0);
      if (label != null) {
         String text = textarea.getText();
         String translatable = Component.translatable(text).getString();
         label.setIsVisible(!Util.instance.equalsDeleteColor(translatable, text, false));
         if (label.isVisible()) { label.setHoverTexts(translatable); }
      }
      super.render(graphics, mouseX, mouseY, partialTicks);
   }

   @Override
   public void textUpdate(String textIn) { text = textIn; }

   public GuiTextAreaScreen enableHighlighting() {
      highlighting = true;
      return this;
   }

}
