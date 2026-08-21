package noppes.npcs.command;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.CustomNpcs;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.PlayerQuestController;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.controllers.data.QuestCategory;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketSync;
import noppes.npcs.api.CommandNoppesBase;
import noppes.npcs.api.handler.data.IQuestObjective;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.util.ValueUtil;

import javax.annotation.Nonnull;

public class CmdQuest extends CommandNoppesBase {

	@Override
	public int getRequiredPermissionLevel() { return CustomNpcs.NoppesCommandOpOnly ? 4 : 2; }

	@Override
	public String getDescription() { return "Quest operations"; }

	@Override
	public @Nonnull String getName() { return "quest"; }

	@SubCommand(desc = "Finish a quest", usage = "<player> <quest>", isOpOnly = true)
	public void finish(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		String playername = args[0];
		Quest quest = getQuest(args[1]);
		if (quest == null) { throw new CommandException("Unknown Quest ID \"" + args[1] + "\""); }
		List<PlayerData> players = PlayerDataController.instance.getPlayersData(sender, playername);
		if (players.isEmpty()) { throw new CommandException("Unknown player '"+playername+"'");}
		for (PlayerData data : players) {
			data.questData.finish(quest, data.player);
			data.save(true);
		}
	}

	@SubCommand(desc = "get/set objectives for quests progress", usage = "<player> <quest> [objective] [value]", isOpOnly = true)
	public void objective(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		EntityPlayer player = CommandBase.getPlayer(server, sender, args[0]);
		Quest quest = getQuest(args[1]);
		if (quest == null) { throw new CommandException("Unknown Quest ID \"" + args[1] + "\""); }
		PlayerData data = PlayerData.get(player);
		if (!data.questData.activeQuests.containsKey(quest.id)) { throw new CommandException("Player doesnt have quest active ID:" + quest.id); }
		IQuestObjective[] objectives = quest.questInterface.getObjectives(player);
		if (args.length == 2) {
			for (IQuestObjective ob : objectives) { sender.sendMessage(ob.getMCText()); }
			return;
		}
		// objective
		int objective;
		try { objective = Integer.parseInt(args[2]); }
		catch (NumberFormatException ex2) { throw new CommandException("Objective must be an integer. Most often 0, 1 or 2. (" + args[2] + ")"); }
		if (objective < 0 || objective >= objectives.length) { throw new CommandException("Invalid objective number was given ( 0<>" + (objectives.length - 1) + ")"); }
		if (args.length == 3) {
			sender.sendMessage(objectives[objective].getMCText());
			return;
		}
		// value
		IQuestObjective object = objectives[objective];
		String s = args[3];
		int value;
		try { value = Integer.parseInt(args[3]); }
		catch (NumberFormatException ex3) { throw new CommandException("Value must be an integer. (" + args[3] + ")"); }
		if (s.startsWith("-") || s.startsWith("+")) {
			value = ValueUtil.correctInt(object.getProgress() + value, 0, object.getMaxProgress());
		}
		object.setProgress(value);
	}

	@SubCommand(desc = "reload quests from disk", isOpOnly = true)
	public void reload(MinecraftServer server, ICommandSender sender, String[] args) {
		new QuestController().load();
		for (QuestCategory category : QuestController.instance.categories.values()) {
			Packets.sendAll(new PacketSync(3, category.save(new NBTTagCompound()), false));
		}
		Packets.sendAll(new PacketSync(3, new NBTTagCompound(), true));
	}

	@SubCommand(desc = "Removes a quest from finished and active quests", usage = "<player> <quest>", isOpOnly = true)
	public void remove(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		String playername = args[0];
		Quest quest = getQuest(args[1]);
		if (quest == null) { throw new CommandException("Unknown Quest ID \"" + args[1] + "\""); }
		List<PlayerData> players = PlayerDataController.instance.getPlayersData(sender, playername);
		if (players.isEmpty()) { throw new CommandException("Unknown player '"+playername+"'");}
		for (PlayerData data : players) {
			data.questData.activeQuests.remove(quest.id);
			data.questData.removeFinishedQuest(quest.id);
			data.save(true);
		}
	}

	@SubCommand(desc = "Start a quest", usage = "<player> <quest>", isOpOnly = true)
	public void start(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		String playername = args[0];
		Quest quest = getQuest(args[1]);
		if (quest == null) { throw new CommandException("Unknown Quest ID \"" + args[1] + "\""); }
		EntityPlayerMP player = CommandBase.getPlayer(server, sender, playername);
		PlayerQuestController.addActiveQuest(quest, player, true);
		sender.sendMessage(Component.literal("Player \"" + player.getName() + "\" started the quest ID: " + quest.id).getParent());
	}

	@SubCommand(desc = "Stop a started quest", usage = "<player> <quest>", isOpOnly = true)
	public void stop(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		String playername = args[0];
		Quest quest = getQuest(args[1]);
		if (quest == null) { throw new CommandException("Unknown Quest ID \"" + args[1] + "\""); }
		List<PlayerData> data = PlayerDataController.instance.getPlayersData(sender, playername);
		if (data.isEmpty()) { throw new CommandException("Unknown player '"+playername+"'"); }
		for (PlayerData playerdata : data) {
			playerdata.questData.activeQuests.remove(quest.id);
			playerdata.save(true);
		}
	}

	private Quest getQuest(String questIdOrName) {
		try {
			int id = Integer.parseInt(questIdOrName);
			return QuestController.instance.get(id);
		}
		catch (Exception ignored) {  }
		return QuestController.instance.getQuestFromName(questIdOrName);
	}

	@Override
	public @Nonnull List<String> getTabCompletions(@Nonnull MinecraftServer server, @Nonnull ICommandSender par1, @Nonnull String[] args, BlockPos pos) {
		if (args.length == 2) {
			List<String> list = new ArrayList<>();
			for (Quest quest : QuestController.instance.quests.values()) {
				list.add("" + quest.id);
				list.add(quest.getName());
			}
			return list;
		}
		return new ArrayList<>();
	}

}
