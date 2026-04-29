package noppes.npcs.packets.server;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.RecipeController;
import noppes.npcs.controllers.data.RecipeCarpentry;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiUpdate;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketRecipeSave extends PacketServerBasic {

   protected static int channelId;
   private NBTTagCompound data;

   public SPacketRecipeSave() { }

   public SPacketRecipeSave(NBTTagCompound dataIn) { data = dataIn; }

   @Override
   public CustomNpcsPermissions.Permission getPermission() { return CustomNpcsPermissions.GLOBAL_RECIPE; }

   @Override
   public void encode(FriendlyByteBuf buf) { buf.writeNbt(data); }

   @Override
   public void decode(FriendlyByteBuf buf) { data = buf.readNbt(); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      RecipeCarpentry recipe = RecipeCarpentry.create(data);
      RecipeController.getInstance().addAndSaveRecipe(recipe);
      Packets.sendDelayed(player, new PacketGuiUpdate(), 100);
      CustomNpcs.debugData.end("Packets");
   }

}
