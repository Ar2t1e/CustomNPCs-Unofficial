package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.FactionController;
import noppes.npcs.controllers.data.Faction;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

import java.util.Collections;
import java.util.List;

public class SPacketFactionSave extends PacketServerBasic {

   protected static int channelId;
   private final CompoundTag data;

   public SPacketFactionSave(CompoundTag dataIn) { data = dataIn; }

   @Override
   public boolean requiresNpc() { return false; }

   @Override
   public boolean toolAllowed(ItemStack item) { return item.getItem() == CustomItems.wand; }

   @Override
   public List<PermissionNode<Boolean>> getPermission() { return Collections.singletonList(CustomNpcsPermissions.GLOBAL_FACTION); }

   public static void encode(SPacketFactionSave msg, FriendlyByteBuf buf) { buf.writeNbt(msg.data); }

   public static SPacketFactionSave decode(FriendlyByteBuf buf) { return new SPacketFactionSave(buf.readNbt()); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      Faction faction = new Faction();
      faction.load(data);
      FactionController.instance.saveFaction(faction);
      SPacketFactionsGet.sendFactionDataAll(player);
      CompoundTag compound = new CompoundTag();
      faction.save(compound);
      Packets.send(player, new PacketGuiData(compound));
      CustomNpcs.debugData.end("Packets");
   }

}
