package noppes.npcs.client.gui.roles;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import noppes.npcs.client.gui.select.SubGuiSoundSelection;
import noppes.npcs.shared.client.gui.GuiBasic;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SubGuiNpcConversationLine extends GuiBasic implements ITextfieldListener {

   public @Nonnull String line;
   public @Nullable ResourceLocation sound;

   public SubGuiNpcConversationLine(@Nonnull String lineIn, @Nonnull String soundIn) {
      super();
      setBackground("menubg.png");
      imageWidth = 212;
      imageHeight = 119;

      line = lineIn;
      sound = new ResourceLocation(soundIn);
   }

   @Override
   public void init() {
      super.init();
      int x = guiLeft + 5;
      int y = guiTop + 6;
      // message
      addLabel(0, x + 1, y, Component.translatable("conversation.line").append(":"));
      addTextField(0, x + 1, y += 11, 200, 18, line);
      // sound
      addLabel(1, x + 1, y += 22, Component.translatable("stats.firesound").append(":"));
      addTextField(1, x + 1, y += 11, 200, 18, sound)
              .setResourceLocationType(1);
      addButton(1, x, y += 22, "gui.selectSound")
              .setSize(90, 20);
      addButton(2, x + 96, y, "X")
              .setSize(20, 20);
      // exit
      addButton(66, guiLeft + imageWidth - 96, y + 22, "gui.done")
              .setSize(90, 20);
   }

   @Override
   public void buttonEvent(GuiButtonNop button) {
      switch (button.id) {
         case 1: setSubGui(new SubGuiSoundSelection(this, 0, null, sound != null ? sound.toString() : "")); break;
         case 2: sound = null; init(); break;
         case 66: onClose(); break;
      }
   }

   @Override
   public void subGuiClosed(Screen subgui) {
      if (subgui instanceof SubGuiSoundSelection gui && gui.resource != null) { sound = gui.resource; }
   }

   @Override
   public void unFocused(GuiTextFieldNop textField) {
      switch (textField.id) {
         case 0: line = textField.getValue(); break;
         case 1: {
            if (textField.isEmpty() || textField.getValue().equals("minecraft:")) {
               sound = null;
               textField.setValue("");
            }
            else { sound = textField.getResourceLocation(null); }
            break;
         }
      }
   }

}
