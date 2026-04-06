package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.RecipeController;
import noppes.npcs.controllers.data.RecipeCarpentry;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketRecipeRemove extends PacketServerBasic {

   protected static int channelId;
   private final ResourceLocation recipe;

   public SPacketRecipeRemove(ResourceLocation recipeIn) { recipe = recipeIn; }

   @Override
   public PermissionNode<Boolean> getPermission() { return CustomNpcsPermissions.GLOBAL_RECIPE; }

   public static void encode(SPacketRecipeRemove msg, FriendlyByteBuf buf) { buf.writeResourceLocation(msg.recipe); }

   public static SPacketRecipeRemove decode(FriendlyByteBuf buf) { return new SPacketRecipeRemove(buf.readResourceLocation()); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      RecipeCarpentry r = RecipeController.getInstance().delete(recipe);
      SPacketRecipesGet.sendRecipeData(player, r.isGlobal ? 3 : 4);
      SPacketRecipeGet.setRecipeGui(player, new RecipeCarpentry(new ResourceLocation(CustomNpcs.MODID, ""), ""));
      CustomNpcs.debugData.end("Packets");
   }

}
