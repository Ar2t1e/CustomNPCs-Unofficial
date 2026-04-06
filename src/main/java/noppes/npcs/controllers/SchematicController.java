package noppes.npcs.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.level.Level;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.schematics.Blueprint;
import noppes.npcs.schematics.BlueprintUtil;
import noppes.npcs.schematics.ISchematic;
import noppes.npcs.schematics.Schematic;
import noppes.npcs.schematics.SchematicWrapper;
import noppes.npcs.schematics.SpongeSchem;
import noppes.npcs.shared.common.CommonUtil;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.util.Util;

import javax.annotation.Nullable;

public class SchematicController {

   public static final SchematicController Instance = new SchematicController();
   public static final List<String> included = Arrays.asList("archery_range.schematic", "bakery.schematic", "barn.schematic", "building_site.schematic",
           "chapel.schematic", "church.schematic", "gate.schematic", "glassworks.schematic", "guard_tower.schematic", "guild_house.schematic",
           "house.schematic", "house_small.schematic", "inn.schematic", "library.schematic", "lighthouse.schematic", "mill.schematic", "observatory.schematic",
           "ship.schematic", "shop.schematic", "stall.schematic", "stall2.schematic", "stall3.schematic", "tier_house1.schematic", "tier_house2.schematic",
           "tier_house3.schematic", "tower.schematic", "wall.schematic", "wall_corner.schematic");

   public static long time = 50L;

   // New from Unofficial (BetaZavr)
   private final List<SchematicWrapper> buildingList = new ArrayList<>();
   public final Map<String, SchematicWrapper> map = new HashMap<>(); // OLD -> private SchematicWrapper building = null;

   public List<String> list() {
      List<String> list = new ArrayList<>(included);
      File dir = getDir();
      if (dir != null) {
         File[] files = dir.listFiles();
         if (files != null) {
            for (File file : files) {
               String name = NoppesUtilServer.validPath(file.getName());
               if (name.toLowerCase().endsWith(".schematic") || name.toLowerCase().endsWith(".schem") || name.toLowerCase().endsWith(".blueprint")) {
                  list.add(name);
               }
            }
         }
      }
      Collections.sort(list);
      return list;
   }

   // New from Unofficial (BetaZavr)
   public static void buildBlocks(ServerPlayer player, BlockPos pos, int rotation, SchematicWrapper wrapper) { // Schematic
      if (player != null && pos != null && wrapper != null) {
         long ticks = 3000L + wrapper.size * SchematicController.time + (long) Math.floor((double) wrapper.size / CustomNpcs.MaxBuilderBlocks) * 1000L;
         player.sendSystemMessage(Component.translatable("schematic.info.started", wrapper.schema.getName(),
                 "" + pos.getX(), "" + pos.getY(), "" + pos.getZ(), player.level().dimension().location().toString(),
                 Util.instance.ticksToElapsedTime(ticks, true, true, false)));
         SchematicController.Instance.build(wrapper, player.createCommandSourceStack());
      }
   }

   public static @Nullable File getDir() {
      File schematicDir = CustomNpcs.getLevelSaveDirectory( "schematics");
      File saveDir = CustomNpcs.getLevelSaveDirectory();
      while (saveDir != null && saveDir.getParentFile() != null) {
         saveDir = saveDir.getParentFile();
         if ((new File(saveDir, "config")).exists()) {
            schematicDir = new File(saveDir, "schematics");
            break;
         }
      }
      if (schematicDir != null && (schematicDir.exists() || schematicDir.mkdir())) { return schematicDir; }
      return null;
   }

   public void build(SchematicWrapper schema, CommandSourceStack sender) {
      if (schema == null) {
         sendMessage(sender, Component.translatable("schematic.info.notbuild"));
         return;
      }
      if (buildingList.contains(schema)) {
         sendMessage(sender, Component.translatable("schematic.info.already",
                 Component.literal(schema.schema.getName()).withStyle(ChatFormatting.GRAY),
                 Component.literal(schema.getPercentage() + "%").withStyle(ChatFormatting.GRAY)));
         if (schema.sender != null) {
            sendMessage(sender, Component.translatable("schematic.info.start.name",
                    Component.literal(schema.sender.getDisplayName().getString()).withStyle(ChatFormatting.GRAY)));
         }
         return;
      }
      schema.setBuilder(sender);
      buildingList.add(schema);
   }

   public void info(CommandSourceStack sender) {
      if (buildingList.isEmpty()) {
         sendMessage(sender, Component.translatable("schematic.info.empty"));
      } else {
         for (SchematicWrapper sm : buildingList) {
            sendMessage(sender, Component.translatable("schematic.info.0",
                    Component.literal(sm.schema.getName()).withStyle(ChatFormatting.GRAY),
                    Component.literal(sm.getPercentage() + "%").withStyle(ChatFormatting.GRAY),
                    Component.translatable(sm.sender == null ? "" : "schematic.info.1").withStyle(ChatFormatting.GRAY)));
         }
      }

   }

