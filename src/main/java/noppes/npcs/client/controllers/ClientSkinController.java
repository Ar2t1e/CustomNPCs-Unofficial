package noppes.npcs.client.controllers;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import noppes.npcs.client.SkinUtil;
import noppes.npcs.controllers.data.SkinData;

public class ClientSkinController {
   private static final Map<String, SkinData> skinInfo = new HashMap<>();

   public static void addSkinForPlayer(String playerName, SkinData skinData) {
      SkinUtil.createPlayerSkin(skinData);
      skinInfo.put(playerName, skinData);
   }

   public static ResourceLocation getSkinForPlayer(String playerName) {
      SkinData skin = skinInfo.get(playerName);
      return skin == null ? null : skin.getLocation();
   }

}
