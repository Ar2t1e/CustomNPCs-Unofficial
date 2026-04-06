package noppes.npcs.controllers.data;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.entity.data.role.ITransportLocation;
import noppes.npcs.containers.inventories.NpcMiscInventory;

import java.util.UUID;

public class TransportLocation implements ITransportLocation {

   public int id = -1;
   public String name = "default name";
   public BlockPos pos;
   public int type = 0;
   public ResourceKey<Level> dimension = ResourceKey.create(Registries.DIMENSION, Level.OVERWORLD.location());
   public TransportCategory category;

   // New from Unofficial (BetaZavr)
   public final NpcMiscInventory inventory = new NpcMiscInventory(9);
   public UUID npc = null;
   public long money = 0;
   public float yaw = 0.0f;
   public float pitch = 0.0f;

   public void load(CompoundTag compound) {
      if (compound != null) {
         id = compound.getInt("Id");
         pos = new BlockPos((int)compound.getDouble("PosX"), (int)compound.getDouble("PosY"), (int)compound.getDouble("PosZ"));
         type = compound.getInt("Type");
         ResourceLocation location = ResourceLocation.tryParse(compound.getString("DimensionType"));
         if (location == null) { location = new ResourceLocation("minecraft", "overworld"); }
         dimension = ResourceKey.create(Registries.DIMENSION, location);
         name = compound.getString("Name");

         // New from Unofficial (BetaZavr)
         inventory.clearContent();
         if (compound.contains("CostInv", 10)) {
            inventory.load(compound.getCompound("CostInv"));
         }
         npc = null;
         if (compound.contains("NpcUUID", 11)) { npc = compound.getUUID("NpcUUID"); }
         money = compound.getLong("Cost");
         yaw = compound.getFloat("PlayerYaw");
         pitch = compound.getFloat("PlayerPitch");
      }
   }

   public CompoundTag save() {
      CompoundTag compound = new CompoundTag();
      compound.putInt("Id", id);
      compound.putDouble("PosX", pos.getX());
      compound.putDouble("PosY", pos.getY());
      compound.putDouble("PosZ", pos.getZ());
      compound.putInt("Type", type);
      compound.putString("DimensionType", dimension.location().toString());
      compound.putString("Name", name);

      // New from Unofficial (BetaZavr)
      compound.put("CostInv", inventory.save());
      if (npc != null) { compound.putUUID("NpcUUID", npc); }
      compound.putLong("Cost", money);
      compound.putFloat("PlayerYaw", yaw);
      compound.putFloat("PlayerPitch", pitch);
      return compound;
   }

   @Override
   public int getId() { return id; }

   @Override
   public String getDimension() { return dimension.location().toString(); }

   @Override
   public int getX() { return pos.getX(); }

   @Override
   public int getY() { return pos.getY(); }

   @Override
   public int getZ() { return pos.getZ(); }

   @Override
   public String getName() { return name; }

   @Override
   public int getType() { return type; }

   public boolean isDefault() { return type == 1; }

   // New from Unofficial (BetaZavr)
   @Override
   public void setPos(String dimensionId, int x, int y, int z) {
      ResourceKey<Level> key = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(dimensionId));
      if (CustomNpcs.Server != null && CustomNpcs.Server.getLevel(key) != null) {
         dimension = key;
         pos = new BlockPos(x, y, z);
      }
      else { throw new CustomNPCsException("Unknown dimensionId: " + dimensionId); }
   }

   @Override
   public void setType(int typeIn) {
      if (typeIn < 0 || typeIn > 2) { throw new CustomNPCsException("Unknown location type (0<>2): " + typeIn); }
      type = typeIn;
   }

   public TransportLocation copy() {
      TransportLocation tl = new TransportLocation();
      tl.id = id;
      tl.name = name;
      tl.type = type;
      tl.dimension = dimension;
      tl.npc = npc;
      tl.money = money;
      tl.pos = pos;
      for (int i = 0; i < inventory.getContainerSize(); i++) {
         tl.inventory.setItem(i, inventory.getItem(i).copy());
      }
      tl.category = category;
      tl.yaw = yaw;
      tl.pitch = yaw;
      return tl;
   }

}
