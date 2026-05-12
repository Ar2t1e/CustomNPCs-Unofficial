package noppes.npcs.command;

import java.util.*;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.DimensionManager;
import noppes.npcs.CustomNpcs;
import noppes.npcs.EventHooks;
import noppes.npcs.ForgeEventHandler;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketEventNames;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.api.CommandNoppesBase;
import noppes.npcs.api.IPos;
import noppes.npcs.api.IWorld;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.event.WorldEvent;
import noppes.npcs.blocks.tiles.TileScripted;
import noppes.npcs.constants.EnumScriptType;
import noppes.npcs.controllers.ScriptContainer;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.dimensions.DimensionHandler;

import javax.annotation.Nonnull;

public class CmdScript extends CommandNoppesBase {

	@Override
	public int getRequiredPermissionLevel() { return CustomNpcs.NoppesCommandOpOnly ? 4 : 2; }

	@Override
	public String getDescription() { return "Commands for scripts"; }

	@Nonnull
	public String getName() { return "script"; }

	@SubCommand(desc = "List of available event names from all APIs in mod", permission = 4)
	public Boolean apilist(MinecraftServer server, ICommandSender sender, String[] args) {
		Component message = Component.empty();
		List<String> g = new ArrayList<>();
		for (EnumScriptType est : EnumScriptType.values()) { g.add(est.function); }
		Collections.sort(g);
		boolean start = true;
		for (String name : g) {
			if (start) {
				message.append(Component.literal("Mod APIs event names:\n").withStyle(TextFormatting.GOLD));
				start = false;
			}
			else { message.append(Component.literal(", ").withStyle(TextFormatting.GOLD)); }
			message.append(Component.literal(name).withStyle(TextFormatting.RESET));
		}
		message.append(Component.literal(";\n").withStyle(TextFormatting.GOLD))
				.append(Component.literal("Total Size: ").withStyle(TextFormatting.GOLD))
				.append(Component.literal("" + g.size()).withStyle(TextFormatting.YELLOW));
		sender.sendMessage(message);
		if (sender instanceof EntityPlayerMP) {
			Map<String, String> names = new HashMap<>();
			for (EnumScriptType est : EnumScriptType.values()) { names.put(est.function, ""); }
			Packets.send((EntityPlayerMP) sender, new PacketEventNames(names, (byte) 2));
		}
		return true;
	}

	@SubCommand(desc = "List of available Forge event names", permission = 4)
	public Boolean clientlist(MinecraftServer server, ICommandSender sender, String[] args) {
		Component message = Component.empty();
		List<String> g = new ArrayList<>(ForgeEventHandler.clientEventNames.values());
		Collections.sort(g);
		boolean start = true;
		for (String name : g) {
			if (start) {
				message.append(Component.literal("Client forge event names:\n").withStyle(TextFormatting.GOLD));
				start = false;
			}
			else { message.append(Component.literal(", ").withStyle(TextFormatting.GOLD)); }
			message.append(Component.literal(name).withStyle(TextFormatting.RESET));
		}
		message.append(Component.literal(";\n").withStyle(TextFormatting.GOLD))
				.append(Component.literal("Total Size: ").withStyle(TextFormatting.GOLD))
				.append(Component.literal("" + g.size()).withStyle(TextFormatting.YELLOW));
		sender.sendMessage(message);
		if (sender instanceof EntityPlayerMP) {
			Map<String, String> names = new HashMap<>();
			for (Map.Entry<Class<?>, String> entry : ForgeEventHandler.clientEventNames.entrySet()) {
				names.put(entry.getKey().getName(), entry.getValue());
			}
			Packets.send((EntityPlayerMP) sender, new PacketEventNames(names, (byte) 0));
		}
		return true;
	}

