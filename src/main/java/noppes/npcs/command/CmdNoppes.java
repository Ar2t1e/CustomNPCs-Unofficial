package noppes.npcs.command;

import com.mojang.brigadier.CommandDispatcher;

import java.util.ArrayList;
import java.util.List;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import noppes.npcs.CustomEntities;
import noppes.npcs.CustomNpcs;
import noppes.npcs.command.arguments.PlayerDataArgument;
import noppes.npcs.command.arguments.URLArgument;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.entity.EntityNPCInterface;

public class CmdNoppes {

   // New from Unofficial (BetaZavr) -> Arguments
   public static final DeferredRegister<ArgumentTypeInfo<?, ?>> ARGUMENT_TYPES = DeferredRegister.create(ForgeRegistries.COMMAND_ARGUMENT_TYPES, CustomNpcs.MODID);

   public static void registerArguments(IEventBus eventBus) {
      ARGUMENT_TYPES.register("skin_url", () -> ArgumentTypeInfos.registerByClass(URLArgument.class, SingletonArgumentInfo.contextFree(URLArgument::urlArg)));
      ARGUMENT_TYPES.register("custom_data", () -> ArgumentTypeInfos.registerByClass(PlayerDataArgument.class, SingletonArgumentInfo.contextFree(PlayerDataArgument::dataArg)));
      ARGUMENT_TYPES.register(eventBus);
   }

   public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
      // Commands
      dispatcher.register(Commands.literal("noppes")
              .requires((commandSourceStack) -> commandSourceStack.hasPermission(2))
              .then(CmdClone.register())
              .then(CmdConfig.register())
              .then(CmdDialog.register())
              .then(CmdFaction.register())
              .then(CmdMark.register())
              .then(CmdNPC.register())
              .then(CmdQuest.register())
              .then(CmdScene.register())
              .then(CmdSchematics.register())
              .then(CmdScript.register())
              .then(CmdSlay.register())
              // New from Unofficial (BetaZavr)
              .then(CmdPlayer.register())
              .then(CmdWorld.register())
              .then(CmdPermissions.register())
      );
      // New from Unofficial (BetaZavr)
      dispatcher.register(CmdMoney.register());
   }

   @SuppressWarnings("unchecked")
   public static List<EntityNPCInterface> getNpcsByName(ServerLevel level, String name) {
      return (List<EntityNPCInterface>) level.getEntities(CustomEntities.entityCustomNpc, (npc) -> npc.display.getName().equalsIgnoreCase(name));
   }

   @SuppressWarnings("unchecked")
   public static <T extends Entity> List<T> getEntities(EntityType<T> type, ServerLevel level) {
      return (List<T>) level.getEntities(type, (entity) -> true);
   }

}
