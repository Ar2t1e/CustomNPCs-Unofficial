package noppes.npcs.schematics;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import noppes.npcs.shared.common.CommonUtil;

public class BlueprintUtil {

   public static CompoundTag writeBlueprintToNBT(Blueprint schem) {

      CompoundTag compound = new CompoundTag();
      compound.putString("Name", schem.getName());
      compound.putByte("version", (byte)1);
      compound.putShort("size_x", schem.getSizeX());
      compound.putShort("size_y", schem.getSizeY());
      compound.putShort("size_z", schem.getSizeZ());
      BlockState[] palette = schem.getPalette();
      ListTag paletteTag = new ListTag();

      for(short i = 0; i < schem.getPaletteSize(); ++i) {
         paletteTag.add(NbtUtils.writeBlockState(palette[i]));
      }

      compound.put("palette", paletteTag);
      int[] blockInt = convertBlocksToSaveData(schem.getStructure(), schem.getSizeX(), schem.getSizeY(), schem.getSizeZ());
      compound.putIntArray("blocks", blockInt);
      ListTag finishedTes = new ListTag();
      CompoundTag[] tes = schem.getTileEntities();
      Collections.addAll(finishedTes, tes);

      compound.put("tile_entities", finishedTes);
      List<String> requiredMods = schem.getRequiredMods();
      ListTag modsList = new ListTag();
      for (String requiredMod : requiredMods) {
         modsList.add(StringTag.valueOf(requiredMod));
      }

      compound.put("required_mods", modsList);
      String name = schem.getName();
      String[] architects = schem.getArchitects();
      if (name != null) {
         compound.putString("name", name);
         compound.putString("Name", name);
      }

      if (architects != null) {
         ListTag architectsTag = new ListTag();
         for (String architect : architects) {
            architectsTag.add(StringTag.valueOf(architect));
         }
         compound.put("architects", architectsTag);
      }

      return compound;
   }

   public static Blueprint readBlueprintFromNBT(CompoundTag tag) {
      byte version = tag.getByte("version");
      if (version != 1) {
         return null;
      } else {
         short sizeX = tag.getShort("size_x");
         short sizeY = tag.getShort("size_y");
         short sizeZ = tag.getShort("size_z");
         List<String> requiredMods = new ArrayList<>();
         ListTag modsList = tag.getList("required_mods", 8);
         short modListSize = (short)modsList.size();

         for(int i = 0; i < modListSize; ++i) {
            requiredMods.add(modsList.get(i).getAsString());
            if (!ModList.get().isLoaded(requiredMods.get(i))) {
               Logger var10000 = Logger.getGlobal();
               Level var10001 = Level.WARNING;
               String var10002 = requiredMods.get(i);
               var10000.log(var10001, "Couldn't load Blueprint, the following mod is missing: " + var10002);
               return null;
            }
         }

         ListTag paletteTag = tag.getList("palette", 10);
         short paletteSize = (short)paletteTag.size();
         BlockState[] palette = new BlockState[paletteSize];

         for(short i = 0; i < palette.length; ++i) {
            palette[i] = NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), paletteTag.getCompound(i));
         }

         short[][][] blocks = convertSaveDataToBlocks(tag.getIntArray("blocks"), sizeX, sizeY, sizeZ);
         ListTag teTag = tag.getList("tile_entities", 10);
         CompoundTag[] tileEntities = new CompoundTag[teTag.size()];

         for(short i = 0; i < tileEntities.length; ++i) {
            tileEntities[i] = teTag.getCompound(i);
         }

         Blueprint schem = new Blueprint(sizeX, sizeY, sizeZ, paletteSize, palette, blocks, tileEntities, requiredMods);
         if (tag.contains("name", 8)) { schem.setName(tag.getString("name")); }
         if (tag.contains("Name", 8)) { schem.setName(tag.getString("Name")); }

         if (tag.contains("architects")) {
            ListTag architectsTag = tag.getList("architects", 8);
            String[] architects = new String[architectsTag.size()];

            for(int i = 0; i < architectsTag.size(); ++i) {
               architects[i] = architectsTag.getString(i);
            }

            schem.setArchitects(architects);
         }

