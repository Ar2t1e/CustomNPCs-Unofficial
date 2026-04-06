package noppes.npcs.client.gui;

import java.util.*;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import noppes.npcs.controllers.data.FactionOption;
import noppes.npcs.controllers.data.FactionOptions;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketFactionsGet;
import noppes.npcs.shared.client.gui.GuiBasic;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiCustomScrollNop;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;
import noppes.npcs.shared.client.gui.listeners.IScrollData;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;
import noppes.npcs.util.Util;

public class SubGuiNpcFactionOptions
        extends GuiBasic
        implements IScrollData, ICustomScrollListener, ITextfieldListener {

   protected final FactionOptions options;
   protected final Map<Component, Integer> data = new HashMap<>();
   protected GuiCustomScrollNop scroll;

   public SubGuiNpcFactionOptions(FactionOptions optionsIn) {
      super();
      setBackground("menubg.png");
      imageWidth = 256;
      imageHeight = 216;

      options = optionsIn;
      Packets.sendServer(new SPacketFactionsGet());
   }

   @Override
   public void init() {
      super.init();
      if (scroll == null) { scroll = addScroll(0).setSize(120, 195); }
      int x0 = guiLeft + 6;
      int x1 = x0 + 123;
      int y = guiTop + 5;
      addLabel(1, x0 + 1, y, Component.translatable("faction.options").append(":"))
              .setSize(123, 10);
      addLabel(2, x1 + 1, y, Component.translatable("gui.settings").append(":"))
              .setSize(123, 10)
              .setIsVisible(scroll.hasSelected());
      add(scroll.setPos(x0, y += 11));
      FactionOption fo = null;
      if (scroll.hasSelected() && data.get(scroll.getNormalSelected()) != null) { fo = options.get(data.get(scroll.getNormalSelected())); }
      // faction points
      addTextField(1, x1 + 1, y + 1, 120, 18, fo != null ? "" + fo.factionPoints : "0")
              .setMinMaxDefault(-100000, 100000, fo != null ? fo.factionPoints : 0)
              .setEditableIn(scroll.hasSelected())
              .setHoverTexts("faction.hover.option.points");
      addButton(1, x1, y + 22, false, fo != null && fo.decreaseFactionPoints ? 1 : 0, "gui.add", "gui.decrease")
              .setSize(90, 20)
              .setIsVisible(scroll.hasSelected())
              .setHoverTexts("faction.hover.option.decrease");
      addButton(66, x1, guiTop + imageHeight - 25, "gui.back")
              .setSize(90, 20)
              .setHoverTexts("hover.back");
   }

   @Override
   public void buttonEvent(GuiButtonNop guiButton) {
      switch (guiButton.id) {
         case 1 : change(guiButton.getValue() == 1, getTextField(1) == null ? 0 : getTextField(1).getInteger()); break;
         case 66 : onClose(); break;
      }
   }

   @Override
   public void scrollClicked(GuiCustomScrollNop scroll) { init(); }

   @Override
   public void scrollDoubleClicked(GuiCustomScrollNop scroll) { }

   @Override
   public void setData(Vector<String> dataList, Map<String, Integer> dataMap) {
      data.clear();
      String name = Util.instance.deleteColor(scroll.getSelected());
      if (name != null && name.contains("ID:") && name.indexOf(" - ") >= name.indexOf("ID:")) {
         name = name.substring(name.indexOf(" - ") + 3);
      }
      Map<String, Component> newList = new TreeMap<>();
      Map<Component, Component> hoverMap = new HashMap<>();
      for (String key : dataMap.keySet()) {
         int id = dataMap.get(key);
         String newName = Util.instance.deleteColor(key);
         if (newName.contains("ID:" + id + " - ")) {
            newName = newName.substring(newName.indexOf(" - ") + 3);
         }
         newName = Component.translatable(newName).getString();

         Component str = Component.empty().append(Component.literal("ID:" + id + " - " + newName).withStyle(ChatFormatting.GRAY));
         if (options.hasFaction(id)) {
            FactionOption fo = options.get(id);
            str = Component.empty()
                    .append(Component.literal("ID:" + id + " - ").withStyle(ChatFormatting.GRAY))
                    .append(Component.literal(newName).withStyle(fo.decreaseFactionPoints ? ChatFormatting.RED : ChatFormatting.DARK_GREEN));
         }
         newList.put(str.getString(), str);
         hoverMap.put(str, Component.literal(newName));
         data.put(str, id);
         if (name != null && name.equals(Util.instance.deleteColor(newName))) { name = str.getString(); }
      }
      LinkedHashMap<Integer, List<Component>> hts = new LinkedHashMap<>();
      int i = 0;
      for (Component key : newList.values()) {
         hts.put(i, Collections.singletonList(hoverMap.get(key)));
         i++;
      }
      scroll.setUnsortedList(new ArrayList<>(newList.values()));
      scroll.setHoverTexts(hts);
      if (name != null) { scroll.setSelected(name); }
      init();
   }

   @Override
   public void setSelected(String selected) { }

   // New from Unofficial (BetaZavr)
   private void change(boolean isTake, int value) {
      if (!scroll.hasSelected() || !data.containsKey(scroll.getNormalSelected())) { return; }
      int id = data.get(scroll.getNormalSelected());
      FactionOption fo = options.get(id);
      if (fo == null) {
         if (value == 0) { return; }
         fo = new FactionOption(id, value, isTake);
         options.factionOptions.add(fo);
      }
      else {
         if (value == 0) {
            if (options.remove(id)) { fo = null; }
         } else {
            fo.factionPoints = value;
            fo.decreaseFactionPoints = isTake;
         }
      }
      if (fo != null) { fo.check(); }
      Map<String, Integer> dataMap = new HashMap<>();
      for (Component component : data.keySet()) { dataMap.put(component.getString(), data.get(component)); }
      setData(null, dataMap);
   }

   @Override
   public void unFocused(GuiTextFieldNop textField) {
      change(getButton(1) != null && getButton(1).getValue() == 1, textField.getInteger());
   }

}
