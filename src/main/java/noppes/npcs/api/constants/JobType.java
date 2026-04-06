package noppes.npcs.api.constants;

import net.minecraft.network.chat.Component;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.*;

import java.util.ArrayList;
import java.util.List;

public enum JobType {

   NONE("none", 0, false),
   BARD("bard", 1, true),
   HEALER("healer", 2, true),
   GUARD("guard", 3, true),
   ITEM_GIVER("itemgiver", 4, true),
   FOLLOWER("follower", 5, true),
   SPAWNER("spawner", 6, true),
   CONVERSATION("conversation", 7, true),
   CHUNK_LOADER("chunkloader", 8, false),
   PUPPET("puppet", 9, true),
   BUILDER("builder", 10, false),
   FARMER("farmer", 11, true);

   public static JobType get(int id) {
      for (JobType ej : JobType.values()) {
         if (ej.type == id) { return ej; }
      }
      return JobType.NONE;
   }

   public static Object[] getNames() {
      List<Component> list = new ArrayList<>();
      for (JobType ej : JobType.values()) {
         if (ej != PUPPET) { list.add(ej.name); }
      }
      return list.toArray(new Component[0]);
   }

   private final int type;
   public final Component name;
   public final boolean hasSettings;

   JobType(String named, int t, boolean hasSet) {
      type = t;
      name = Component.translatable("job." + named);
      hasSettings = hasSet;
   }

   public int get() { return type; }

   public void setToNpc(EntityNPCInterface npc) {
      npc.job = switch (this) {
         case BARD -> new JobBard(npc);
         case HEALER -> new JobHealer(npc);
         case GUARD -> new JobGuard(npc);
         case ITEM_GIVER -> new JobItemGiver(npc);
         case FOLLOWER -> new JobFollower(npc);
         case SPAWNER -> new JobSpawner(npc);
         case CONVERSATION -> new JobConversation(npc);
         case CHUNK_LOADER -> new JobChunkLoader(npc);
         case BUILDER -> new JobBuilder(npc);
         case FARMER -> new JobFarmer(npc);
         default -> JobInterface.NONE;
      };
      if (this == PUPPET) { npc.puppet = new JobPuppet(npc); }
   }

}
