package noppes.npcs.packets.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.gui.custom.GuiCustom;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.shared.common.PacketBasic;

public class PacketGuiParts extends PacketBasic {

   protected static int channelId;
   private int id;
   private NBTTagCompound data;

   public PacketGuiParts() { }

   public PacketGuiParts(int idIn, NBTTagCompound dataIn) {
      id = idIn;
      data = dataIn;
   }

   @Override
   public void decode(FriendlyByteBuf buf) {
      id = buf.readInt();
      data = buf.readNbt();
   }

   @Override
   public void encode(FriendlyByteBuf buf) {
      buf.writeInt(id);
      buf.writeNbt(data);
   }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      Entity entity = player.world.getEntityByID(id);
      Minecraft mc = Minecraft.getMinecraft();
      if (mc.currentScreen instanceof GuiCustom && entity instanceof EntityCustomNpc) {
         /*GuiCreationNewParts parts = new GuiCreationNewParts(((GuiCustom) mc.currentScreen), ((EntityCustomNpc) entity));
         gui.initCallback = () -> {
            gui.add(parts);
            parts.init();
         };*/
         ((GuiCustom) mc.currentScreen).setGuiData(data);
      }
      CustomNpcs.debugData.end("Packets");
   }

}
