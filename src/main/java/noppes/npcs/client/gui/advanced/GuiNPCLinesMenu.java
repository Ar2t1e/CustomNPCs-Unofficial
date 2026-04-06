package noppes.npcs.client.gui.advanced;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import noppes.npcs.client.gui.SubGuiNPCLinesEdit;
import noppes.npcs.client.gui.util.GuiNPCInterface2;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.constants.EnumMenuType;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketMenuSave;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiButtonYesNo;

public class GuiNPCLinesMenu extends GuiNPCInterface2 {

   public GuiNPCLinesMenu(EntityNPCInterface npc) {
      super(npc);
      backGui = EnumGuiType.MainMenuAdvanced;
   }

   @Override
   public void init() {
      super.init();
      int x = guiLeft + 85;
      int y = guiTop + 20;
      addButton(0, x, y, "lines.world");
      addButton(1, x, y += 23, "lines.attack");
      addButton(2, x, y += 23, "lines.interact");
      addButton(3, x, y += 23, "lines.killed");
      addButton(4, x, y += 23, "lines.kill");
      addButton(5, x, y += 23, "lines.npcinteract");
      addLabel(16, x, (y += 23) + 5, "lines.random")
              .setSize(148, 10);
      addYesNo(16, x + 150, y, !npc.advanced.orderedLines);
   }

   @Override
   public void buttonEvent(GuiButtonNop button) {
      switch (button.id) {
         case 0: setSubGui(new SubGuiNPCLinesEdit(0, npc, npc.advanced.worldLines, "lines.world")); break;
         case 1: setSubGui(new SubGuiNPCLinesEdit(1, npc, npc.advanced.attackLines, "lines.attack")); break;
         case 2: setSubGui(new SubGuiNPCLinesEdit(2, npc, npc.advanced.interactLines, "lines.interact")); break;
         case 3: setSubGui(new SubGuiNPCLinesEdit(3, npc, npc.advanced.killedLines, "lines.killed")); break;
         case 4: setSubGui(new SubGuiNPCLinesEdit(4, npc, npc.advanced.killLines, "lines.kill")); break;
         case 5: setSubGui(new SubGuiNPCLinesEdit(5, npc, npc.advanced.npcInteractLines, "lines.npcinteract")); break;
         case 16: npc.advanced.orderedLines = !((GuiButtonYesNo) button).getBoolean(); break;
      }
   }

   @Override
   public void save() { Packets.sendServer(new SPacketMenuSave(EnumMenuType.ADVANCED, npc.advanced.save(new CompoundTag()))); }

   // New from Unofficial (BetaZavr)
   @Override
   public void subGuiClosed(Screen subgui) {
      if (subgui instanceof SubGuiNPCLinesEdit sub) {
         sub.lines.correctLines();
         switch (sub.id) {
            case 0: npc.advanced.worldLines = sub.lines; break;
            case 1: npc.advanced.attackLines = sub.lines; break;
            case 2: npc.advanced.interactLines = sub.lines; break;
            case 3: npc.advanced.killedLines = sub.lines; break;
            case 4: npc.advanced.killLines = sub.lines; break;
            case 5: npc.advanced.npcInteractLines = sub.lines; break;
         }
         save();
      }
   }

}
