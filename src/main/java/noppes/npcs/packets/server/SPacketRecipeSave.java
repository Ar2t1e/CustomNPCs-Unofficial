package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.RecipeController;
import noppes.npcs.controllers.data.RecipeCarpentry;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketRecipeSave extends PacketServerBasic {

   protected static int channelId;
   private final CompoundTag data;

   public SPacketRecipeSave(CompoundTag dataIn) { data = dataIn; }

   @Override
   public PermissionNode<Boolean> getPermission() { return CustomNpcsPermissions.GLOBAL_RECIPE; }

   public static void encode(SPacketRecipeSave msg, FriendlyByteBuf buf) { buf.writeNbt(msg.data); }

   public static SPacketRecipeSave decode(FriendlyByteBuf buf) { return new SPacketRecipeSave(buf.readNbt()); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      RecipeCarpentry recipe = RecipeCarpentry.load(data);
      RecipeController.getInstance().saveRecipe(recipe);
      SPacketRecipesGet.sendRecipeData(player, recipe.isGlobal ? 3 : 4);
      SPacketRecipeGet.setRecipeGui(player, recipe);
      CustomNpcs.debugData.end("Packets");
   }

}
