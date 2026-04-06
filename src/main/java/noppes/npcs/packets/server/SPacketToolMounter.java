package noppes.npcs.packets.server;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.client.EntityUtil;
import noppes.npcs.controllers.ServerCloneController;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketToolMounter extends PacketServerBasic {

   protected static int channelId;
   private int type;
   private String name = "";
   private int tab = -1;
   private NBTTagCompound compound = new NBTTagCompound();

   private SPacketToolMounter(int typeIn, String nameIn, int tabIn, NBTTagCompound compoundIn) {
      type = typeIn;
      name = nameIn;
      tab = tabIn;
      compound = compoundIn;
   }

   public SPacketToolMounter(int typeIn, String nameIn, int tabIn) {
      type = typeIn;
      name = nameIn;
      tab = tabIn;
   }

   public SPacketToolMounter(int typeIn, NBTTagCompound compoundIn) {
      type = typeIn;
      compound = compoundIn;
   }

   public SPacketToolMounter() { type = 3; }

   @Override
   public boolean toolAllowed(ItemStack item) { return item.getItem() == CustomItems.mount; }

   @Override
   public CustomNpcsPermissions.Permission getPermission() { return CustomNpcsPermissions.TOOL_MOUNTER; }

   @Override
   public void encode(FriendlyByteBuf buf) {
      buf.writeInt(type);
      buf.writeUtf(name);
      buf.writeInt(tab);
      buf.writeNbt(compound);
   }

   @Override
   public void decode(FriendlyByteBuf buf) {
      type = buf.readInt();
      name = buf.readUtf();
      tab = buf.readInt();
      compound = buf.readNbt();
   }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      PlayerData data = PlayerData.get(player);
      if (data.mounted != null) {
         Entity entity;
         if (type == 0) {
            entity = EntityList.createEntityFromNBT(compound, player.world);
            if (entity != null) {
               entity.setPosition(data.mounted.posX, data.mounted.posY, data.mounted.posZ);
               player.world.spawnEntity(entity);
               entity.startRiding(data.mounted, true);
            }
         }
         else if (type == 1) {
            entity = EntityList.createEntityFromNBT(ServerCloneController.Instance.getCloneData(player, name, tab), player.world);
            if (entity != null) {
               entity.setPosition(data.mounted.posX, data.mounted.posY, data.mounted.posZ);
               player.world.spawnEntity(entity);
               entity.startRiding(data.mounted, true);
            }
         }
         else if (type == 2) {
            ResourceLocation loc = EntityUtil.getAllEntities(player.world, false).get(name);
            EntityEntry t = ForgeRegistries.ENTITIES.getValue(loc);
            if (t != null) {
               entity = t.newInstance(player.world);
               if (entity != null) {
                  entity.setPosition(data.mounted.posX, data.mounted.posY, data.mounted.posZ);
                  player.world.spawnEntity(entity);
                  entity.startRiding(data.mounted, true);
               }
            }
         } else {
            player.startRiding(data.mounted, true);
         }
      }
      CustomNpcs.debugData.end("Packets");
   }
}
