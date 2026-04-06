package noppes.npcs.client.gui;

import net.minecraft.network.chat.Component;
import noppes.npcs.shared.client.gui.GuiBasic;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;

public class SubGuiNpcCommand extends GuiBasic implements ITextfieldListener {

   public String command;

   public SubGuiNpcCommand(String commandIn) {
      super();
      command = commandIn;
      setBackground("menubg.png");

      imageWidth = 256;
      imageHeight = 216;
   }

   @Override
   public void init() {
      super.init();
      int x = guiLeft + 4;
      int y = guiTop + 84;
      // text
      addTextField(4, x, y, 248, 20, command)
              .setHoverTexts(Component.translatable("command.hover.text", ((char) 167) + "6" + Short.MAX_VALUE))
              .setMaxLength(Short.MAX_VALUE);
      // extra info
      addLabel(4, x, y += 26, "advMode.command").setSize(248, 10);
      addLabel(5, x, y += 15, "advMode.nearestPlayer").setSize(248, 10);
      addLabel(6, x, y += 15, "advMode.randomPlayer").setSize(248, 10);
      addLabel(7, x, y += 15, "advMode.allPlayers").setSize(248, 10);
      addLabel(8, x, y + 15, "dialog.commandoptionplayer").setSize(248, 10);
      // exit
      addButton(66, guiLeft + 82, guiTop + 190, "gui.done")
              .setSize(98, 20)
              .setHoverTexts("hover.back");
   }

   @Override
   public void buttonEvent(GuiButtonNop button) {
      if (button.id == 66) { onClose(); }
   }

   public void unFocused(GuiTextFieldNop textfield) {
      if (textfield.id == 4) { command = textfield.getValue(); }
   }

}
