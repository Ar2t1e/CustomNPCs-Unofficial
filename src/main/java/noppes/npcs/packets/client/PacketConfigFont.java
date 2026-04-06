package noppes.npcs.packets.client;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.text.TextComponentTranslation;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.ClientProxy;
import noppes.npcs.shared.common.PacketBasic;

public class PacketConfigFont extends PacketBasic {

   protected static int channelId;
   private String font;
   private int size;

   public PacketConfigFont() { }

   public PacketConfigFont(String fontIn, int sizeIn) {
      font = fontIn;
      size = sizeIn;
   }

   @Override
   public void decode(FriendlyByteBuf buf) {
      font = buf.readUtf();
      size = buf.readInt();
   }

   @Override
   public void encode(FriendlyByteBuf buf) {
      buf.writeUtf(font);
      buf.writeInt(size);
   }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      Runnable run = () -> {
         if (font != null && !font.isEmpty()) {
            CustomNpcs.FontType = font;
            CustomNpcs.FontSize = size;
            ClientProxy.Font.clear();
            ClientProxy.Font = new ClientProxy.FontContainer(CustomNpcs.FontType, CustomNpcs.FontSize);
            CustomNpcs.Config.updateConfig();
            player.sendMessage(new TextComponentTranslation("Font set to %s", ClientProxy.Font.getName()));
         }
         else { player.sendMessage(new TextComponentTranslation("Current font is %s", ClientProxy.Font.getName())); }
      };
      Minecraft.getMinecraft().addScheduledTask(run);
      CustomNpcs.debugData.end("Packets");
   }

}
