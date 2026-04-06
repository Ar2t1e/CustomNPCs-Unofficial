package noppes.npcs.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.ChunkController;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketConfigFont;
import noppes.npcs.packets.client.PacketDebug;
import noppes.npcs.util.CustomNPCsScheduler;

import java.util.List;

public class CmdConfig {

   public static LiteralArgumentBuilder<CommandSourceStack> register() {
      LiteralArgumentBuilder<CommandSourceStack> command = Commands.literal("config");
      command.then(Commands.literal("leavesdecay").requires((source) -> source.hasPermission(4)).executes((context) -> {
                 context.getSource().sendSuccess(() -> Component.literal("LeavesDecay: " + CustomNpcs.LeavesDecayEnabled), false);
                 return 1;
              })
              .then(Commands.argument("boolean", BoolArgumentType.bool()).executes((context) -> {
                 CustomNpcs.LeavesDecayEnabled = BoolArgumentType.getBool(context, "boolean");
                 CustomNpcs.Config.updateConfig();
                 context.getSource().sendSuccess(() -> Component.literal("LeavesDecay: " + CustomNpcs.LeavesDecayEnabled), false);
                 return 1;
              })));
      command.then(Commands.literal("vineinflateth").requires((source) -> source.hasPermission(4)).executes((context) -> {
         context.getSource().sendSuccess(() -> Component.literal("VineGrowth: " + CustomNpcs.VineGrowthEnabled), false);
         return 1;
      }).then(Commands.argument("boolean", BoolArgumentType.bool()).executes((context) -> {
         CustomNpcs.VineGrowthEnabled = BoolArgumentType.getBool(context, "boolean");
         CustomNpcs.Config.updateConfig();
         context.getSource().sendSuccess(() -> Component.literal("VineGrowth: " + CustomNpcs.VineGrowthEnabled), false);
         return 1;
      })));
      command.then(Commands.literal("icemelts").requires((source) -> source.hasPermission(4)).executes((context) -> {
         context.getSource().sendSuccess(() -> Component.literal("IceMelts: " + CustomNpcs.IceMeltsEnabled), false);
         return 1;
      }).then(Commands.argument("boolean", BoolArgumentType.bool()).executes((context) -> {
         CustomNpcs.IceMeltsEnabled = BoolArgumentType.getBool(context, "boolean");
         CustomNpcs.Config.updateConfig();
         context.getSource().sendSuccess(() -> Component.literal("IceMelts: " + CustomNpcs.IceMeltsEnabled), false);
         return 1;
      })));
      command.then(Commands.literal("freezenpcs").requires((source) -> source.hasPermission(4)).executes((context) -> {
         context.getSource().sendSuccess(() -> Component.literal("Frozen NPCs: " + CustomNpcs.FreezeNPCs), false);
         return 1;
      }).then(Commands.argument("boolean", BoolArgumentType.bool()).executes((context) -> {
         CustomNpcs.FreezeNPCs = BoolArgumentType.getBool(context, "boolean");
         context.getSource().sendSuccess(() -> Component.literal("Frozen NPCs: " + CustomNpcs.FreezeNPCs), false);
         return 1;
      })));
      command.then(Commands.literal("debug").requires((source) -> source.hasPermission(4)).executes((context) -> {
         context.getSource().sendSuccess(() -> Component.literal("Verbose debug is " + CustomNpcs.VerboseDebug), false);
         return 1;
      }).then(Commands.argument("boolean", BoolArgumentType.bool()).executes((context) -> {
         CustomNpcs.VerboseDebug = BoolArgumentType.getBool(context, "boolean");
         context.getSource().sendSuccess(() -> Component.literal("Verbose debug is now " + CustomNpcs.VerboseDebug), false);
         return 1;
      })));
      command.then(Commands.literal("scripting").requires((source) -> source.hasPermission(4)).executes((context) -> {
         context.getSource().sendSuccess(() -> Component.literal("Scripting is " + CustomNpcs.EnableScripting), false);
         return 1;
      }).then(Commands.argument("boolean", BoolArgumentType.bool()).executes((context) -> {
         CustomNpcs.EnableScripting = BoolArgumentType.getBool(context, "boolean");
         CustomNpcs.Config.updateConfig();
         context.getSource().sendSuccess(() -> Component.literal("Scripting is now" + CustomNpcs.EnableScripting), false);
         return 1;
      })));
      command.then(Commands.literal("chunkloaders").requires((source) -> source.hasPermission(4)).executes((context) -> {
         context.getSource().sendSuccess(() -> Component.literal("ChunkLoaders: " + ChunkController.instance.size() + "/" + CustomNpcs.ChuckLoaders), false);
         return 1;
      }).then(Commands.argument("number", IntegerArgumentType.integer(0)).executes((context) -> {
         CustomNpcs.ChuckLoaders = IntegerArgumentType.getInteger(context, "number");
         CustomNpcs.Config.updateConfig();
         context.getSource().sendSuccess(() -> Component.literal("Max ChunkLoaders: " + CustomNpcs.ChuckLoaders), false);
         return 1;
      })));
      command.then(Commands.literal("font").requires((source) -> source.hasPermission(2)).executes((context) -> {
         Packets.send(context.getSource().getPlayerOrException(), new PacketConfigFont("", 0));
         return 1;
      }).then(Commands.argument("font", StringArgumentType.string()).executes((context) -> {
         Packets.send(context.getSource().getPlayerOrException(), new PacketConfigFont(StringArgumentType.getString(context, "font"), 18));
         return 1;
      }).then(Commands.argument("size", IntegerArgumentType.integer(0)).executes((context) -> {
         Packets.send(context.getSource().getPlayerOrException(), new PacketConfigFont(StringArgumentType.getString(context, "font"), IntegerArgumentType.getInteger(context, "size")));
         return 1;
      }))));
      // new
      command.then(Commands.literal("report").requires((source) -> source.hasPermission(4)).executes((context) -> {
         List<String> list = CustomNpcs.debugData.logging();
         if (!list.isEmpty()) {
            context.getSource().sendSuccess(() -> Component.literal("Server info:"), false);
            for (String str : list) { context.getSource().sendSuccess(() -> Component.literal(str), false); }
         }
         if (context.getSource().getPlayer() instanceof ServerPlayer && (CustomNpcs.Server == null || !CustomNpcs.Server.isSingleplayer())) {
            context.getSource().sendSuccess(() -> Component.literal("Client info:"), false);
            Packets.send(context.getSource().getPlayer(), new PacketDebug(true));
         }
         CustomNPCsScheduler.runTack(() -> context.getSource().sendSuccess(() -> Component.translatable("command.debug.show"), false), 1000);
         return 1;
      }));
      command.then(Commands.literal("clear").requires((source) -> source.hasPermission(4)).executes((context) -> {
         CustomNpcs.debugData.clear();
         if (context.getSource().getPlayer() instanceof ServerPlayer) {
            Packets.send(context.getSource().getPlayer(), new PacketDebug(false));
         }
         context.getSource().sendSuccess(() -> Component.translatable("command.debug.clear"), false);
         return 1;
      }));
      command.then(Commands.literal("invisiblenpcs")
              .requires((source) -> source.hasPermission(4) && CustomNpcsPermissions.hasPermission(source.getPlayer(), CustomNpcsPermissions.NPC_DISPLAY))
              .executes((context) -> {
         context.getSource().sendSuccess(() -> Component.literal("Invisible NPCs is " + CustomNpcs.EnableInvisibleNpcs), false);
         return 1;
      }).then(Commands.argument("boolean", BoolArgumentType.bool()).executes((context) -> {
         CustomNpcs.EnableInvisibleNpcs = BoolArgumentType.getBool(context, "boolean");
         CustomNpcs.Config.updateConfig();
         context.getSource().sendSuccess(() -> Component.literal("Invisible NPCs is now" + CustomNpcs.EnableInvisibleNpcs), false);
         return 1;
      })));
      return command;
   }
}
