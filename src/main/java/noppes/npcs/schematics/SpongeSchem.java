package noppes.npcs.schematics;

import com.mojang.serialization.Dynamic;
import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.*;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.registries.ForgeRegistries;
import noppes.npcs.CustomBlocks;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.IPos;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.shared.common.CommonUtil;

public class SpongeSchem implements ISchematic {

   public static final int latestDataVersion = 2586;
   public String name;
   public short width;
   public short height;
   public short length;
   public long timestamp = System.currentTimeMillis();
   public int[] data;
   public Map<Integer, BlockState> palette = new HashMap<>();
   public List<CompoundTag> tileData = new ArrayList<>();
   protected BlockPos offset = BlockPos.ZERO;

   // New from Unofficial (BetaZavr)
   public ListTag entityList = new ListTag();

   public SpongeSchem(String nameIn) { name = nameIn; }

   @Override
   public short getWidth() { return width; }

   @Override
   public short getHeight() { return height; }

   @Override
   public short getLength() { return length; }

   @Override
   public int getBlockEntityDimensions() { return tileData.size(); }

   @Override
   public CompoundTag getBlockEntity(int i) { return tileData.get(i); }

   @Override
   public String getName() { return name; }

   @Override
   public BlockState getBlockState(int x, int y, int z) { return getBlockState(xyzToIndex(x, y, z)); }

   public int xyzToIndex(int x, int y, int z) { return (y * length + z) * width + x; }

   @Override
   public BlockState getBlockState(int i) { return palette.get(data[i]); }

   @Override
   public CompoundTag getNBT() {
      CompoundTag root = new CompoundTag();
      CompoundTag compound = new CompoundTag();
      compound.putString("Name", name);
      root.put("", compound);
      CompoundTag schematicData = new CompoundTag();
      compound.put("Schematic", schematicData);
      schematicData.putInt("Width", width);
      schematicData.putInt("Height", height);
      schematicData.putInt("Length", length);
      schematicData.putInt("Version", 3);
      schematicData.putInt("DataVersion", latestDataVersion);
      CompoundTag metadata = new CompoundTag();
      metadata.putLong("Date", timestamp);
      schematicData.put("Metadata", metadata);
      CompoundTag blockData = new CompoundTag();
      ByteArrayOutputStream buffer = new ByteArrayOutputStream(data.length);
      for (int datum : data) {
         int blockId;
         for (blockId = datum; (blockId & -128) != 0; blockId >>>= 7) {
            buffer.write(blockId & 127 | 128);
         }
         buffer.write(blockId);
      }
      blockData.putByteArray("Data", buffer.toByteArray());
      CompoundTag paletteNBT = new CompoundTag();
      for (Entry<Integer, BlockState> en : palette.entrySet()) {
         paletteNBT.putInt(BlockStateParser.serialize(en.getValue()), en.getKey());
      }
      blockData.put("Palette", paletteNBT);
      ListTag tileNBT = new ListTag();
      for (CompoundTag tile : tileData) {
         tile = tile.copy();
         tile.putIntArray("Pos", new int[]{tile.getInt("x"), tile.getInt("y"), tile.getInt("z")});
         tile.putString("Id", tile.getString("id"));
         tile.remove("x");
         tile.remove("y");
         tile.remove("z");
         tile.remove("id");
         tileNBT.add(tile);
      }
      blockData.put("BlockEntities", tileNBT);
      schematicData.put("Blocks", blockData);

      compound.putLong("Offset", offset.asLong());
      return root;
   }

   @Override
   public IPos getOffset() {
      return Objects.requireNonNull(NpcAPI.Instance()).getIPos(offset.getX(), offset.getY(), offset.getZ());
   }

   @Override
   public boolean hasEntitys() {
      return false;
   }

   @Override
   public ListTag getEntitys() {
      return null;
   }

