package noppes.npcs.api.wrapper;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.registries.ForgeRegistries;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.IContainer;
import noppes.npcs.api.INbt;
import noppes.npcs.api.IPos;
import noppes.npcs.api.IWorld;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.block.IBlock;
import noppes.npcs.api.entity.data.IData;
import noppes.npcs.api.wrapper.data.Data;
import noppes.npcs.blocks.BlockScripted;
import noppes.npcs.blocks.BlockScriptedDoor;
import noppes.npcs.blocks.tiles.TileNpcEntity;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.mixin.world.entity.IEntityMixin;
import noppes.npcs.shared.common.util.LRUHashMap;

public class BlockWrapper implements IBlock {

   /*
    * Used in:
    * A large number of Forge events
    * When checking vision when an NPC is looking at a target
    * Mod events and scripts
    */
   public static volatile ConcurrentHashMap<Long, BlockWrapper> blockCache = new ConcurrentHashMap<>(2500);

   protected final IWorld level;
   protected final Block block;
   protected final BlockPos pos;
   protected final BlockPosWrapper iPos;
   protected BlockEntity tile;
   protected TileNpcEntity storage;

   private IData storeddata = new Data();
   private IData tempdata = new Data();

   protected BlockWrapper(Level levelIn, Block blockIn, BlockPos posIn) {
      level = Objects.requireNonNull(NpcAPI.Instance()).getIWorld(levelIn);
      block = blockIn;
      pos = posIn;
      iPos = new BlockPosWrapper(posIn);
      setTile(levelIn.getBlockEntity(posIn));
   }

   public static void checkClearCache() {
      if (blockCache.size() > 2500) {
         blockCache.keySet().stream()
                 .limit(blockCache.size() - 2500)
                 .forEach(blockCache::remove);
      }
   }
    public int getX() {
      return this.pos.getX();
   }

   public int getY() {
      return this.pos.getY();
   }

   public int getZ() {
      return this.pos.getZ();
   }

   public IPos getPos() {
      return this.iPos;
   }

   public Object getProperty(String name) {
      BlockState state = this.getMCBlockState();
      for (Property<?> p : state.getProperties()) {
         if (p.getName().equalsIgnoreCase(name)) {
            return state.getValue(p);
         }
      }
      throw new CustomNPCsException("Unknown property: " + name);
   }

   public void setProperty(String name, Object value) {
      if (!(value instanceof Comparable)) {
         throw new CustomNPCsException("Not a valid property value: " + value);
      }
      BlockState state = this.getMCBlockState();
      for (Property<?> p : state.getProperties()) {
         if (p.getName().equalsIgnoreCase(name)) {
            setPropertyValue(state, p, (Comparable<?>) value);
            return;
         }
      }
      throw new CustomNPCsException("Unknown property: " + name);
   }

   private <T extends Comparable<T>> void setPropertyValue(BlockState state, Property<T> p, Comparable<?> c) {
      this.level.getMCLevel().setBlock(this.pos, state.setValue(p, p.getValueClass().cast(c)), 3);
   }

   public String[] getProperties() {
      Collection<Property<?>> props = this.getMCBlockState().getProperties();
      List<String> list = new ArrayList<>();
      for (Property<?> prop : props) {
         list.add(prop.getName());
      }
      return list.toArray(new String[0]);
   }

   public void remove() {
      this.level.getMCLevel().removeBlock(this.pos, false);
   }

   public boolean isRemoved() {
      BlockState state = this.level.getMCLevel().getBlockState(this.pos);
      if (state == null) {
         return true;
      } else {
         return state.getBlock() != this.block;
      }
   }

   public boolean isAir() {
      return this.level.getMCLevel().getBlockState(this.pos).isAir();
   }

   public BlockWrapper setBlock(String name) {
      Block block = ForgeRegistries.BLOCKS.getValue(ResourceLocation.tryParse(name));
      if (block == null) {
         return this;
      } else {
         this.level.getMCLevel().setBlock(this.pos, block.defaultBlockState(), 2);
         return new BlockWrapper(this.level.getMCLevel(), block, this.pos);
      }
   }

