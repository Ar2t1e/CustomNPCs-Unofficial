package noppes.npcs.api.wrapper;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.registries.ForgeRegistries;
import noppes.npcs.EventHooks;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.ITimers;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.block.IBlockScripted;
import noppes.npcs.api.block.ITextPlane;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.blocks.BlockScripted;
import noppes.npcs.blocks.tiles.TileScripted;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.mixin.world.entity.IEntityMixin;

import java.util.Objects;

public class BlockScriptedWrapper
        extends BlockWrapper
        implements IBlockScripted {

   private TileScripted tile;

   public BlockScriptedWrapper(Level level, Block block, BlockPos pos) {
      super(level, block, pos);
      tile = (TileScripted) super.tile;
   }

   public void setModel(IItemStack item) {
      if (item == null) {
         tile.setItemModel(null, null);
      } else {
         Item itemMC = item.getMCItemStack().getItem();
         tile.setItemModel(item.getMCItemStack(), itemMC instanceof BlockItem ? ((BlockItem) itemMC).getBlock() : Blocks.AIR);
      }
   }

   public void setModel(String name) {
      if (name == null) {
         tile.setItemModel(null, null);
         return;
      }
      ResourceLocation loc = ResourceLocation.tryParse(name);
      if (loc == null) {
         tile.setItemModel(null, null);
         return;
      }
      Block block = ForgeRegistries.BLOCKS.getValue(loc);
      Item item = ForgeRegistries.ITEMS.getValue(loc);
      if (item == null) {
         tile.setItemModel(null, null);
         return;
      }
      tile.setItemModel(new ItemStack(item), block);
   }

   public IItemStack getModel() {
      return Objects.requireNonNull(NpcAPI.Instance()).getIItemStack(tile.itemModel);
   }

   public void setRedstonePower(int strength) {
      tile.setRedstonePower(strength);
   }

   public int getRedstonePower() {
      return tile.powering;
   }

   public void setIsLadder(boolean bo) {
      tile.isLadder = bo;
      tile.needsClientUpdate = true;
   }

   public boolean getIsLadder() {
      return tile.isLadder;
   }

   public void setIsPassible(boolean bo) {
      tile.isPassible = bo;
      tile.needsClientUpdate = true;
   }

   public boolean getIsPassible() {
      return tile.isPassible;
   }

   public void setLight(int value) {
      tile.setLightValue(value);
   }

   public int getLight() {
      return tile.lightValue;
   }

   public void setScale(float x, float y, float z) {
      tile.setScale(x, y, z);
   }

   public float getScaleX() {
      return tile.scaleX;
   }

   public float getScaleY() {
      return tile.scaleY;
   }

   public float getScaleZ() {
      return tile.scaleZ;
   }

   public void setRotation(int x, int y, int z) {
      tile.setRotation(x % 360, y % 360, z % 360);
   }

   public int getRotationX() {
      return tile.rotationX;
   }

   public int getRotationY() {
      return tile.rotationY;
   }

   public int getRotationZ() {
      return tile.rotationZ;
   }

   public float getHardness() {
      return tile.blockHardness;
   }

   public void setHardness(float hardness) {
      tile.blockHardness = hardness;
   }

   public float getResistance() {
      return tile.blockResistance;
   }

   public void setResistance(float resistance) {
      tile.blockResistance = resistance;
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

   public ITextPlane getTextPlane() {
      return tile.text1;
   }

   public ITextPlane getTextPlane2() {
      return tile.text2;
   }

   public ITextPlane getTextPlane3() {
      return tile.text3;
   }

   public ITextPlane getTextPlane4() {
      return tile.text4;
   }

   public ITextPlane getTextPlane5() {
      return tile.text5;
   }

   public ITextPlane getTextPlane6() {
      return tile.text6;
   }

   public ITimers getTimers() {
      return tile.timers;
   }

   protected void setTile(BlockEntity tileIn) {
      tile = (TileScripted) tileIn;
      super.setTile(tile);
   }

   public void trigger(int id, Object... arguments) {
      EventHooks.onScriptTriggerEvent(tile, id, level, getPos(), null, arguments);
   }

   // New Unofficial (Goodbird)
   public void setIsWaterlogged(boolean bo) {
      BlockState newState = level.getMCLevel().getBlockState(pos).setValue(BlockScripted.WATERLOGGED, bo);
      level.getMCLevel().setBlock(pos, newState, 3);
   }

   public boolean getIsWaterlogged() {
      return level.getMCLevel().getBlockState(pos).getValue(BlockScripted.WATERLOGGED);
   }

}