   public void load(CompoundTag compound) {
      if (compound.size() == 1) { compound = compound.getCompound("").getCompound("Schematic"); }
      if (compound.contains("Name", 8)) { name = compound.getString("Name"); }
      width = compound.getShort("Width");
      height = compound.getShort("Height");
      length = compound.getShort("Length");
      CompoundTag metadata = compound.getCompound("Metadata");
      timestamp = 0L;
      if (!metadata.isEmpty()) {
         timestamp = metadata.getLong("Date");
      }
      int dataVersion = 1631;
      if (compound.contains("DataVersion")) {
         dataVersion = compound.getInt("DataVersion");
         //if (dataVersion > latestDataVersion) {} if (dataVersion < latestDataVersion) {}
      }
      int version = compound.getInt("Version");
      if (version < 3) {
         palette = readPalette(compound.getCompound("Palette"), dataVersion);
         ListTag tileEntities = compound.getList("BlockEntities", 10);
         if (tileEntities.isEmpty()) {
            tileEntities = compound.getList("TileEntities", 10);
         }
         tileData = readTileData(tileEntities, dataVersion);
         data = readBlockData(compound.getByteArray("BlockData"));
      } else {
         CompoundTag blocks = compound.getCompound("Blocks");
         palette = readPalette(blocks.getCompound("Palette"), dataVersion);
         tileData = readTileData(blocks.getList("BlockEntities", 10), dataVersion);
         data = readBlockData(blocks.getByteArray("Data"));
      }
      offset = BlockPos.ZERO;
      if (compound.contains("Offset", 4)) {
         offset = BlockPos.of(compound.getLong("Offset"));
      }
   }

   private int[] readBlockData(byte[] bytes) {
      int[] data = new int[width * length * height];
      int index = 0;

      for(int i = 0; i < bytes.length; ++index) {
         int value = 0;
         int variantLength = 0;

         while(true) {
            value |= (bytes[i] & 127) << variantLength++ * 7;
            if (variantLength > 5) {
               throw new CustomNPCsException("VarInt too big (probably corrupted data)");
            }
            if ((bytes[i] & 128) != 128) {
               ++i;
               data[index] = value;
               break;
            }

            ++i;
         }
      }

      return data;
   }

   private Map<Integer, BlockState> readPalette(CompoundTag comp, int dataVersion) {
      Map<String, Integer> map = new HashMap<>();
      Map<Integer, BlockState> palette = new HashMap<>();

      Iterator<?> var5;
      String blockState;
      int id;
      for(var5 = comp.getAllKeys().iterator(); var5.hasNext(); map.put(blockState, id)) {
         blockState = (String)var5.next();
         id = comp.getInt(blockState);
         if (dataVersion < latestDataVersion) {
            CompoundTag stateNBT = stateToNBT(blockState);
            Dynamic<Tag> dynamic = new Dynamic<>(NbtOps.INSTANCE, stateNBT);
            stateNBT = (CompoundTag)DataFixers.getDataFixer().update(References.BLOCK_STATE, dynamic, dataVersion, latestDataVersion).getValue();
            blockState = nbtToState(stateNBT);
         }
      }

      var5 = ForgeRegistries.BLOCKS.iterator();

      while(var5.hasNext()) {
         Block block = (Block)var5.next();
         block.getStateDefinition().getPossibleStates().forEach((state) -> {
            String name = BlockStateParser.serialize(state);
            if (map.containsKey(name)) {
               int remId = map.remove(name);
               palette.put(remId, state);
            }
         });
      }

      var5 = map.values().iterator();

      while(var5.hasNext()) {
         palette.put((Integer) var5.next(), Blocks.AIR.defaultBlockState());
      }

      return palette;
   }

   private List<CompoundTag> readTileData(ListTag list, int dataVersion) {
      List<CompoundTag> tileData = new ArrayList<>();
       if (!list.isEmpty()) {
           for (int i = 0; i < list.size(); ++i) {
               CompoundTag data = list.getCompound(i);
               int[] posArr = data.getIntArray("Pos");
               BlockPos pos = new BlockPos(posArr[0], posArr[1], posArr[2]);
               data.putInt("x", pos.getX());
               data.putInt("y", pos.getY());
               data.putInt("z", pos.getZ());
               data.put("id", Objects.requireNonNull(data.get("Id")));
               data.remove("Id");
               data.remove("Pos");
               if (dataVersion < latestDataVersion) {
                   Dynamic<Tag> dynamic = new Dynamic<>(NbtOps.INSTANCE, data);
                   data = (CompoundTag) DataFixers.getDataFixer().update(References.BLOCK_ENTITY, dynamic, dataVersion, latestDataVersion).getValue();
               } else {
                   data = data.copy();
               }
               tileData.add(data);
           }
       }
       return tileData;
   }

   private String nbtToState(CompoundTag tagCompound) {
      StringBuilder sb = new StringBuilder();
      sb.append(tagCompound.getString("Name"));
      if (tagCompound.contains("Properties", 10)) {
         sb.append('[');
         CompoundTag props = tagCompound.getCompound("Properties");
         sb.append(props.getAllKeys()
                 .stream().map((k) -> k + "=" + props.getString(k)
                 .replace("\"", ""))
                 .collect(Collectors.joining(",")));
         sb.append(']');
      }
      return sb.toString();
   }

