package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import noppes.npcs.CustomNpcs;
import noppes.npcs.containers.ContainerManageRecipes;
import noppes.npcs.controllers.RecipeController;
import noppes.npcs.controllers.data.RecipeCarpentry;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

public class SPacketRecipeGet extends PacketServerBasic {

   protected static int channelId;
   private final ResourceLocation recipe;

   public SPacketRecipeGet(ResourceLocation recipeIn) { recipe = recipeIn; }

   public static void encode(SPacketRecipeGet msg, FriendlyByteBuf buf) { buf.writeResourceLocation(msg.recipe); }

   public static SPacketRecipeGet decode(FriendlyByteBuf buf) { return new SPacketRecipeGet(buf.readResourceLocation()); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      RecipeCarpentry r = RecipeController.getInstance().getRecipe(recipe);
      setRecipeGui(player, r);
      CustomNpcs.debugData.end("Packets");
   }

   public static void setRecipeGui(ServerPlayer player, RecipeCarpentry recipe) {
      if (recipe != null && player.containerMenu instanceof ContainerManageRecipes container) {
         container.setRecipe(recipe, player.level().registryAccess());
         Packets.send(player, new PacketGuiData(recipe.writeNBT()));
      }
   }

}