   public BlockWrapper setBlock(IBlock block) {
      this.level.getMCLevel().setBlock(this.pos, block.getMCBlock().defaultBlockState(), 2);
      return new BlockWrapper(this.level.getMCLevel(), block.getMCBlock(), this.pos);
   }

   public boolean isContainer() {
      if (this.tile != null && this.tile instanceof Container) {
         return ((Container)this.tile).getContainerSize() > 0;
      } else {
         return false;
      }
   }

   public IContainer getContainer() {
      if (!this.isContainer()) {
         throw new CustomNPCsException("This block is not a container");
      } else {
         return Objects.requireNonNull(NpcAPI.Instance()).getIContainer((Container)this.tile);
      }
   }

   public IData getTempdata() {
      return this.tempdata;
   }

   public IData getStoreddata() {
      return this.storeddata;
   }

   public String getName() {
      return Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(this.block)).toString();
   }

   public String getDisplayName() {
      return this.tile != null && this.tile instanceof Nameable ? ((Nameable)this.tile).getDisplayName().getString() : this.getName();
   }

   public IWorld getWorld() {
      return this.level;
   }

   public Block getMCBlock() {
      return this.block;
   }

   public static IBlock createNew(Level level, BlockPos pos, BlockState state) {
      CustomNpcs.debugData.start(BlockWrapper.class);
      Long key = makeKey(level, state, pos);
      BlockWrapper wrapper = blockCache.get(key);
      if (wrapper == null) {
         wrapper = createBlockWrapper(level, state, pos);
         blockCache.put(key, wrapper);
      }
      CustomNpcs.debugData.end(BlockWrapper.class);
      return wrapper;
   }

   private static Long makeKey(Level level, BlockState state, BlockPos pos) {
       return pos.asLong() << 32 | (level == null ? 0 : level.dimension().hashCode()) | state.getBlock().hashCode();
   }

   private static BlockWrapper createBlockWrapper(Level level, BlockState state, BlockPos pos) {
      Block block = state.getBlock();
      BlockWrapper wrapper;
      if (block instanceof BlockScripted) { wrapper = new BlockScriptedWrapper(level, block, pos); }
      else if (block instanceof BlockScriptedDoor) { wrapper = new BlockScriptedDoorWrapper(level, block, pos); }
      else if (block instanceof IFluidBlock) { wrapper = new BlockFluidContainerWrapper(level, block, pos); }
      else { wrapper = new BlockWrapper(level, block, pos); }
      wrapper.setTile(level.getBlockEntity(pos));
      return wrapper;
   }

   public static void clearCache() {
      blockCache.clear();
   }

   public boolean hasTileEntity() {
      return this.tile != null;
   }

   protected void setTile(BlockEntity tile) {
      this.tile = tile;
      if (tile instanceof TileNpcEntity) {
         this.storage = (TileNpcEntity)tile;
         tempdata = storage.tempData;
         storeddata = storage.storedData;
      }
   }

   public INbt getBlockEntityNBT() {
      CompoundTag compound = this.tile.saveWithoutMetadata();
      return new NBTWrapper(compound);
   }

   public void setTileEntityNBT(INbt nbt) {
      this.tile.load(nbt.getMCNBT());
      this.tile.setChanged();
      BlockState state = this.level.getMCLevel().getBlockState(this.pos);
      this.level.getMCLevel().sendBlockUpdated(this.pos, state, state, 3);
   }

   public BlockEntity getMCTileEntity() {
      return this.tile;
   }

   public BlockState getMCBlockState() {
      return this.level.getMCLevel().getBlockState(this.pos);
   }

   public void blockEvent(int type, int data) {
      this.level.getMCLevel().blockEvent(this.pos, this.getMCBlock(), type, data);
   }

   public void interact(int side) {
      Player player = EntityNPCInterface.GenericPlayer;
      Level w = this.level.getMCLevel();
      ((IEntityMixin) player).setLevel(w);
      player.setPos(this.pos.getX(), this.pos.getY(), this.pos.getZ());
      this.getMCBlockState().use(w, EntityNPCInterface.CommandPlayer, InteractionHand.MAIN_HAND, new BlockHitResult(Vec3.ZERO, Direction.from3DDataValue(side), this.pos, true));
   }

   public TileNpcEntity getStorage() { return storage; }

   public BlockEntity getTile() { return tile; }

}
