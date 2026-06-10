package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.controllers.SpawnController;
import noppes.npcs.controllers.data.SpawnData;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.Collections;
import java.util.List;

public class SPacketNaturalSpawnSave extends PacketServerBasic {

   protected static int channelId;
   private final CompoundTag data;

   public SPacketNaturalSpawnSave(CompoundTag dataIn) { data = dataIn; }

   @Override
   public boolean requiresNpc() { return false; }

   @Override
   public boolean toolAllowed(ItemStack item) { return item.getItem() == CustomItems.wand; }

   @Override
   public List<PermissionNode<Boolean>> getPermission() { return Collections.singletonList(CustomNpcsPermissions.GLOBAL_NATURALSPAWN); }

   public static void encode(SPacketNaturalSpawnSave msg, FriendlyByteBuf buf) { buf.writeNbt(msg.data); }

   public static SPacketNaturalSpawnSave decode(FriendlyByteBuf buf) { return new SPacketNaturalSpawnSave(buf.readNbt()); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      SpawnData sData = new SpawnData();
      sData.load(data);
      SpawnController.instance.saveSpawnData(sData);
      NoppesUtilServer.sendScrollData(player, SpawnController.instance.getScroll());
      CustomNpcs.debugData.end("Packets");
   }

}