   private void sendMessage(CommandSourceStack sender, Component message) {
      if (sender != null) { sender.sendSuccess(() -> message, false); }
   }

   public void stop(CommandSourceStack sender) {
      if (buildingList.isEmpty()) {
         sendMessage(sender, Component.translatable("schematic.info.build.empty"));
      } else {
         StringBuilder smts = new StringBuilder();
         for (SchematicWrapper sm : buildingList) {
            if (!smts.isEmpty()) { smts.append(";" + ((char) 10)); }
            smts.append(((char) 167)).append("7\"").append(sm.schema.getName()).append("\" in [")
                    .append(sm.start.getX()).append(", ").append(sm.start.getY()).append(", ").append(sm.start.getZ()).append("]");
         }
         sendMessage(sender, Component.translatable("schematic.info.build.stop", smts.toString()));
         buildingList.clear();
      }
   }

   public void updateBuilding() {
      if (buildingList.isEmpty()) { return; }
      CustomNpcs.debugData.start(null);
      List<SchematicWrapper> del = new ArrayList<>();
      for (SchematicWrapper wrapper : buildingList) {
         wrapper.build();
         if (wrapper.sender != null && wrapper.getPercentage() - wrapper.buildingPercentage >= 10) {
            sendMessage(wrapper.sender, Component.translatable("schematic.info.build.percentage",
                            Component.literal(wrapper.schema.getName()).withStyle(ChatFormatting.GRAY),
                            Component.literal(wrapper.getPercentage() + "%").withStyle(ChatFormatting.GRAY)));
            wrapper.buildingPercentage = wrapper.getPercentage();
         }
         if (!wrapper.isBuilding) {
            if (wrapper.sender != null) {
               if (wrapper.schema.hasEntitys()) {
                  sendMessage(wrapper.sender, Component.translatable("schematic.info.spawn.entitys",
                          Component.literal(wrapper.schema.getName()).withStyle(ChatFormatting.GRAY)));
               }
               sendMessage(wrapper.sender, Component.translatable("schematic.info.build.finish",
                       Component.literal(wrapper.schema.getName()).withStyle(ChatFormatting.GRAY)));
            }
            del.add(wrapper);
         }
      }
      for (SchematicWrapper sm : del) { buildingList.remove(sm); }
      CustomNpcs.debugData.end(null);
   }

   public SchematicWrapper load(String name) {
      InputStream stream = null;
      if (included.contains(name)) {
         ResourceLocation resource = new ResourceLocation(CustomNpcs.MODID, "schematics/" + name);
         Resource ir = CustomNpcs.Server.getServerResources().resourceManager().getResource(resource).orElse(null);
         if (ir != null) {
            try { stream = ir.open(); }
            catch (IOException ignored) {}
         }
      }
      if (stream == null) {
         File file = new File(getDir(), name);
         if (!file.exists()) { return null; }
         try { stream = new FileInputStream(file); }
         catch (FileNotFoundException var7) { return null; }
      }
      try {
         CompoundTag compound = NbtIo.readCompressed(stream);
         stream.close();
         if (name.toLowerCase().endsWith(".schem")) {
            SpongeSchem bp = new SpongeSchem(name);
            bp.load(compound);
            return new SchematicWrapper(bp);
         }
         else if (name.toLowerCase().endsWith(".blueprint")) {
            Blueprint bp = BlueprintUtil.readBlueprintFromNBT(compound);
            if (bp != null) {
               bp.setName(name);
               return new SchematicWrapper(bp);
            }
            return null;
         }
         else {
            Schematic schema = new Schematic(name);
            schema.load(compound);
            return new SchematicWrapper(schema);
         }
      } catch (IOException var6) {
         LogWriter.except(var6);
         return null;
      }
   }

   public void save(CommandSourceStack sender, String name, int type, BlockPos pos, short height, short width, short length) {
      CustomNpcs.debugData.start(null);
      name = name.replace(" ", "_");
      if (!included.contains(name)) {
         Level level = sender.getLevel();
         File file = null;
         ISchematic schema = null;
         if (type == 0) {
            file = new File(SchematicController.getDir(), name + ".schematic");
            schema = SpongeSchem.create(level, name, pos, height, width, length);
         } else if (type == 1) {
            file = new File(SchematicController.getDir(), name + ".blueprint");
            schema = BlueprintUtil.createBlueprint(level, pos, width, length, height);
         }
         CommonUtil.NotifyOPs(Component.literal("Schematic " + name + " successfully created").withStyle(ChatFormatting.GRAY), false);
         try {
            if (schema != null) {
               NbtIo.writeCompressed(schema.getNBT(), Files.newOutputStream(file.toPath()));
            }
         } catch (Exception e) { LogWriter.error(e); }
      }
      CustomNpcs.debugData.end(null);
   }

}
