package noppes.npcs.packets.server;

import java.util.HashMap;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.controllers.RecipeController;
import noppes.npcs.controllers.data.RecipeCarpentry;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketRecipesGet extends PacketServerBasic {

   protected static int channelId;
   private final int width;

   public SPacketRecipesGet(int widthIn) { width = widthIn; }

   public static void encode(SPacketRecipesGet msg, FriendlyByteBuf buf) { buf.writeInt(msg.width); }

   public static SPacketRecipesGet decode(FriendlyByteBuf buf) { return new SPacketRecipesGet(buf.readInt()); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      sendRecipeData(player, width);
      CustomNpcs.debugData.end("Packets");
   }

   public static void sendRecipeData(ServerPlayer player, int size) {
      HashMap<String, Integer> map = new HashMap<>();
      RecipeController rData = RecipeController.getInstance();
      for (RecipeCarpentry recipe : (size == 3 ? rData.getGlobalRecipes() : rData.getAnvilRecipes()).values()) {
         map.put(recipe.name + "|" + recipe.getId() + "|" + recipe.isValid(), 0);
      }
      NoppesUtilServer.sendScrollData(player, map);
   }

}
