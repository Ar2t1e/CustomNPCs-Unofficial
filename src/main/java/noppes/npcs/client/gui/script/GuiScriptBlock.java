package noppes.npcs.client.gui.script;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import noppes.npcs.blocks.tiles.TileScripted;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketScriptGet;

public class GuiScriptBlock extends GuiScriptInterface {

   protected final TileScripted script;

   public GuiScriptBlock(BlockPos pos) {
      super(1);
      handler = script = (TileScripted) player.level().getBlockEntity(pos);
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