	@SubCommand(desc = "List of available Forge event names", permission = 4)
	public Boolean forgelist(MinecraftServer server, ICommandSender sender, String[] args) {
		Component message = Component.empty();
		List<String> g = new ArrayList<>(ForgeEventHandler.eventNames.values());
		Collections.sort(g);
		boolean start = true;
		for (String name : g) {
			if (start) {
				message.append(Component.literal("Server forge event names:\n").withStyle(TextFormatting.GOLD));
				start = false;
			}
			else { message.append(Component.literal(", ").withStyle(TextFormatting.GOLD)); }
			message.append(Component.literal(name).withStyle(TextFormatting.RESET));
		}
		message.append(Component.literal(";\n").withStyle(TextFormatting.GOLD))
				.append(Component.literal("Total Size: ").withStyle(TextFormatting.GOLD))
				.append(Component.literal("" + g.size()).withStyle(TextFormatting.YELLOW));
		sender.sendMessage(message);
		if (sender instanceof EntityPlayerMP) {
			Map<String, String> names = new HashMap<>();
			for (Map.Entry<Class<?>, String> entry : ForgeEventHandler.eventNames.entrySet()) {
				names.put(entry.getKey().getName(), entry.getValue());
			}
			Packets.send((EntityPlayerMP) sender, new PacketEventNames(names, (byte) 1));
		}
		return true;
	}

	@SubCommand(desc = "Displays all script owners that have logs.", permission = 4)
	public Boolean logs(MinecraftServer server, ICommandSender sender, String[] args) {
		List<Component> list = new ArrayList<>();
	 	for (ScriptContainer container : ScriptController.Instance.getErrored()) { list.add(container.noticeString()); }
		if (list.isEmpty()) {
			sender.sendMessage(Component.translatable("command.script.logs.empty"));
		} else {
			sender.sendMessage(Component.translatable("command.script.logs.info"));
			for (Component message : list) { sender.sendMessage(message); }
		}
		sender.sendMessage(Component.translatable("command.script.logs.end"));
		return true;
	}

	@SubCommand(desc = "Reload scripts and saved data from disks script folder.", permission = 4)
	public Boolean reload(MinecraftServer server, ICommandSender sender, String[] args) {
		ScriptController.Instance.loadCategories();
		// Players
		if (ScriptController.Instance.loadPlayerScripts()) { sender.sendMessage(Component.literal("Reload player scripts successfully")); }
		else { sender.sendMessage(Component.literal("Failed reloading player scripts")); }
		// NPCs
		if (ScriptController.Instance.loadNPCsScripts()) { sender.sendMessage(Component.literal("Reload NPCs scripts successfully")); }
		else { sender.sendMessage(Component.literal("Failed reloading NPCs scripts")); }
		// Forge
		if (ScriptController.Instance.loadForgeScripts()) { sender.sendMessage(Component.literal("Reload forge scripts successfully")); }
		else { sender.sendMessage(Component.literal("Failed reloading forge scripts")); }
		// Clients
		if (ScriptController.Instance.loadClientScripts()) { sender.sendMessage(Component.literal("Reload client scripts successfully")); }
		else { sender.sendMessage(Component.literal("Failed reloading client scripts")); }
		// Potions
		if (ScriptController.Instance.loadPotionScripts()) { sender.sendMessage(Component.literal("Reload potion scripts successfully")); }
		else { sender.sendMessage(Component.literal("Failed reloading potion scripts")); }
		// Constants data
		if (ScriptController.Instance.loadConstantData()) { sender.sendMessage(Component.literal("Reload constant data successfully")); }
		else { sender.sendMessage(Component.literal("Failed reloading constant data")); }
		// Stored data
		if (ScriptController.Instance.loadStoredData()) { sender.sendMessage(Component.literal("Reload stored data successfully")); }
		else { sender.sendMessage(Component.literal("Failed reloading stored data")); }
		// Client data
		for (EntityPlayerMP player : server.getPlayerList().getPlayers()) { ScriptController.Instance.sendClientTo(player); }
		return true;
	}

	@SubCommand(desc = "Runs scriptCommand in the players scripts", usage = "[args]", permission = 4)
	public Boolean run(MinecraftServer server, ICommandSender sender, String[] args) {
		IWorld world = Objects.requireNonNull(NpcAPI.Instance()).getIWorld(sender.getEntityWorld());
		BlockPos bpos = sender.getPosition();
		IPos pos = Objects.requireNonNull(NpcAPI.Instance()).getIPos(bpos.getX(), bpos.getY(), bpos.getZ());
		WorldEvent.ScriptCommandEvent event = new WorldEvent.ScriptCommandEvent(world, pos, args);
		EventHooks.onWorldScriptEvent(event);
		return true;
	}

