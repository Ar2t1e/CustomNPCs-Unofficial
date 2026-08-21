package noppes.npcs.packets.server;

import java.util.*;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.mixin.world.IWorldMixin;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;
import noppes.npcs.packets.client.PacketGuiScrollSelected;
import noppes.npcs.util.CustomNPCsScheduler;

public class SPacketRemoteNpcsGet extends PacketServerBasic {

   protected static int channelId;
   private boolean isAll;

   public SPacketRemoteNpcsGet() { }

   public SPacketRemoteNpcsGet(boolean all) { isAll = all; }

   @Override
   public boolean requiresNpc() { return false; }

   @Override
   public boolean toolAllowed(ItemStack item) { return item.getItem() == CustomItems.wand; }

   @Override
   public List<CustomNpcsPermissions.Permission> getPermission() { return Collections.singletonList(CustomNpcsPermissions.NPC_GUI); }

   @Override
   public void encode(FriendlyByteBuf buf) { buf.writeBoolean(isAll); }

   @Override
   public void decode(FriendlyByteBuf buf) { isAll = buf.readBoolean(); }

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
   public static void sendNearbyNpcs(EntityPlayerMP player, boolean all) {
      CustomNPCsScheduler.runTack(() -> {
         HashMap<Float, NBTTagCompound> map = new HashMap<>();
         List<Float> alist = new ArrayList<>();
         List<Float> nlist = new ArrayList<>();
         NBTTagCompound compound = new NBTTagCompound();
         NBTTagList list = new NBTTagList();
         List<Entity> entitys = new ArrayList<>(player.world.loadedEntityList);
         for (Entity e : ((IWorldMixin) player.world).getUnloadedEntityList()) { if (!entitys.contains(e)) { entitys.add(e); } }
         for (Entity entity : entitys) {
            if (entity.isDead || (!all && !(entity instanceof EntityNPCInterface)) || entity instanceof EntityPlayer) { continue; }
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setInteger("Id", entity.getEntityId());
            if (entity instanceof EntityNPCInterface) {
               int type;
               switch (((EntityNPCInterface) entity).faction.playerStatus(player)) {
                  case -1: type = 2; break;
                  case 0: type = 3; break;
                  default: type = 1; break;
               }
               nbt.setInteger("Type", type);
            }
            if (entity instanceof EntityLiving) {
               if (entity instanceof EntityMob) { nbt.setInteger("Type", 2); }
               else if (entity instanceof EntityAnimal) { nbt.setInteger("Type", 3); }
            }
            else { nbt.setInteger("Type", 0); }
            nbt.setString("Name", entity.getName());
            nbt.setString("Class", entity.getClass().getSimpleName());
            NBTTagList posList = new NBTTagList();
            posList.appendTag(new NBTTagDouble(entity.posX));
            posList.appendTag(new NBTTagDouble(entity.posY));
            posList.appendTag(new NBTTagDouble(entity.posZ));
            nbt.setTag("Pos", posList);
            float distance = player.getDistance(entity);
            while (map.containsKey(distance)) { distance += 0.00001f; }
            nbt.setFloat("Distance", distance);
            if (entity instanceof EntityNPCInterface) { nlist.add(distance); }
            else { alist.add(distance); }
            map.put(distance, nbt);
         }
         Collections.sort(alist);
         Collections.sort(nlist);
         for (float d : nlist) { list.appendTag(map.get(d)); }
         for (float d : alist) { list.appendTag(map.get(d)); }
         compound.setTag("Data", list);
         Packets.send(player, new PacketGuiData(compound));
      });
   }

}
