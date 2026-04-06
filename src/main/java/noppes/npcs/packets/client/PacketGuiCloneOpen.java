package noppes.npcs.packets.client;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.GuiNpcMobSpawnerAdd;
import noppes.npcs.shared.common.PacketBasic;

public class PacketGuiCloneOpen extends PacketBasic {

   protected static int channelId;
   private final CompoundTag data;

   public PacketGuiCloneOpen(CompoundTag dataIn) { data = dataIn; }

   public static void encode(PacketGuiCloneOpen msg, FriendlyByteBuf buf) { buf.writeNbt(msg.data); }

   public static PacketGuiCloneOpen decode(FriendlyByteBuf buf) { return new PacketGuiCloneOpen(buf.readNbt()); }

   @Override
   public int getChannelId() { return channelId; }

   @OnlyIn(Dist.CLIENT)
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      NoppesUtil.openGUI(player, new GuiNpcMobSpawnerAdd(data));
      CustomNpcs.debugData.end("Packets");
   }

}
