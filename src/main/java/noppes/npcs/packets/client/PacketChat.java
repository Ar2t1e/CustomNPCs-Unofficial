package noppes.npcs.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.CustomNpcs;
import noppes.npcs.shared.common.PacketBasic;

public class PacketChat extends PacketBasic {

   protected static int channelId;
   private final Component message;

   public PacketChat(Component messageIn) { message = messageIn; }

   public static void encode(PacketChat msg, FriendlyByteBuf buf) { buf.writeComponent(msg.message); }

   public static PacketChat decode(FriendlyByteBuf buf) { return new PacketChat(buf.readComponent()); }

   @Override
   public int getChannelId() { return channelId; }

   @OnlyIn(Dist.CLIENT)
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      player.sendSystemMessage(message);
      CustomNpcs.debugData.end("Packets");
   }

}
