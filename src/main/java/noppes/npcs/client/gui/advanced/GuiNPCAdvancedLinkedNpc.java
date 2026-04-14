package noppes.npcs.client.gui.advanced;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import noppes.npcs.client.gui.util.GuiNPCInterface2;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketLinkedGet;
import noppes.npcs.packets.server.SPacketLinkedSet;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiCustomScrollNop;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;
import noppes.npcs.shared.client.gui.listeners.IScrollData;

public class GuiNPCAdvancedLinkedNpc extends GuiNPCInterface2
        implements IScrollData, ICustomScrollListener {

   protected final List<String> data = new ArrayList<>();
   protected GuiCustomScrollNop scroll;

   public GuiNPCAdvancedLinkedNpc(EntityNPCInterface npc) {
      super(npc);
      backGui = EnumGuiType.MainMenuAdvanced;
      Packets.sendServer(new SPacketLinkedGet());
   }

   @Override
   public void init() {
      super.init();
      int x = guiLeft + 137;
      if (scroll == null) { scroll = addScroll(0).setSize(143, 208); }
      add(scroll.setSelectedIndex(npc.linkedName)
              .setPos(x, guiTop + 5)
              .setList(data));
      // clear
      addButton(1, x + scroll.width + 3, guiTop + 38, "gui.clear")
              .setSize(60, 20);
      // help
      addLabel(0, x + scroll.width + 12, guiTop + 62, "type.help")
              .setBackColor(0x80808080)
              .setBorderColor(0x80808080)
              .setHoverTexts("linked.hover.info");
   }

   @Override
   public void buttonEvent(GuiButtonNop button) {
      if (button.id == 1) {
         Packets.sendServer(new SPacketLinkedSet(""));
         scroll.setSelectedIndex("");
      }
   }

   @Override
   public void setData(Vector<String> dataList, Map<String, Integer> dataMap) {
      data.clear();
      data.addAll(dataList);
      init();
   }

   @Override
   public void setSelected(String selected) { scroll.setSelectedIndex(selected); }

   @Override
   public void save() { }

   @Override
   public void scrollClicked(GuiCustomScrollNop scroll) { Packets.sendServer(new SPacketLinkedSet(scroll.getSelected())); }

   @Override
   public void scrollDoubleClicked(GuiCustomScrollNop scroll) { }

}
