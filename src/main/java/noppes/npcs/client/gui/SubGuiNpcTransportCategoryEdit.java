package noppes.npcs.client.gui;

import net.minecraft.nbt.CompoundTag;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.controllers.data.TransportCategory;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketTransportCategoriesGet;
import noppes.npcs.packets.server.SPacketTransportCategorySave;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;

import java.awt.*;

public class SubGuiNpcTransportCategoryEdit extends GuiNPCInterface implements ITextfieldListener {

   protected final TransportCategory category;

   public SubGuiNpcTransportCategoryEdit(EntityNPCInterface npc, TransportCategory categoryIn) {
      super(npc);

      category = categoryIn;
   }

   @Override
   public void init() {
      super.init();
      addTextField(0, width / 2 - 40, 100, 140, 20, category.title);
      addLabel(0, width / 2 - 100 + 4, 105, "Title:")
              .setColor(new Color(0xFFFFFF).getRGB());
      addButton(1, width / 2 - 100, 210, "gui.back")
              .setSize(98, 20);
      addButton(2, width / 2 + 2, 210, "gui.save")
              .setSize(98, 20);
   }

   @Override
   public void buttonEvent(GuiButtonNop button) {
      switch (button.id) {
         case 0: {
            Packets.sendServer(new SPacketTransportCategoriesGet());
            onClose();
            break;
         }
         case 1: {
            save();
            Packets.sendServer(new SPacketTransportCategoriesGet());
            onClose();
            break;
         }
      }
   }

   @Override
   public void save() {
      CompoundTag compound = new CompoundTag();
      category.save(compound);
      Packets.sendServer(new SPacketTransportCategorySave(compound));
   }

   @Override
   public void unFocused(GuiTextFieldNop textField) { category.title = textField.getValue(); }

}
