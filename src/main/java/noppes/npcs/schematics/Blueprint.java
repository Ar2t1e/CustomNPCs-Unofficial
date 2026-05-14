package noppes.npcs.schematics;

import java.util.List;
import java.util.Objects;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import noppes.npcs.api.IPos;
import noppes.npcs.api.NpcAPI;

public class Blueprint implements ISchematic {

   private final List<String> requiredMods;
   private final short sizeX;
   private final short sizeY;
   private final short sizeZ;
   private final short paletteSize;
   private final BlockState[] palette;
   private String name;
   private String[] architects;
   private final short[][][] structure;
   private final CompoundTag[] tileEntities;

   // New from Unofficial (BetaZavr)
   private final BlockPos offset = BlockPos.ZERO;

   public Blueprint(short sizeXIn, short sizeYIn, short sizeZIn, short paletteSizeIn, BlockState[] paletteIn,
                    short[][][] structureIn, CompoundTag[] tileEntitiesIn, List<String> requiredModsIn) {
      sizeX = sizeXIn;
      sizeY = sizeYIn;
      sizeZ = sizeZIn;
      paletteSize = paletteSizeIn;
      palette = paletteIn;
      structure = structureIn;
      tileEntities = tileEntitiesIn;
      requiredMods = requiredModsIn;
   }

   public void build(Level level, BlockPos pos) {
      BlockState[] palette = getPalette();
      short[][][] structure = getStructure();
      short y;
      short z;
      short x;
      BlockState state;
      for(y = 0; y < getSizeY(); ++y) {
         for(z = 0; z < getSizeZ(); ++z) {
            for(x = 0; x < getSizeX(); ++x) {
               state = palette[structure[y][z][x] & '\uffff'];
               if (state.getBlock() != Blocks.STRUCTURE_VOID && state.isCollisionShapeFullBlock(EmptyBlockGetter.INSTANCE, BlockPos.ZERO)) {
                  level.setBlock(pos.offset(x, y, z).offset(offset), state, 2);
               }
            }
         }
      }
      for(y = 0; y < getSizeY(); ++y) {
         for(z = 0; z < getSizeZ(); ++z) {
            for(x = 0; x < getSizeX(); ++x) {
               state = palette[structure[y][z][x]];
               if (state.getBlock() != Blocks.STRUCTURE_VOID && !state.isCollisionShapeFullBlock(EmptyBlockGetter.INSTANCE, BlockPos.ZERO)) {
                  level.setBlock(pos.offset(x, y, z).offset(offset), state, 2);
               }
            }
         }
      }
      if (getTileEntities() != null) {
         CompoundTag[] var10 = getTileEntities();
         for (CompoundTag tag : var10) {
            BlockEntity te = level.getBlockEntity(pos.offset(tag.getShort("x"), tag.getShort("y"), tag.getShort("z")));
            tag.putInt("x", pos.getX() + tag.getShort("x"));
            tag.putInt("y", pos.getY() + tag.getShort("y"));
            tag.putInt("z", pos.getZ() + tag.getShort("z"));
            if (te != null) { te.deserializeNBT(tag); }
         }
      }

   }

   public short getSizeX() { return sizeX; }

   public short getSizeY() { return sizeY; }

   public short getSizeZ() { return sizeZ; }

   public short getPaletteSize() { return paletteSize; }

   public BlockState[] getPalette() { return palette; }

   public short[][][] getStructure() { return structure; }

   public CompoundTag[] getTileEntities() { return tileEntities; }

   public List<String> getRequiredMods() { return requiredMods; }

   @Override
   public String getName() { return name; }

   public void setName(String nameIn) { name = nameIn; }

   public String[] getArchitects() { return architects; }

   public void setArchitects(String[] architectsIn) { architects = architectsIn; }

   @Override
   public short getWidth() { return this.getSizeX(); }

   @Override
   public short getHeight() { return getSizeZ(); }

   @Override
   public short getLength() { return getSizeY(); }

   @Override
   public int getBlockEntityDimensions() { return tileEntities.length; }

   @Override
   public CompoundTag getBlockEntity(int i) { return tileEntities[i]; }

   @Override
   public BlockState getBlockState(int x, int y, int z) { return palette[structure[y][z][x]]; }

   @Override
   public BlockState getBlockState(int i) {
      int x = i % getWidth();
      int z = (i - x) / getWidth() % getLength();
      int y = ((i - x) / getWidth() - z) / getLength();
      return getBlockState(x, y, z);
   }

   @Override
   public CompoundTag getNBT() { return BlueprintUtil.writeBlueprintToNBT(this); }

   @Override
   public IPos getOffset() { return Objects.requireNonNull(NpcAPI.Instance()).getIPos(offset.getX(), offset.getY(), offset.getZ()); }

   @Override
   public boolean hasEntitys() { return false; }

   @Override
   public ListTag getEntitys() { return null; }

}
