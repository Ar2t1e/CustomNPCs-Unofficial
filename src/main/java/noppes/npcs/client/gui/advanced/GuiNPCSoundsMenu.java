package noppes.npcs.client.gui.advanced;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import noppes.npcs.client.gui.select.SubGuiSoundSelection;
import noppes.npcs.client.gui.util.GuiNPCInterface2;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.constants.EnumMenuType;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketMenuSave;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiButtonYesNo;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;

public class GuiNPCSoundsMenu extends GuiNPCInterface2 implements ITextfieldListener {

   protected GuiTextFieldNop selectedField;

   public GuiNPCSoundsMenu(EntityNPCInterface npc) {
      super(npc);
      backGui = EnumGuiType.MainMenuAdvanced;
   }

   @Override
   public void init() {
      super.init();
      int x0 = guiLeft + 5;
      int w = 80;
      int x1 = x0 + w + 3;
      int x2 = x1 + 203;
      int x3 = x2 + 82;
      int y = guiTop + 15;
      for (int i = 0; i < 5; i++) {
         String name = switch (i) {
             case 1 -> "advanced.angersound";
             case 2 -> "advanced.hurtsound";
             case 3 -> "advanced.deathsound";
             case 4 -> "advanced.stepsound";
             default -> "advanced.idlesound";
         };
         addLabel(i, x0, y + 5, name)
                 .setSize(w, 10);
         addTextField(i, x1, y, 200, 20, npc.advanced.getSound(i));
         addButton(i, x2, y, "gui.selectSound")
                 .setSize(80, 20);
         addButton(10 + i, x3, y, "X")
                 .setSize(20, 20);
         y += 23;
      }
      addLabel(5, x0, y + 5, "advanced.haspitch")
              .setSize(w + 200, 10);
      addYesNo(5, x2, y, npc.advanced.disablePitch)
              .setSize(80, 20);
   }

   @Override
   public void buttonEvent(GuiButtonNop button) {
      if (button.id == 5) { npc.advanced.disablePitch = ((GuiButtonYesNo) button).getBoolean(); }
      else if (button.id < 10) {
         selectedField = getTextField(button.id);
         setSubGui(new SubGuiSoundSelection(this, 0, npc, selectedField.getValue()));
      }
      else {
         selectedField = getTextField(button.id - 10);
         selectedField.setValue("");
         unFocused(selectedField);
      }
   }

   @Override
   public void unFocused(GuiTextFieldNop textField) { npc.advanced.setSound(textField.id, textField.getValue()); }

   @Override
   public void save() { Packets.sendServer(new SPacketMenuSave(EnumMenuType.ADVANCED, npc.advanced.save(new CompoundTag()))); }

   @Override
   public void subGuiClosed(Screen subgui) {
      if (subgui instanceof SubGuiSoundSelection gui && gui.resource != null) {
         selectedField.setValue(gui.resource.toString());
         unFocused(selectedField);
      }
   }

}