         return schem;
      }
   }

   private static int[] convertBlocksToSaveData(short[][][] multDimArray, short sizeX, short sizeY, short sizeZ) {
      short[] oneDimArray = new short[sizeX * sizeY * sizeZ];
      int j = 0;
      short z;
      for(short y = 0; y < sizeY; ++y) {
         for(z = 0; z < sizeZ; ++z) {
            for(short x = 0; x < sizeX; ++x) {
               oneDimArray[j++] = multDimArray[y][z][x];
            }
         }
      }

      int[] ints = new int[(int)Math.ceil((float)oneDimArray.length / 2.0F)];
      int currentInt;
      for(int i = 1; i < oneDimArray.length; i += 2) {
         z = oneDimArray[i - 1];
         currentInt = z << 16 | oneDimArray[i];
         ints[(int)Math.ceil((float)i / 2.0F) - 1] = currentInt;
      }

      if (oneDimArray.length % 2 == 1) {
         currentInt = oneDimArray[oneDimArray.length - 1] << 16;
         ints[ints.length - 1] = currentInt;
      }
      return ints;
   }

   public static short[][][] convertSaveDataToBlocks(int[] ints, short sizeX, short sizeY, short sizeZ) {
      short[] oneDimArray = new short[ints.length * 2];

      for(int i = 0; i < ints.length; ++i) {
         oneDimArray[i * 2] = (short)(ints[i] >> 16);
         oneDimArray[i * 2 + 1] = (short)ints[i];
      }

      short[][][] multDimArray = new short[sizeY][sizeZ][sizeX];
      int i = 0;

      for(short y = 0; y < sizeY; ++y) {
         for(short z = 0; z < sizeZ; ++z) {
            for(short x = 0; x < sizeX; ++x) {
               multDimArray[y][z][x] = oneDimArray[i++];
            }
         }
      }

      return multDimArray;
   }

   // New from Unofficial (BetaZavr)
   public static Blueprint createBlueprint(net.minecraft.world.level.Level level, BlockPos pos, short sizeX, short sizeY, short sizeZ) {
      return createBlueprint(level, pos, sizeX, sizeY, sizeZ, null);
   }

   public static Blueprint createBlueprint(net.minecraft.world.level.Level level, BlockPos pos, short sizeX, short sizeY, short sizeZ,
                                           String name, String... architects) {
      CommonUtil.NotifyOPs("Creating blueprint at: " + pos + " might lag slightly");
      List<BlockState> palette = new ArrayList<>();
      short[][][] structure = new short[sizeY][sizeZ][sizeX];
      List<CompoundTag> tileEntities = new ArrayList<>();
      List<String> requiredMods = new ArrayList<>();
      for (short y = 0; y < sizeY; ++y) {
         for (short z = 0; z < sizeZ; ++z) {
            for (short x = 0; x < sizeX; ++x) {
               BlockState state = level.getBlockState(pos.offset(x, y, z));
               ResourceLocation regName = ForgeRegistries.BLOCKS.getKey(state.getBlock());
               if (regName != null) {
                  if (!requiredMods.contains(regName.getNamespace())) { requiredMods.add(regName.getNamespace()); }
               }
               BlockEntity te = level.getBlockEntity(pos.offset(x, y, z));
               if (te != null) {
                  CompoundTag teTag = te.serializeNBT();
                  teTag.putInt("x", x);
                  teTag.putInt("y", y);
                  teTag.putInt("z", z);
                  tileEntities.add(teTag);
               }
               if (!palette.contains(state)) { palette.add(state); }
               structure[y][z][x] = (short) palette.indexOf(state);
            }
         }
      }
      BlockState[] states = new BlockState[palette.size()];
      states = palette.toArray(states);
      CompoundTag[] tes = new CompoundTag[tileEntities.size()];
      tes = tileEntities.toArray(tes);
      Blueprint schem = new Blueprint(sizeX, sizeY, sizeZ, (byte) palette.size(), states, structure, tes, requiredMods);
      if (name != null) { schem.setName(name); }
      if (architects != null) { schem.setArchitects(architects); }
      return schem;
   }

}
