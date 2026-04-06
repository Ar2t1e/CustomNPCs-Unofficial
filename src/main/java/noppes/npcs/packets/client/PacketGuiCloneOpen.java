package noppes.npcs.packets.client;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.GuiNpcMobSpawnerAdd;
import noppes.npcs.shared.common.PacketBasic;

public class PacketGuiCloneOpen extends PacketBasic {

   protected static int channelId;
   private NBTTagCompound data;

   public PacketGuiCloneOpen() { }

   public PacketGuiCloneOpen(NBTTagCompound dataIn) { data = dataIn; }

   @Override
   public void decode(FriendlyByteBuf buf) { data = buf.readNbt(); }

   @Override
   public void encode(FriendlyByteBuf buf) { buf.writeNbt(data); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      NoppesUtil.openGUI(player, new GuiNpcMobSpawnerAdd(data));
      CustomNpcs.debugData.end("Packets");
   }

}