	@SubCommand(desc = "Attempts to execute on the specified object", usage = "<dimensionID> <x> <y> <z> <entity> <triggerID> [Strings]", permission = 4)
	public Boolean trigger(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		IWorld world;
		IPos pos = null;
		IEntity<?> entity = null;
		int id;
		try {
			int dimID = Integer.parseInt(args[0]);
			if (!DimensionManager.isDimensionRegistered(dimID) || DimensionHandler.getInstance().isDelete(dimID)) {
				throw new CommandException("DimensionID: " + dimID + " - not found");
			}
			world = Objects.requireNonNull(NpcAPI.Instance()).getIWorld(dimID);
		} catch (NumberFormatException ex) {
			throw new CommandException("DimensionID \"" + args[0] + "\" - must be an integer ");
		}
		try {
			double dx = parseCoordinate(sender.getPosition().getX(), args[1], true).getResult();
			double dy = parseCoordinate(sender.getPosition().getY(), args[2], 0, 255, false).getResult();
			double dz = parseCoordinate(sender.getPosition().getZ(), args[3], true).getResult();
			pos = Objects.requireNonNull(NpcAPI.Instance()).getIPos(dx, dy, dz);
		} catch (NumberFormatException e) { LogWriter.error(e); }
		IEntity<?>[] entitys = world.getNearbyEntities(pos, 2, 0);
		for (IEntity<?> e : entitys) {
			if (args[4].equalsIgnoreCase("player") && e.getType() == 1 || e.getName().equalsIgnoreCase(args[4])) {
				entity = e;
				break;
			}
		}
		try {
			id = Integer.parseInt(args[5]);
		} catch (NumberFormatException ex) {
			throw new CommandException("TriggerID \"" + args[0] + "\" must be an integer");
		}
		Object[] arguments = new String[args.length - 6];
		System.arraycopy(args, 6, arguments, 0, args.length - 6);
		if (entity == null) {
			assert pos != null;
			TileEntity tile = world.getMCWorld().getTileEntity(pos.getMCBlockPos());
			if (tile instanceof TileScripted) {
				EventHooks.onScriptTriggerEvent((TileScripted) tile, id, world, pos, null, arguments);
				return true;
			}
		}
		EventHooks.onScriptTriggerEvent(id, world, pos, entity, arguments);
		return true;
	}

	@SubCommand(desc = "Display a list of all load script elements positions in chat", permission = 4)
	public Boolean list(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		Component positions;
		String key = args.length > 0 ? args[0] : "all";
		switch (key) {
			case "blocks":
				positions = ScriptController.Instance.getElements(0);
				if (positions != null) {
					sender.sendMessage(Component.translatable("script.command.blocks"));
					sender.sendMessage(positions);
				}
				break;
			case "doors":
				positions = ScriptController.Instance.getElements(1);
				if (positions != null) {
					sender.sendMessage(Component.translatable("script.command.doors"));
					sender.sendMessage(positions);
				}
				break;
			case "npcs":
				positions = ScriptController.Instance.getElements(2);
				if (positions != null) {
					sender.sendMessage(Component.translatable("script.command.npcs"));
					sender.sendMessage(positions);
				}
				break;
			case "all":
				sender.sendMessage(Component.translatable("script.command.all"));
				positions = ScriptController.Instance.getElements(0);
				if (positions != null) {
					sender.sendMessage(Component.translatable("script.command.blocks"));
					sender.sendMessage(positions);
				}
				positions = ScriptController.Instance.getElements(1);
				if (positions != null) {
					sender.sendMessage(Component.translatable("script.command.doors"));
					sender.sendMessage(positions);
				}
				positions = ScriptController.Instance.getElements(2);
				if (positions != null) {
					sender.sendMessage(Component.translatable("script.command.npcs"));
					sender.sendMessage(positions);
				}
				break;
			default:
				throw new CommandException("Unknown type \""+key+"\"");
		}
		if (positions == null) {
			sender.sendMessage(Component.translatable("script.command.not.found"));
		}
		return true;
	}

	@Override
	public @Nonnull List<String> getTabCompletions(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args, BlockPos pos) {
		List<String> list = new ArrayList<>();
		if (args.length == 2) {
            switch (args[0]) {
                case "clientlist":
                    return new ArrayList<>(ForgeEventHandler.clientEventNames.values());
                case "forgelist":
                    return new ArrayList<>(ForgeEventHandler.eventNames.values());
                case "apilist":
                    for (EnumScriptType est : EnumScriptType.values()) { list.add(est.function); }
                    break;
				case "list":
					list.add("blocks");
					list.add("doors");
					list.add("npcs");
					list.add("all");
					break;
            }
		}
		return list;
	}

}
