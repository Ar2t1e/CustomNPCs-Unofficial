package noppes.npcs.client.gui;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import noppes.npcs.CustomNpcs;
import noppes.npcs.blocks.tiles.TileWaypoint;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketTileEntityGet;
import noppes.npcs.packets.server.SPacketTileEntitySave;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.IGuiData;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;

public class GuiNpcWaypoint extends GuiNPCInterface implements IGuiData, ITextfieldListener {

   protected final TileWaypoint tile;

   public GuiNpcWaypoint(BlockPos pos) {
      super();
      imageWidth = 265;

      tile = (TileWaypoint) player.level().getBlockEntity(pos);
      Packets.sendServer(new SPacketTileEntityGet(pos));
   }

   @Override
   public void init() {
      super.init();
      if (tile == null) { onClose(); }
      else {
         // name
         addLabel(0, guiLeft + 1, guiTop + 76, "gui.name")
                 .setColor(CustomNpcs.MainColor.getRGB());
         addTextField(0, guiLeft + 60, guiTop + 71, 200, 20, tile.name);
         // range
         addLabel(1, guiLeft + 1, guiTop + 97, "gui.range")
                 .setColor(CustomNpcs.MainColor.getRGB());
         addTextField(1, guiLeft + 60, guiTop + 92, 200, 20, tile.range)
                 .setMinMaxDefault(2, 60, 10);
         // exit
         addButton(0, guiLeft + 40, guiTop + 190,"gui.done")
                 .setSize(120, 20)
                 .setHoverTexts("hover.exit");
      }
   }

   @Override
   public void buttonEvent(GuiButtonNop button) {
      if (button.id == 0) { onClose(); }
   }

   @Override
   public void save() { Packets.sendServer(new SPacketTileEntitySave(tile.saveWithFullMetadata())); }

   @Override
   public void setGuiData(CompoundTag compound) {
      tile.load(compound);
      init();
   }

   @Override
   public void unFocused(GuiTextFieldNop textField) {
      switch (textField.id) {
         case 0: tile.name = textField.getValue(); break;
         case 1: tile.range = textField.getInteger(); break;
      }
   }

}
