package noppes.npcs.client.gui;

import net.minecraft.nbt.CompoundTag;
import noppes.npcs.entity.data.DataDisplay;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketNpRandomNameSet;
import noppes.npcs.shared.client.gui.GuiBasic;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.IGuiData;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;

public class SubGuiNpcName extends GuiBasic implements ITextfieldListener, IGuiData {

   protected final DataDisplay display;

   public SubGuiNpcName(DataDisplay displayIn) {
      super();
      display = displayIn;
      setBackground("menubg.png");

      imageWidth = 256;
      imageHeight = 216;
   }

   @Override
   public void init() {
      super.init();
      int x = guiLeft + 4;
      int y = guiTop + 4;
      addButton(66, guiLeft + imageWidth - 24, y, "X")
              .setSize(20, 20)
              .setHoverTexts("hover.back");
      addTextField(0, x, y += 50, 226, 20, display.getName())
              .setHoverTexts("display.hover.name");
      addButton(1, x, y += 22, true, display.getMarkovGeneratorId(),
              "markov.roman.name", "markov.japanese.name", "markov.slavic.name", "markov.welsh.name", "markov.sami.name",
              "markov.oldNorse.name", "markov.ancientGreek.name", "markov.aztec.name", "markov.classicCNPCs.name", "markov.spanish.name")
              .setSize(200, 20)
              .setHoverTexts("display.hover.group.name");
      addButton(2, x + 60, y += 22, true, display.getMarkovGender(),
              "markov.gender.either", "markov.gender.male", "markov.gender.female")
              .setSize(120, 20)
              .setHoverTexts("display.hover.group.either");
      addLabel(2, x + 1, y + 5, "markov.gender.name");
      addButton(3, x, y + 42, "markov.generate")
              .setSize(70, 20)
              .setHoverTexts("display.hover.random.name");
   }

   @Override
   public void buttonEvent(GuiButtonNop button) {
      switch (button.id) {
         case 1: display.setMarkovGeneratorId(button.getValue()); break;
         case 2: display.setMarkovGender(button.getValue()); break;
         case 3: Packets.sendServer(new SPacketNpRandomNameSet(display.getMarkovGeneratorId(), display.getMarkovGender())); break;
         case 66: onClose(); break;
      }
   }

   @Override
   public void setGuiData(CompoundTag compound) {
      display.load(compound);
      init();
   }

   @Override
   public void unFocused(GuiTextFieldNop textfield) {
      if (textfield.id == 0) {
         if (!textfield.isEmpty()) { display.setName(textfield.getValue()); }
         else { textfield.setValue(display.getName()); }
      }
   }

}
