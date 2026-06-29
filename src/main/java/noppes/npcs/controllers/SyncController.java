package noppes.npcs.controllers;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.config.ConfigLoader;
import noppes.npcs.controllers.data.*;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketSync;
import noppes.npcs.packets.client.PacketSyncUpdate;
import noppes.npcs.util.BuilderData;

import java.util.HashMap;
import java.util.Map;

public class SyncController {

   // New from Unofficial (BetaZavr)
   public static final Map<Integer, BuilderData> dataBuilder = new HashMap<>();

   public static void syncPlayer(ServerPlayer player) {
      // send all faction settings
      syncAllFactions(player);
      // player data quests
      syncAllQuests(player);
      // player data dialogs
      syncAllDialogs(player);
      // dimension data
      syncAllDimensions(player);
      // global recipes
      syncAllRecipes(player);
      // main player data
      PlayerData data = PlayerData.get(player);
      Packets.send(player, new PacketSync(8, data.getNBT(), true));

      // New from Unofficial (BetaZavr)
      BorderController.getInstance().sendTo(player); // borders data
      MarcetController.getInstance().sendTo(player, -1); // markets
      ScriptController.Instance.sendClientTo(player); // send all client scripts
      CustomNpcsPermissions.sendTo(player); // permissions
      AnimationController.getInstance().sendTo(player); // custom animations
      ConfigLoader.sendTo(player); // mod data
      Packets.send(player, new PacketSync(17, KeyController.getInstance().getNBT(), true)); // custom keys
   }

   private static void syncAllFactions(ServerPlayer player) {
      ListTag list = new ListTag();
      CompoundTag compound = new CompoundTag();
      for (Faction faction : FactionController.instance.factions.values()) { list.add(faction.save(new CompoundTag())); }
      compound.put("Data", list);
      Packets.send(player, new PacketSync(1, compound, true));
   }

   public static void syncAllQuests(ServerPlayer player) {
      for (QuestCategory category : QuestController.instance.categories.values()) {
         Packets.send(player, new PacketSync(3, category.save(new CompoundTag()), false));
      }
      Packets.send(player, new PacketSync(3, new CompoundTag(), true));
   }

   public static void syncAllDialogs(ServerPlayer player) {
      for (DialogCategory category : DialogController.instance.categories.values()) {
         Packets.send(player, new PacketSync(5, category.save(new CompoundTag()), false));
      }
      Packets.send(player, new PacketSync(5, new CompoundTag(), true));
      Packets.send(player, new PacketSyncUpdate(0, 11, DialogController.instance.getGuiSettings().save()));
   }

   private static void syncAllDimensions(ServerPlayer player) {
      DimensionController.load();
      CompoundTag compound = new CompoundTag();
      ListTag list = new ListTag();
      for (ServerLevel level : CustomNpcs.Server.getAllLevels()) {
         DimensionData dd = DimensionController.get(level);
         dd.isLoad = level.isLoaded(BlockPos.ZERO);
         list.add(dd.save());
      }
      compound.put("Data", list);
      Packets.send(player, new PacketSync(9, compound, true));
   }

   // New from Unofficial (BetaZavr)
   private static void syncAllRecipes(ServerPlayer player) {
      RecipeController.getInstance().sendTo(player);
      player.awardRecipes(RecipeController.instance.getKnownRecipes());
   }

}
