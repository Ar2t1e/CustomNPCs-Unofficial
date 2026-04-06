package noppes.npcs.packets.server;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.handler.data.INpcRecipe;
import noppes.npcs.containers.ContainerManageRecipes;
import noppes.npcs.controllers.RecipeController;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

public class SPacketRecipeGet extends PacketServerBasic {

   protected static int channelId;
   private ResourceLocation recipe;

   public SPacketRecipeGet() { }

   public SPacketRecipeGet(ResourceLocation recipeIn) { recipe = recipeIn; }

   @Override
   public void encode(FriendlyByteBuf buf) { buf.writeResourceLocation(recipe); }

   @Override
   public void decode(FriendlyByteBuf buf) { recipe = buf.readResourceLocation(); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      INpcRecipe r = RecipeController.getInstance().getRecipe(recipe);
      setRecipeGui(player, r);
      CustomNpcs.debugData.end("Packets");
   }

   public static void setRecipeGui(EntityPlayerMP player, INpcRecipe recipe) {
      if (recipe != null && player.openContainer instanceof ContainerManageRecipes) {
            ((ContainerManageRecipes) player.openContainer).setRecipe(recipe);
            Packets.send(player, new PacketGuiData(recipe.writeNBT()));
      }
   }

}
