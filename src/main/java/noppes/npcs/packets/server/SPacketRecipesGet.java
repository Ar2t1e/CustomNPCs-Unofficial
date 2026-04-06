package noppes.npcs.packets.server;

import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.handler.data.INpcRecipe;
import noppes.npcs.controllers.RecipeController;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketRecipesGet extends PacketServerBasic {

   protected static int channelId;
   private int width;

   public SPacketRecipesGet() { }

   public SPacketRecipesGet(int widthIn) { width = widthIn; }

   @Override
   public void encode(FriendlyByteBuf buf) { buf.writeInt(width); }

   @Override
   public void decode(FriendlyByteBuf buf) { width = buf.readInt(); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      sendRecipeData(player, width);
      CustomNpcs.debugData.end("Packets");
   }

   public static void sendRecipeData(EntityPlayerMP player, int size) {
      HashMap<String, Integer> map = new HashMap<>();
      if (size == 3) {
         for (INpcRecipe recipe : RecipeController.instance.globalRecipes.values()) { map.put(recipe.getName() + "|" + recipe.getId(), 0); }
      } else {
         for (INpcRecipe recipe : RecipeController.instance.anvilRecipes.values()) { map.put(recipe.getName() + "|" + recipe.getId(), 0); }
      }
      NoppesUtilServer.sendScrollData(player, map);
   }

}
