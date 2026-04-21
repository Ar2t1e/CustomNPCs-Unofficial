package noppes.npcs.api.wrapper;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.registries.ForgeRegistries;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.ITimers;
import noppes.npcs.api.block.IBlockScriptedDoor;
import noppes.npcs.blocks.tiles.TileScriptedDoor;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.mixin.world.entity.IEntityMixin;

public class BlockScriptedDoorWrapper extends BlockWrapper implements IBlockScriptedDoor {

   protected TileScriptedDoor tile;

   public BlockScriptedDoorWrapper(Level level, BlockState state, BlockPos pos) {
      super(level, state, pos);
      tile = (TileScriptedDoor) super.tile;
   }

   @Override
   public boolean getOpen() { return getMCBlockState().getValue(DoorBlock.OPEN).equals(true); }

   @Override
   public void setOpen(boolean open) {
      if (getOpen() != open && !isRemoved() && level != null) {
         ((DoorBlock) getMCBlock()).setOpen(null, level.getMCLevel(), getMCBlockState(), iPos.blockPos, open);
      }
   }

   @Override
   public void setBlockModel(String name) {
      Block b = null;
      if (name != null) {
         b = ForgeRegistries.BLOCKS.getValue(ResourceLocation.tryParse(name));
      }
      tile.setItemModel(b);
   }

   @Override
   public String getBlockModel() {
      ResourceLocation registerName = ForgeRegistries.BLOCKS.getKey(tile.blockModel);
      return registerName != null ? registerName.toString() : "minecraft:air";
   }

   @Override
   public ITimers getTimers() { return tile.timers; }

   @Override
   public float getHardness() { return tile.blockHardness; }

   @Override
   public void setHardness(float hardness) { tile.blockHardness = hardness; }

   @Override
   public float getResistance() { return tile.blockResistance; }

   @Override
   public void setResistance(float resistance) { tile.blockResistance = resistance; }

   @Override
   protected void setTile(BlockEntity tileIn) {
      if (tileIn instanceof TileScriptedDoor door) {
         tile = door;
         super.setTile(tile);
      }
   }

   @Override
   public String getSound(boolean isOpen) { return tile.getSound(isOpen); }

   @Override
   public void setSound(boolean isOpen, String song) { tile.setSound(isOpen, song); }

   @Override
   public String executeCommand(String command) {
      if (tile.getLevel() == null || tile.getLevel().getServer() == null) {
         throw new CustomNPCsException("There is no world or server to execute the command!");
      }
      if (!tile.getLevel().getServer().isCommandBlockEnabled()) {
         throw new CustomNPCsException("Command blocks need to be enabled to executeCommands");
      }
      FakePlayer player = EntityNPCInterface.CommandPlayer;
      ((IEntityMixin) player).setLevel(tile.getLevel());
      player.setPos(getX(), getY(), getZ());
      return NoppesUtilServer.runCommand(tile.getLevel(), tile.getBlockPos(), "ScriptBlock: " + tile.getBlockPos(), command, null, player);
   }

}
