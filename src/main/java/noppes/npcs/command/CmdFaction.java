package noppes.npcs.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.CommandNoppesBase;
import noppes.npcs.controllers.FactionController;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.data.Faction;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerFactionData;

import javax.annotation.Nonnull;

public class CmdFaction extends CommandNoppesBase {

	public List<PlayerData> data;
	public Faction selectedFaction;

	@Override
	public int getRequiredPermissionLevel() { return CustomNpcs.NoppesCommandOpOnly ? 4 : 2; }

	@SubCommand(desc = "Add points", usage = "<points>", isOpOnly = true)
	public void add(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		int points;
		try {
			points = Integer.parseInt(args[0]);
		} catch (NumberFormatException ex) {
			throw new CommandException(args[0]+" must be an integer");
		}
		int factionId = selectedFaction.id;
		for (PlayerData playerdata : data) {
			PlayerFactionData playerfactiondata = playerdata.factionData;
			playerfactiondata.increasePoints(playerdata.player, factionId, points);
			sender.sendMessage(new TextComponentString(points + " points added to player \""+playerdata.name+"\" Faction ID: "+factionId));
			playerdata.save(true);
		}
	}

	@SubCommand(desc = "Drop relationship", permission = 2)
	public void drop(MinecraftServer server, ICommandSender sender, String[] args) {
		for (PlayerData playerdata : data) {
			playerdata.factionData.factionData.remove(selectedFaction.id);
			sender.sendMessage(new TextComponentString("Player \""+playerdata.name+"\" has Faction ID: "+selectedFaction.id+" removed"));
			playerdata.save(true);
		}
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args == null) { return; }
		String playername = args[0];
		String factionName = args[1];
		data = PlayerDataController.instance.getPlayersData(sender, playername);
		if (data.isEmpty()) {
			throw new CommandException("Unknown player " + playername);
		}
		try {
			selectedFaction = FactionController.instance.getFaction(Integer.parseInt(factionName));
		} catch (NumberFormatException e) {
			selectedFaction = FactionController.instance.getFactionFromName(factionName);
		}
		if (selectedFaction == null) { throw new CommandException("Unknown FactionID \"" + factionName + "\""); }
		executeSub(server, sender, args[2], Arrays.copyOfRange(args, 3, args.length));
	}

	@Override
	public String getDescription() { return "Faction operations"; }

	@Nonnull
	public String getName() { return "faction"; }

	@Override
	public String getUsage() { return "<player> <faction> <command>"; }

	@SubCommand(desc = "Reset points to default", isOpOnly = true)
	public void reset(MinecraftServer server, ICommandSender sender, String[] args) {
		for (PlayerData playerdata : data) {
			playerdata.factionData.factionData.put(selectedFaction.id, selectedFaction.defaultPoints);
			playerdata.save(true);
		}
	}

	@Override
	public boolean runSubCommands() {
		return false;
	}

	@SubCommand(desc = "Set points", usage = "<points>", isOpOnly = true)
	public void set(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		int points;
		try {
			points = Integer.parseInt(args[0]);
		} catch (NumberFormatException ex) {
			throw new CommandException(args[0]+" - must be an integer");
		}
		for (PlayerData playerdata : data) {
			PlayerFactionData playerfactiondata = playerdata.factionData;
			playerfactiondata.factionData.put(selectedFaction.id, points);
			playerdata.save(true);
		}
	}

	@SubCommand(desc = "Subtract points", usage = "<points>", isOpOnly = true)
	public void subtract(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		int points;
		try {
			points = Integer.parseInt(args[0]);
		} catch (NumberFormatException ex) {
			throw new CommandException("Must be " + "an integer");
		}
		int factionId = selectedFaction.id;
		for (PlayerData playerdata : data) {
			PlayerFactionData playerfactiondata = playerdata.factionData;
			playerfactiondata.increasePoints(playerdata.player, factionId, -points);
			playerdata.save(true);
		}
	}

	@Override
	public @Nonnull List<String> getTabCompletions(@Nonnull MinecraftServer server, @Nonnull ICommandSender par1, @Nonnull String[] args, BlockPos pos) {
		if (args.length == 2) {
			List<String> list = new ArrayList<>();
			for (Faction faction : FactionController.instance.factions.values()) {
				list.add("" + faction.id);
				list.add(faction.getName());
			}
			return list;
		}
		if (args.length == 3) {
			return CommandBase.getListOfStringsMatchingLastWord(args, "add", "subtract", "set", "reset", "drop", "create");
		}
		return new ArrayList<>();
	}

}
