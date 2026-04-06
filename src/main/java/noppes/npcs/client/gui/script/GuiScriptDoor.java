package noppes.npcs.client.gui.script;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import noppes.npcs.blocks.tiles.TileScriptedDoor;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketScriptGet;

public class GuiScriptDoor extends GuiScriptInterface {

   protected final TileScriptedDoor script;

   public GuiScriptDoor(BlockPos pos) {
      super(5);
      handler = script = (TileScriptedDoor) player.level().getBlockEntity(pos);
      Packets.sendServer(new SPacketScriptGet(type));
   }

   @Override
   public void setGuiData(CompoundTag compound) {
      script.setNBT(compound);
      super.setGuiData(compound);
   }

   @Override
   public void save() {
      super.save();
      sendToServer(script.getNBT(new CompoundTag()));
   }

}
