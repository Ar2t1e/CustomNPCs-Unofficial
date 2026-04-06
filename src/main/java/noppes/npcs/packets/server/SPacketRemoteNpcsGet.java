package noppes.npcs.packets.server;

import java.util.*;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;
import noppes.npcs.packets.client.PacketGuiScrollSelected;
import noppes.npcs.util.CustomNPCsScheduler;

public class SPacketRemoteNpcsGet extends PacketServerBasic {

   protected static int channelId;
   private final boolean isAll;

   public SPacketRemoteNpcsGet(boolean all) { isAll = all; }

   @Override
   public PermissionNode<Boolean> getPermission() { return CustomNpcsPermissions.NPC_GUI; }

   public static void encode(SPacketRemoteNpcsGet msg, FriendlyByteBuf buf) { buf.writeBoolean(msg.isAll); }

   public static SPacketRemoteNpcsGet decode(FriendlyByteBuf buf) { return new SPacketRemoteNpcsGet(buf.readBoolean()); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      sendNearbyNpcs(player, isAll);
      Packets.send(player, new PacketGuiScrollSelected(CustomNpcs.FreezeNPCs ? "Unfreeze Npcs" : "Freeze Npcs"));
      CustomNpcs.debugData.end("Packets");
   }

   // New fields from Unofficial (BetaZavr)
   public static void sendNearbyNpcs(ServerPlayer player, boolean all) {
      CustomNPCsScheduler.runTack(() -> {
         HashMap<Float, CompoundTag> map = new HashMap<>();
         List<Float> alist = new ArrayList<>();
         List<Float> nlist = new ArrayList<>();
         CompoundTag compound = new CompoundTag();
         ListTag list = new ListTag();
         for (Entity entity : ((ServerLevel) player.level()).getAllEntities()) {
            if (entity == null || entity.isRemoved() || entity instanceof Player || (!all && !(entity instanceof EntityNPCInterface))) { continue; }
            CompoundTag nbt = new CompoundTag();
            nbt.putInt("Id", entity.getId());
            if (entity instanceof EntityNPCInterface cnpc) {
               nbt.putInt("Type", switch (cnpc.faction.playerStatus(player)) {
                  case -1 -> 2;
                  case 0 -> 3;
                  default -> 1;
               });
            }
            if (entity instanceof LivingEntity) {
               if (entity instanceof Mob) { nbt.putInt("Type", 2); }
               else if (entity instanceof Animal) { nbt.putInt("Type", 3); }
            }
            else { nbt.putInt("Type", 0); }
            nbt.putString("Name", Component.Serializer.toJson(entity.getName()));
            nbt.putString("Class", entity.getClass().getSimpleName());
            ListTag posList = new ListTag();
            posList.add(DoubleTag.valueOf(entity.getX()));
            posList.add(DoubleTag.valueOf(entity.getY()));
            posList.add(DoubleTag.valueOf(entity.getZ()));
            nbt.put("Pos", posList);
            float distance = player.distanceTo(entity);
            while (map.containsKey(distance)) { distance += 0.00001f; }
            nbt.putFloat("Distance", distance);
            if (entity instanceof EntityNPCInterface) { nlist.add(distance); }
            else { alist.add(distance); }
            map.put(distance, nbt);
         }
         Collections.sort(alist);
         Collections.sort(nlist);
         for (float d : nlist) { list.add(map.get(d)); }
         for (float d : alist) { list.add(map.get(d)); }
         compound.put("Data", list);
         Packets.send(player, new PacketGuiData(compound));
      });
   }

}
