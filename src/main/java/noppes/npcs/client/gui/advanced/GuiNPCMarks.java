package noppes.npcs.client.gui.advanced;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import noppes.npcs.api.constants.MarkType;
import noppes.npcs.client.gui.select.SubGuiColorSelector;
import noppes.npcs.client.gui.availability.SubGuiNpcAvailability;
import noppes.npcs.client.gui.util.GuiNPCInterface2;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.constants.EnumMenuType;
import noppes.npcs.controllers.data.Availability;
import noppes.npcs.controllers.data.MarkData;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketMenuSave;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiCustomScrollNop;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;
import noppes.npcs.util.Util;

import java.util.ArrayList;
import java.util.List;

public class GuiNPCMarks
        extends GuiNPCInterface2
        implements ICustomScrollListener {

   protected static final Object[] marks;
   protected final MarkData data;
   protected MarkData.Mark selectedMark;

   // New from Unofficial Betazavr
   static {
      marks = new Object[MarkType.values().length]; // { "gui.none", "mark.question", "mark.exclamation", "mark.pointer", "mark.skull", "mark.cross", "mark.star" };
      int i = 0;
      for (MarkType mt : MarkType.values()) {
         marks[i++] = (mt == MarkType.NONE ? "gui." : "mark.") + mt.name().toLowerCase();
      }
   }
   protected final EntityNPCInterface npcDisplay;
   protected final MarkData dataDisplay;
   protected final Screen parent;
   protected GuiCustomScrollNop scroll;
   protected Component selMark = Component.empty();

   public GuiNPCMarks(EntityNPCInterface npc, Screen gui) {
      super(npc);
      backGui = EnumGuiType.MainMenuAdvanced;

      if (minecraft == null) { minecraft = Minecraft.getInstance(); }
      data = MarkData.get(npc);
      parent = gui;
      npcDisplay = Util.instance.copyToGUI(npc, player.level(), false);
      dataDisplay = MarkData.get(npcDisplay);
   }

   @Override
   public void buttonEvent(GuiButtonNop button) {
      if (selectedMark == null) { return; }
      switch (button.id) {
         case 0: selectedMark.setType(button.getValue()); init(); break;
         case 1: {
            setSubGui(new SubGuiColorSelector(selectedMark.color, new SubGuiColorSelector.ColorCallback() {
               @Override
               public void color(int colorIn) {
                  if (selectedMark == null) { return; }
                  if (!data.marks.isEmpty() && scroll.hasSelected() && scroll.getSelectedIndex() < data.marks.size()) {
                     data.marks.get(scroll.getSelectedIndex()).setColor(colorIn);
                  }
                  selectedMark.color = colorIn;
                  init();
               }
               @Override
               public void preColor(int colorIn) {
                  if (!dataDisplay.marks.isEmpty()) { dataDisplay.marks.get(0).setColor(colorIn); }
               }
            }));
            break;
         }
         case 2: setSubGui(new SubGuiNpcAvailability(selectedMark.availability, parent)); break;
         case 3: {
            MarkData.Mark newark = data.addMark(selectedMark.getType());
            newark.color = selectedMark.color;
            newark.rotate = selectedMark.rotate;
            newark.availability.load(selectedMark.availability.save(new CompoundTag()));
            selectedMark = newark;
            init();
            break;
         }
         case 4: {
            if (!scroll.hasSelected()) { return; }
            data.marks.remove(selectedMark);
            scroll.setSelected(-1);
            selMark = Component.empty();
            selectedMark = null;
            init();
            break;
         }
         case 5: selectedMark.rotate = button.getValue() == 0; init(); break;
         case 6: selectedMark.is3d = button.getValue() == 0; init(); break;
      }
   }

   @Override
   public void init() {
      super.init();
      List<Component> ds = new ArrayList<>();
      int i = 0;
      for (MarkData.Mark mark : data.marks) {
         MutableComponent key = Component.literal(i + ": ");
         MutableComponent name = Component.translatable((String) marks[mark.getType()]);
         name.withStyle(name.getStyle().withColor(mark.color));
         key.append(name);
         ds.add(key);
         if (!selMark.getString().isEmpty() && selMark.getString().equals(key.getString())) {
            selectedMark = mark;
         }
         i++;
      }
      if (scroll == null) { scroll = addScroll(0).setSize(130, 174); }
      scroll.setList(new ArrayList<>())
              .setUnsortedList(ds);
      if (selectedMark != null && !selMark.getString().isEmpty()) { scroll.setSelected(selMark); }
      add(scroll.setPos(guiLeft + 5, guiTop + 14));
      if (selectedMark == null) { selectedMark = data.getNewMark(); }
      // type
      addButton(0, guiLeft + 140, guiTop + 14, true, selectedMark.getType(), marks)
              .setSize(120, 20)
              .setHoverTexts("mark.hover.type");
      // color
      StringBuilder color = new StringBuilder(Integer.toHexString(selectedMark.getColor()));
      while (color.length() < 6) { color.insert(0, "0"); }
      addButton(1, guiLeft + 140, guiTop + 36, color.toString())
              .setSize(120, 20)
              .setHoverTexts("color.hover")
              .setColor(selectedMark.getColor());
      // availability
      addButton(2, guiLeft + 140, guiTop + 58,  "availability.options")
              .setSize(120, 20)
              .setHoverTexts("availability.hover");
      // add
      addButton(3, guiLeft + 5, guiTop + imageHeight - 9, "gui.add")
              .setSize(64, 20)
              .setIsEnabled(selectedMark.getType() > 0)
              .setHoverTexts("mark.hover.add");
      // del
      addButton(4, guiLeft + 71, guiTop + imageHeight - 9, "gui.remove")
              .setSize(64, 20)
              .setIsEnabled(scroll.hasSelected())
              .setHoverTexts("mark.hover.del");
      // is rotation
      addButton(5, guiLeft + 140, guiTop + 80, false, selectedMark.rotate ? 0 : 1, "movement.rotation", "ai.standing")
              .setSize(120, 20)
              .setHoverTexts("mark.hover.rotate");
      // view
      addButton(6, guiLeft + 140, guiTop + 102, false, selectedMark.is3d ? 0 : 1, "3D", "2D")
              .setSize(120, 20)
              .setHoverTexts("mark.hover.is3d");
      // list
      dataDisplay.marks.clear();
      MarkData.Mark mark = dataDisplay.addMark(selectedMark.getType());
      mark.setColor(selectedMark.color);
      mark.setRotate(selectedMark.rotate);
      mark.set3D(selectedMark.is3d);
      mark.availability = new Availability();
      addLabel(5, guiLeft + 5, guiTop + 4, Component.translatable("advanced.marks").append(":"));
   }

   @Override
   public void save() { Packets.sendServer(new SPacketMenuSave(EnumMenuType.MARK, data.getNBT())); }

   // New from Unofficial Betazavr
   @Override
   public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      PoseStack matrixStack = graphics.pose();
      matrixStack.pushPose();
      matrixStack.translate(0.0f, 0.0f, 1.0f);
      graphics.fill(guiLeft + 319, guiTop + 30, guiLeft + 380, guiTop + 165, 0xFF808080);
      graphics.fill(guiLeft + 320, guiTop + 31, guiLeft + 379, guiTop + 164, 0xFF000000);
      matrixStack.popPose();
      matrixStack.pushPose();
      drawNpc(graphics, npcDisplay, 350, 150, 1.0f, 0, 0, 1);
      matrixStack.popPose();
      super.render(graphics, mouseX, mouseY, partialTicks);
   }

   @Override
   public void scrollClicked(GuiCustomScrollNop scroll) {
      if (selMark.getString().equals(scroll.getSelected())) { return; }
      selMark = scroll.getNormalSelected();
      init();
   }

   @Override
   public void scrollDoubleClicked(GuiCustomScrollNop scroll) { }


}
