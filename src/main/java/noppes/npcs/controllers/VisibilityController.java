package noppes.npcs.controllers;

import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import net.minecraft.server.level.ServerPlayer;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.entity.EntityNPCInterface;

public class VisibilityController {

   public static VisibilityController instance = new VisibilityController();
   private final Map<Integer, EntityNPCInterface> trackedEntityHashTable = new TreeMap<>();

   public static void checkIsVisible(EntityNPCInterface npc, ServerPlayer player) {
      if (CustomNpcs.EnableInvisibleNpcs && CustomNpcs.InvisibilityAlgorithm == 0) {
         boolean bo = player.getMainHandItem().getItem() != CustomItems.wand && player.getOffhandItem().getItem() != CustomItems.wand;
         if (!npc.display.isVisibleTo(player) && !player.isSpectator() && bo) { npc.setInvisible(player); }
         else { npc.setVisible(player); }
      }
   }

   public void trackNpc(EntityNPCInterface npc) {
      if (!npc.isClientSide()) {
         boolean hasOptions = npc.display.getAvailability().hasOptions();
         if ((hasOptions || npc.display.getVisible() != 0) && !trackedEntityHashTable.containsKey(npc.getId())) { trackedEntityHashTable.put(npc.getId(), npc); }
         if (!hasOptions && npc.display.getVisible() == 0) { trackedEntityHashTable.remove(npc.getId()); }
      }
   }

   public void remove(EntityNPCInterface npc) {
      if (!npc.isClientSide()) { trackedEntityHashTable.remove(npc.getId()); }
   }

   public void onUpdate(ServerPlayer player) {
      if (CustomNpcs.EnableInvisibleNpcs) {
         for (Entry<Integer, EntityNPCInterface> entry : trackedEntityHashTable.entrySet()) { checkIsVisible(entry.getValue(), player); }
      }
   }

}
