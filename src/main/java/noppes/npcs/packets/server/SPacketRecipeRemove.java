package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.RecipeController;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiUpdate;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketRecipeRemove extends PacketServerBasic {

   protected static int channelId;
   private ResourceLocation recipeId;

   public SPacketRecipeRemove() { }

   public SPacketRecipeRemove(ResourceLocation recipeIdIn) { recipeId = recipeIdIn; }

   @Override
   public CustomNpcsPermissions.Permission getPermission() { return CustomNpcsPermissions.GLOBAL_RECIPE; }

   @Override
   public void encode(FriendlyByteBuf buf) { buf.writeResourceLocation(recipeId); }

   @Override
   public void decode(FriendlyByteBuf buf) { recipeId = buf.readResourceLocation(); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      RecipeController.getInstance().delete(recipeId);
      Packets.sendDelayed(player, new PacketGuiUpdate(), 100);
      CustomNpcs.debugData.end("Packets");
   }

}