   private static CompoundTag stateToNBT(String blockState) {
      int propIdx = blockState.indexOf(91);
      CompoundTag tag = new CompoundTag();
      if (propIdx < 0) {
         tag.putString("Name", blockState);
      } else {
         tag.putString("Name", blockState.substring(0, propIdx));
         CompoundTag propTag = new CompoundTag();
         String props = blockState.substring(propIdx + 1, blockState.length() - 1);
         for (String pair : props.split(",")) {
            String[] split = pair.split("=");
            propTag.putString(split[0], split[1]);
         }
         tag.put("Properties", propTag);
      }
      return tag;
   }

   public static SpongeSchem create(Level level, String name, BlockPos pos, short height, short width, short length) {
      SpongeSchem schema = new SpongeSchem(name);
      schema.height = height;
      schema.width = width;
      schema.length = length;
      int size = height * width * length;
      CommonUtil.NotifyOPs("Creating schematic at: " + pos + " might lag slightly");
      Map<String, Integer> map = new HashMap<>();
      schema.data = new int[size];
      int uniqueBlockId = 0;
      for(int i = 0; i < size; ++i) {
         int x = i % width;
         int z = (i - x) / width % length;
         int y = ((i - x) / width - z) / length;
         BlockState state = level.getBlockState(pos.offset(x, y, z));
         String stateName = BlockStateParser.serialize(state);
         Integer blockId = map.get(stateName);
         if (!map.containsKey(stateName)) {
            map.put(stateName, blockId = uniqueBlockId++);
         }
         schema.palette.put(blockId, state);
         schema.data[i] = blockId;
         if (state.getBlock() instanceof EntityBlock) {
            BlockEntity tile = level.getBlockEntity(pos.offset(x, y, z));
            CompoundTag compound = new CompoundTag();
            if (tile != null) { compound = tile.saveWithFullMetadata(); }
            compound.putInt("x", x);
            compound.putInt("y", y);
            compound.putInt("z", z);
            schema.tileData.add(compound);
         }
      }
      return schema;
   }

