package noppes.npcs.api.wrapper;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.registries.ForgeRegistries;
import noppes.npcs.EventHooks;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.ILayerBlockModel;
import noppes.npcs.api.ITimers;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.block.IBlock;
import noppes.npcs.api.block.IBlockScripted;
import noppes.npcs.api.block.ITextPlane;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.blocks.BlockScripted;
import noppes.npcs.blocks.tiles.TileScripted;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.mixin.world.entity.IEntityMixin;

import java.util.*;

public class BlockScriptedWrapper
        extends BlockWrapper
        implements IBlockScripted {

   private TileScripted tile;

   public BlockScriptedWrapper(Level level, BlockState state, BlockPos pos) {
      super(level, state, pos);
      tile = (TileScripted) super.tile;
   }

   @Override
   public void setModel(IItemStack item) { tile.getMainModel().setItemModel(item); }

   @Override
   public void setModel(IBlock block) { tile.getMainModel().setBlockModel(block); }

   @Override
   public void setModel(String name) {
      ILayerBlockModel lbm = tile.getMainModel();
      if (name == null) { lbm.setItemModel(null); }
      else {
         ResourceLocation loc = ResourceLocation.tryParse(NoppesUtilServer.validLocation(name));
         if (loc == null) { lbm.setItemModel(null); }
         else {
            Block block = ForgeRegistries.BLOCKS.getValue(loc);
            if (block != null) { lbm.setBlockModel(BlockWrapper.createNew(tile.getLevel(), tile.getBlockPos(), block.defaultBlockState())); }
            else {
               Item item = ForgeRegistries.ITEMS.getValue(loc);
               if (item != null) { lbm.setItemModel(Objects.requireNonNull(NpcAPI.Instance()).getIItemStack(new ItemStack(item))); }
               else { lbm.setOBJModel(loc.toString()); }
            }
         }
      }
   }

   @Override
   public void setObjModel(String path) { tile.getMainModel().setOBJModel(path); }

   @Override
   public void setObjModel(String path, String[] objVisibleMeshes, String[][] objMaterialsReplase) {
      List<String > ovm = new ArrayList<>();
      Map<String, ResourceLocation> omr = new HashMap<>();
      if (objVisibleMeshes != null) { ovm.addAll(List.of(objVisibleMeshes)); }
      if (objMaterialsReplase != null) {
          for (String[] strings : objMaterialsReplase) {
              if (strings != null && strings.length > 1 && strings[0] != null && strings[1] != null) {
                  omr.put(strings[0], new ResourceLocation(NoppesUtilServer.validLocation(strings[1])));
              }
          }
      }
      setObjModel(path, ovm, omr);
   }

   public void setObjModel(String path, List<String> objVisibleMeshes, Map<String, ResourceLocation> objMaterialsReplase) {
      tile.getMainModel().setOBJModel(path, objVisibleMeshes, objMaterialsReplase);
   }

   @Override
   public IItemStack getModel() { return tile.getMainModel().getItemModel(); }

   @Override
   public ILayerBlockModel getModel(int id) {
      List<ILayerBlockModel> layers = tile.getLayers();
      if (id < 0 || id >= layers.size()) {
         throw new CustomNPCsException("Layer ID must be greater than 0 and no more than " + (layers.size() - 1));
      }
      return layers.get(id);
   }

   @Override
   public ILayerBlockModel createLayerModel() { return tile.createLayerModel(); }

   @Override
   public List<ILayerBlockModel> getLayerModels() { return tile.getLayers(); }

   @Override
   public boolean removeLayerModel(ILayerBlockModel layer) { return tile.removeLayerModel(layer); }

   @Override
   public boolean removeLayerModel(int id) {
      ILayerBlockModel ibm = getModel(id);
      return ibm != null && tile.removeLayerModel(ibm);
   }

   @Override
   public void setRedstonePower(int strength) { tile.setRedstonePower(strength); }

   @Override
   public int getRedstonePower() { return tile.powering; }

   @Override
   public void setIsLadder(boolean bo) {
      tile.isLadder = bo;
      tile.needsClientUpdate = true;
   }

   @Override
   public boolean getIsLadder() { return tile.isLadder; }

   @Override
   public void setIsPassible(boolean bo) {
      tile.isPassable = bo;
      tile.needsClientUpdate = true;
   }

   @Override
   public boolean getIsPassible() { return tile.isPassable; }

   @Override
   public void setLight(int value) { tile.setLightValue(value); }

   @Override
   public int getLight() { return tile.lightValue; }

   @Override
   public void setScale(float x, float y, float z) {
      ILayerBlockModel lbm = tile.getMainModel();
      if (lbm.getScale(0) != x || lbm.getScale(1) != y || lbm.getScale(2) != z) { lbm.setScale(x, y, z); }
   }

   @Override
   public float getScaleX() { return tile.getMainModel().getScale(0); }

   @Override
   public float getScaleY() { return tile.getMainModel().getScale(1); }

   @Override
   public float getScaleZ() { return tile.getMainModel().getScale(2); }

   @Override
   public void setRotation(int x, int y, int z) {
      ILayerBlockModel lbm = tile.getMainModel();
      if (lbm.getRotation(0) != x || lbm.getRotation(1) != y || lbm.getRotation(2) != z) { lbm.setRotation(x, y, z); }
   }

   @Override
   public float getRotationX() { return tile.getMainModel().getRotation(0); }

   @Override
   public float getRotationY() { return tile.getMainModel().getRotation(1); }

   @Override
   public float getRotationZ() { return tile.getMainModel().getRotation(2); }

   @Override
   public float getHardness() { return tile.blockHardness; }

   @Override
   public void setHardness(float hardness) { tile.blockHardness = hardness; }

   @Override
   public float getResistance() { return tile.blockResistance; }

   @Override
   public void setResistance(float resistance) { tile.blockResistance = resistance; }

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

   @Override
   public ITextPlane getTextPlane() { return tile.getTextPlanes().get(0); }

   @Override
   public ITextPlane getTextPlane2() { return tile.getTextPlanes().get(1); }

   @Override
   public ITextPlane getTextPlane3() { return tile.getTextPlanes().get(2); }

   @Override
   public ITextPlane getTextPlane4() { return tile.getTextPlanes().get(3); }

   @Override
   public ITextPlane getTextPlane5() { return tile.getTextPlanes().get(4); }

   @Override
   public ITextPlane getTextPlane6() { return tile.getTextPlanes().get(5); }

   @Override
   public ITextPlane getTextPlane(int id) {
      List<ITextPlane> textPlanes = tile.getTextPlanes();
      if (id < 0 || id >= textPlanes.size()) {
         throw new CustomNPCsException("Layer ID must be greater than 0 and no more than " + (textPlanes.size() - 1));
      }
      return textPlanes.get(id);
   }

   @Override
   public ITimers getTimers() { return tile.timers; }

   @Override
   protected void setTile(BlockEntity tileIn) {
      if (tileIn instanceof TileScripted) {
         tile = (TileScripted) tileIn;
         super.setTile(tile);
      }
   }

   @Override
   public void trigger(int id, Object... arguments) {
      EventHooks.onScriptTriggerEvent(tile, id, level, getPos(), null, arguments);
   }

   @Override
   public void updateModel() { tile.needsClientUpdate = true; }

   // New Unofficial (Goodbird)
   @Override
   public void setIsWaterlogged(boolean bo) {
      if (level != null) {
         BlockState newState = level.getMCLevel().getBlockState(iPos.blockPos).setValue(BlockScripted.WATERLOGGED, bo);
         level.getMCLevel().setBlock(iPos.blockPos, newState, 3);
      }
   }

   @Override
   public boolean getIsWaterlogged() {
      return level != null && level.getMCLevel().getBlockState(iPos.blockPos).getValue(BlockScripted.WATERLOGGED);
   }

}
