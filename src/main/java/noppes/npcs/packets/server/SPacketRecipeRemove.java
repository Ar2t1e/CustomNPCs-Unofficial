package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.RecipeController;
import noppes.npcs.items.crafting.NpcShapedRecipes;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiUpdate;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketRecipeRemove extends PacketServerBasic {

   protected static int channelId;
   private int size;
   private String group;
   private String name;

   public SPacketRecipeRemove() { }

   public SPacketRecipeRemove(int sizeIn, String groupIn, String nameIn) {
      size = sizeIn;
      group = groupIn;
      name = nameIn;
   }

   @Override
   public CustomNpcsPermissions.Permission getPermission() { return CustomNpcsPermissions.GLOBAL_RECIPE; }

   @Override
   public void encode(FriendlyByteBuf buf) {
      buf.writeInt(size);
      buf.writeUtf(group);
      buf.writeUtf(name);
   }

   @Override
   public void decode(FriendlyByteBuf buf) {
      size = buf.readInt();
      group = buf.readUtf();
      name = buf.readUtf();
   }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      /*
      INpcRecipe r = RecipeController.instance.delete(recipe);
      SPacketRecipesGet.sendRecipeData(player, r.isGlobal() ? 3 : 4);
      SPacketRecipeGet.setRecipeGui(player, new RecipeCarpentry(new ResourceLocation(CustomNpcs.MODID, ""), ""));
      /**/
      if (RecipeController.getInstance().delete(group, name)) {
         NoppesUtilServer.sendRecipeData(player, size, group, name);
         NoppesUtilServer.setRecipeGui(player, new NpcShapedRecipes());
      }
      Packets.send(player, new PacketGuiUpdate());
      CustomNpcs.debugData.end("Packets");
   }

}
