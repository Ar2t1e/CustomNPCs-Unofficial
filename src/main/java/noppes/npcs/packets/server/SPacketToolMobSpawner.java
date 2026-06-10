package noppes.npcs.packets.server;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.NBTTags;
import noppes.npcs.controllers.ServerCloneController;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.mixin.world.level.IBaseSpawnerMixin;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.shared.common.util.LogWriter;

import javax.annotation.Nullable;

public class SPacketToolMobSpawner extends PacketServerBasic {

   protected static int channelId;
   private final boolean createSpawner;
   private final boolean isServerClone;
   private final BlockPos pos;
   private final String name;
   private final int tab;
   private final CompoundTag clone;

   public SPacketToolMobSpawner(boolean isServer, boolean createSpawnerIn, BlockPos posIn, String nameIn, int tabIn, CompoundTag cloneIn) {
      isServerClone = isServer;
      createSpawner = createSpawnerIn;
      pos = posIn;
      name = nameIn;
      tab = tabIn;
      clone = cloneIn;
   }

   @Override
   public boolean requiresNpc() { return false; }

   @Override
   public boolean toolAllowed(ItemStack item) { return item.getItem() == CustomItems.cloner; }

   @Override
   public List<PermissionNode<Boolean>> getPermission() {
      return Collections.singletonList(createSpawner ? CustomNpcsPermissions.SPAWNER_CREATE : CustomNpcsPermissions.SPAWNER_MOB);
   }

   public static void encode(SPacketToolMobSpawner msg, FriendlyByteBuf buf) {
      buf.writeBoolean(msg.isServerClone);
      buf.writeBoolean(msg.createSpawner);
      buf.writeBlockPos(msg.pos);
      buf.writeUtf(msg.name);
      buf.writeInt(msg.tab);
      buf.writeNbt(msg.clone);
   }

   public static SPacketToolMobSpawner decode(FriendlyByteBuf buf) {
      return new SPacketToolMobSpawner(buf.readBoolean(), buf.readBoolean(), buf.readBlockPos(), buf.readUtf(), buf.readInt(), buf.readNbt());
   }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   public void handle() {
      CustomNpcs.debugData.start("Packets");
      CompoundTag compound;
      CompoundTag nbtData = new CompoundTag();
      if (isServerClone) {
         nbtData.putString("Name", name);
         nbtData.putInt("Tab", tab);
         nbtData.putBoolean("isServerClone", true);
         compound = ServerCloneController.Instance.getCloneData(player.createCommandSourceStack(), name, tab);
      }
      else {
         compound = clone;
         nbtData.putString("Name", compound.getString("Name"));
         nbtData.putBoolean("isServerClone", false);
      }
      if (compound != null && !compound.isEmpty()) {
         if (createSpawner) { createMobSpawner(pos, compound, player); }
         else {
            if (!isServerClone) { nbtData.put("EntityNBT", compound); }
            Entity entity = SPacketToolMobSpawner.spawnClone(compound, pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d, player.level());
            ItemStack stack = player.getMainHandItem();
            CompoundTag nbt = null;
            if (stack.getItem() == CustomItems.cloner) {
               nbt = stack.getTag();
               if (nbt == null) { stack.setTag(nbt = new CompoundTag()); }
            }
            if (entity == null) {
               if (nbt != null && nbt.contains("Settings")) {
                  nbt.remove("Settings");
                  player.containerMenu.broadcastChanges();
               }
               player.sendSystemMessage(Component.literal("Failed to create an entity out of your clone"));
            }
            else {
               if (nbt != null) {
                  if (!nbtData.contains("Name", 8) || nbtData.getString("Name").isEmpty()) {
                     nbtData.putString("Name", entity.getName().getString());
                  }
                  nbt.put("Settings", nbtData);
                  player.containerMenu.broadcastChanges();
               }
            }
         }
      }
      CustomNpcs.debugData.end("Packets");
   }

   public static @Nullable Entity spawnClone(CompoundTag compound, double x, double y, double z, Level level) {
      if (level == null || level.isClientSide()) {
         LogWriter.error("Clone summoning Error: Level is Client: " + (level == null ? "null" : "true") + " - " + level);
         return null;
      }
      if (compound == null) {
         LogWriter.error("Clone summoning Error: Missing NBT Tags: "
                 + "null or Level: "
                 + level.dimension().location());
         return null;
      }
      ServerCloneController.Instance.cleanTags(compound);
      compound.put("Pos", NBTTags.nbtDoubleList(x, y, z));
      Optional<Entity> type = EntityType.create(compound, level);
      if (type.isEmpty()) {
         LogWriter.error("Clone summoning error: Failed to create an entity based on the passed NBT tags: " + compound);
         return null;
      }
      Entity entity = type.get();
      BlockPos pos = new BlockPos((int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z));
      if (entity instanceof EntityNPCInterface npc) {
         npc.homeDimensionId = level.dimension();
         npc.ais.setStartPos(pos);
      }
      else if (entity instanceof Mob mob) { mob.restrictTo(pos, (int) mob.getRestrictRadius()); }
      entity.load(compound);
      entity.setPos(x, y, z);
      level.addFreshEntity(entity);
      return entity;
   }

   public static void createMobSpawner(BlockPos pos, CompoundTag comp, Player player) {
      ServerCloneController.Instance.cleanTags(comp);
      if (comp.getString("id").equalsIgnoreCase("entityhorse")) {
         player.sendSystemMessage(Component.translatable("message.error.create.mob.spawner"));
      }
      else {
         player.level().setBlockAndUpdate(pos, Blocks.SPAWNER.defaultBlockState());
         SpawnerBlockEntity tile = (SpawnerBlockEntity)player.level().getBlockEntity(pos);
         if (tile != null) {
            BaseSpawner logic = tile.getSpawner();
            if (!comp.contains("id", 8)) { comp.putString("id", "Pig"); }
            comp.putIntArray("StartPosNew", new int[]{pos.getX(), pos.getY(), pos.getZ()});
            ((IBaseSpawnerMixin) logic).callSetNextSpawnData(player.level(), pos, new SpawnData(comp, Optional.empty()));
         }
      }
   }
}
