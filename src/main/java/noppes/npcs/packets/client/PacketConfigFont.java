package noppes.npcs.packets.client;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.ClientProxy;
import noppes.npcs.shared.common.PacketBasic;

public class PacketConfigFont extends PacketBasic {

   protected static int channelId;
   private final String font;
   private final int size;

   public PacketConfigFont(String fontIn, int sizeIn) {
      font = fontIn;
      size = sizeIn;
   }

   public static void encode(PacketConfigFont msg, FriendlyByteBuf buf) {
      buf.writeUtf(msg.font);
      buf.writeInt(msg.size);
   }

   public static PacketConfigFont decode(FriendlyByteBuf buf) { return new PacketConfigFont(buf.readUtf(), buf.readInt()); }

   @Override
   public int getChannelId() { return channelId; }

   @OnlyIn(Dist.CLIENT)
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      Runnable run = () -> {
         if (!font.isEmpty()) {
            CustomNpcs.FontType = font;
            CustomNpcs.FontSize = size;
            ClientProxy.Font.clear();
            ClientProxy.Font = new ClientProxy.FontContainer(CustomNpcs.FontType, CustomNpcs.FontSize);
            CustomNpcs.Config.updateConfig();
            player.sendSystemMessage(Component.translatable("Font set to " + ClientProxy.Font.getName()));
         } else {
            player.sendSystemMessage(Component.translatable("Current font is " + ClientProxy.Font.getName()));
         }
      };
      Minecraft.getInstance().submit(run);
      CustomNpcs.debugData.end("Packets");
   }

}