   public static SpongeSchem create(Level level, Direction fase, String name, Map<Integer, BlockPos> schMap) {
      CommonUtil.NotifyOPs("Generating the \"" + name + "\" schema may be a little late.");
      BlockPos p = schMap.get(0); // offset
      BlockPos m = schMap.get(1); // min
      BlockPos n = schMap.get(2); // max
      AABB bb = new AABB(m, n);
      short height = (short) (Math.abs(bb.maxY - bb.minY) + 1);
      short width = (short) (Math.abs(bb.maxX - bb.minX) + 1);
      short length = (short) (Math.abs(bb.maxZ - bb.minZ) + 1);
      BlockPos pos = new BlockPos((int) Math.floor(bb.minX), (int) Math.floor(bb.minY), (int) Math.floor(bb.minZ));

      SpongeSchem schema = new SpongeSchem(name);
      schema.height = height;
      schema.width = (fase == Direction.EAST || fase == Direction.WEST) ? length : width;
      schema.length = (fase == Direction.EAST || fase == Direction.WEST) ? width : length;
      int size = height * width * length;
      schema.data = new int[size];
      int rot = switch (fase) {
         case EAST -> 1;
         case NORTH -> 2;
         case WEST -> 3;
         default -> 0;
      };
      Map<String, Integer> map = new HashMap<>();
      int uniqueBlockId = 0;
      for (int i = 0; i < size; ++i) {
         int x, z;
         int y = i / (width * length);
         switch (fase) {
            case EAST: {
               x = i / length - y * width;
               z = length - 1 - i % length;
               break;
            }
            case NORTH: {
               x = width - 1 - i % width;
               z = length - 1 - (i / width) % length;
               break;
            }
            case WEST: {
               x = width - 1 - (i / length) % width;
               z = i % length;
               break;
            }
            default: { // SOUTH
               x = i % width;
               z = (i - x) / width % length;
               break;
            }
         }
         BlockState state = SchematicWrapper.rotationState(level.getBlockState(pos.offset(x, y, z)), rot);
         if (state.getBlock() != CustomBlocks.copy) {
            String stateName = BlockStateParser.serialize(state);
            Integer blockId = map.get(stateName);
            if (!map.containsKey(stateName)) {
               map.put(stateName, blockId = uniqueBlockId++);
            }
            schema.palette.put(blockId, state);
            schema.data[i] = blockId;
            if (state.getBlock() instanceof EntityBlock) {
               BlockEntity tile = level.getBlockEntity(pos.offset(x, y, z));
               CompoundTag compound = new CompoundTag();
               if (tile != null) { compound = tile.saveWithFullMetadata(); }
               compound.putInt("x", x);
               compound.putInt("y", y);
               compound.putInt("z", z);
               schema.tileData.add(compound);
            }
         }
      }
      // Added by mod
      schema.offset = new BlockPos((int) Math.floor(bb.minX - p.getX()), 1 + (int) Math.floor(bb.minY - p.getY()), (int) Math.floor(bb.minZ - p.getZ()));
      switch (fase) {
         case EAST: {
            schema.offset = new BlockPos((int) Math.floor(p.getZ() - bb.maxZ), (int) Math.floor(bb.minY - p.getY()), (int) Math.floor(bb.minX - p.getX()));
            break;
         }
         case NORTH: {
            schema.offset = new BlockPos((int) Math.floor(p.getX() - bb.maxX), (int) Math.floor(bb.minY - p.getY()), (int) Math.floor(p.getZ() - bb.maxZ));
            break;
         }
         case WEST: {
            schema.offset = new BlockPos((int) Math.floor(bb.minZ - p.getZ()), (int) Math.floor(bb.minY - p.getY()), (int) Math.floor(p.getX() - bb.maxX));
            break;
         }
         default: { // SOUTH
            schema.offset = new BlockPos((int) Math.floor(bb.minX - p.getX()), (int) Math.floor(bb.minY - p.getY()), (int) Math.floor(bb.minZ - p.getZ()));
            break;
         }
      }
      // Get Entitys:
      try {
         for (Entity e : level.getEntitiesOfClass(Entity.class,
                 new AABB(bb.minX - 0.25d, bb.minY - 0.25d, bb.minZ - 0.25d,
                         bb.maxX + 0.25d, bb.maxY + 0.25d, bb.maxZ + 0.25d),
                 (entity) -> !(entity instanceof Projectile || entity instanceof Arrow || entity instanceof Player))) {
            CompoundTag nbtEntity = e.serializeNBT();
            if (!nbtEntity.contains("UUID", 8)) { nbtEntity.putString("UUID", e.getStringUUID()); }
            ListTag posList = new ListTag();
            double[] d = new double[] { e.getX() - p.getX() - 1.0d, e.getY() - p.getY(), e.getZ() - p.getZ() - 1.0d };
            double[] ed = new double[] { d[0], d[1], d[2] };
            if (e instanceof HangingEntity) {
               d = new double[] { e.getX() - p.getX(), e.getY() - 1 - p.getY(), e.getZ() - p.getZ() };
               ed = new double[] { d[0], d[1], d[2] };
               float er = nbtEntity.getList("Rotation", 5).getFloat(0);
               byte f = nbtEntity.getByte("Facing");
               switch (rot) {
                  case 1:
                     f += 1;
                     er += 90.0f;
                     ed[0] = d[2];
                     ed[2] = d[0];
                     break;
                  case 2:
                     f += 2;
                     er += 180.0f;
                     ed[0] *= -1.0d;
                     ed[2] *= -1.0d;
                     break;
                  case 3:
                     f += 3;
                     er += 270.0f;
                     ed[0] = d[2] * -1.0d;
                     ed[2] = d[0] * -1.0d;
                     break;
                  default:
                     break;
               }
               f %= (byte) 4;
               nbtEntity.putByte("Facing", f);
               nbtEntity.getList("Rotation", 5).set(0, FloatTag.valueOf(er % 360.0f));
               nbtEntity.putInt("TileX", (int) ed[0]);
               nbtEntity.putInt("TileY", (int) ed[1]);
               nbtEntity.putInt("TileZ", (int) ed[2]);
               posList.add(DoubleTag.valueOf(ed[0]));
               posList.add(DoubleTag.valueOf(ed[1]));
               posList.add(DoubleTag.valueOf(ed[2]));
            }
            else {
               switch (rot) {
                  case 1:
                     ed[0] = d[2];
                     ed[2] = d[0];
                     break;
                  case 2:
                     ed[0] *= -1.0d;
                     ed[0] -= 1.0d;
                     ed[2] *= -1.0d;
                     ed[2] -= 1.0d;
                     break;
                  case 3:
                     ed[0] = d[2] * -1.0d;
                     ed[0] -= 1.0d;
                     ed[2] = d[0] * -1.0d;
                     ed[2] -= 1.0d;
                     break;
                  default:
                     break;
               }
               posList.add(DoubleTag.valueOf(ed[0] - 0.5d));
               posList.add(DoubleTag.valueOf(ed[1]));
               posList.add(DoubleTag.valueOf(ed[2] - 0.5d));
            }
            nbtEntity.put("Pos", posList);
            schema.entityList.add(nbtEntity);
         }
      }
      catch (Exception ignored) { }
      return schema;
   }

}
