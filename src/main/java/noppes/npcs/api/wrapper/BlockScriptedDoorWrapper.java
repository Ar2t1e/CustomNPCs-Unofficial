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

   private TileScriptedDoor tile;

   public BlockScriptedDoorWrapper(Level level, Block block, BlockPos pos) {
      super(level, block, pos);
      this.tile = (TileScriptedDoor)super.tile;
   }

   public boolean getOpen() {
      BlockState state = this.level.getMCLevel().getBlockState(this.pos);
      return state.getValue(DoorBlock.OPEN).equals(true);
   }

   public void setOpen(boolean open) {
      if (this.getOpen() != open && !this.isRemoved()) {
         BlockState state = this.level.getMCLevel().getBlockState(this.pos);
         ((DoorBlock)this.block).setOpen(null, this.level.getMCLevel(), state, this.pos, open);
      }
   }

   public void setBlockModel(String name) {
      Block b = null;
      if (name != null) {
         b = ForgeRegistries.BLOCKS.getValue(ResourceLocation.tryParse(name));
      }
      this.tile.setItemModel(b);
   }

   public String getBlockModel() {
      ResourceLocation registerName = ForgeRegistries.BLOCKS.getKey(this.tile.blockModel);
      return registerName != null ? registerName.toString() : "minecraft:air";
   }

   public ITimers getTimers() {
      return this.tile.timers;
   }

   public float getHardness() {
      return this.tile.blockHardness;
   }

   public void setHardness(float hardness) {
      this.tile.blockHardness = hardness;
   }

   public float getResistance() {
      return this.tile.blockResistance;
   }

   public void setResistance(float resistance) {
      this.tile.blockResistance = resistance;
   }

   protected void setTile(BlockEntity tile) {
      this.tile = (TileScriptedDoor)tile;
      super.setTile(tile);
   }

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
