package noppes.npcs.command;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import noppes.npcs.CustomNpcs;
import noppes.npcs.controllers.PlayerSkinController;
import noppes.npcs.controllers.data.SkinData;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.CommandNoppesBase;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.controllers.MarcetController;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.data.Marcet;
import noppes.npcs.controllers.data.PlayerData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CmdPlayers extends CommandNoppesBase {

	@Override
	public String getDescription() { return "Player mod data"; }

	@Nonnull
	@Override
	public String getName() { return "player"; }

	public int getRequiredPermissionLevel() { return 2; }

	private static PlayerData getData(@Nonnull MinecraftServer server, @Nullable String player) throws CommandException {
		if (player == null) { throw new PlayerNotFoundException("commands.generic.player.notFound", "NULL"); }
		PlayerData data = PlayerDataController.instance.getDataFromUsername(server, player);
		if (data == null) { throw new PlayerNotFoundException("commands.generic.player.notFound", player); }
		return data;
	}

	@SubCommand(desc = "Apply to all players", usage = "<action> <action_name>", isOpOnly = true)
	public void all(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length < 2) { throw new CommandException("Usage " + "<action> <action_name>"); }
        if (args[0].equals("clear")) {
            PlayerSkinController sData = PlayerSkinController.getInstance();
            int type;
            switch (args[1]) {
                case "skins":
                    type = 0;
                    break;
                case "capes":
                    type = 1;
                    break;
                case "elytras":
                    type = 2;
                    break;
                default:
                    throw new CommandException("Usage " + "clear <action_name>");
            }
            sData.clear(null, type);
            sender.sendMessage(Component.translatable("command.player.clear.skin.all." + type).getParent());
        }
	}

	@SubCommand(desc = "Show the store window to the player", usage = "<playername> <marcetID>", permission = 2)
	public void openmarcet(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		EntityPlayerMP player;
		try { player = CommandBase.getPlayer(server, sender, args[0]); }
		catch (Exception e) { throw new PlayerNotFoundException("commands.generic.player.notFound", args[0]); }
		int marcetId;
		try { marcetId = Integer.parseInt(args[1]); }
		catch (NumberFormatException ex) { throw new CommandException("Must be an integer: " + args[1]); }
		Marcet marcet = MarcetController.getInstance().getMarcet(marcetId);
		if (marcet == null || !marcet.isValid()) {
			sender.sendMessage(new TextComponentTranslation("command.player.openmarcet.error", "" + marcetId));
			return;
		}
		NoppesUtilServer.setEditingNpc(player, null);
		NoppesUtilServer.openContainerGui(player, EnumGuiType.PlayerTrader, buf -> buf.writeInt(marcetId));
	}

	public static void executeSkin(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		String error = "Usage " + "/noppes player <player name> [skin|cape|elytra] [get|set|clear] [add values]";
		if (args.length < 3) { throw new CommandException(error); }
		String name = args[0];
		PlayerData data = getData(server, args[0]);
		PlayerSkinController sData = PlayerSkinController.getInstance();
		switch (args[1].toLowerCase()) {
			case "skin": {
				switch (args[2]) {
					case "clear": {
						sData.clear(data.uuid, 0);
						sender.sendMessage(Component.translatable("command.player.clear.0", data.name).getParent());
						break;
					}
					case "get": {
						SkinData skinData = PlayerSkinController.getInstance().get(data.uuid, 0);
						if (skinData == null) { throw new PlayerNotFoundException("commands.generic.player.notFound", name); }
						String skin;
						if (skinData.isUrl()) { skin = "(URL) " + skinData.getUrl(); }
						else if (skinData.isLocation()) { skin = "(Location) " + skinData.getLocation(); }
						else {
							ResourceLocation location = skinData.getLocation();
							if (location == null) { skin = "(Not set or create texture)"; }
							else { skin = "(Composite) " + skinData.getLocation(); }
						}
						sender.sendMessage(Component.translatable("command.player.get.0", data.name, skin).getParent());
						break;
					}
					case "set": {
						switch (args[3]) {
							case "url": {
								if (args.length < 5 || args[4].isEmpty()) {
									throw new CommandException("Usage " + "/noppes player <player name> skin set url <urllink>");
								}
								String HTTPS_START = "https://";
								if(!args[4].startsWith(args[4].length() < HTTPS_START.length() ? HTTPS_START.substring(0, args[4].length()) : HTTPS_START)) {
									throw new CommandException("argument.no.https");
								}
								sData.create(UUID.fromString(data.uuid), data.name, 0, 0, args[4]);
								sender.sendMessage(Component.translatable("command.player.set.0", data.name, "(URL) " + args[4]).getParent());
								break;
							}
							case "location": {
								if (args.length < 5 || args[4].isEmpty()) {
									throw new CommandException("Usage " + "/noppes player <player name> skin set location <location path>");
								}
								sData.create(UUID.fromString(data.uuid), data.name, 0, 1, args[4]);
								sender.sendMessage(Component.translatable("command.player.set.0", data.name, "(Location) " + args[4]).getParent());
								break;
							}
							case "composite": {
								error = "Usage /noppes player <player name> skin set composite <genderID> <bodyID>" +
										" <bodyColor> <hairID> <hairColor> <faceID> <eyesColor> <legID> <jacketID> <shoesID> <ID scars>...";
								if (args.length < 14) { throw new CommandException(error); }
								StringBuilder location = new StringBuilder(CustomNpcs.MODID + "textures/entity/custom/");
								try { location.append(Integer.parseInt(args[4]) == 0 ? "male" : "female"); }
								catch (NumberFormatException e) { throw new CommandException("<genderID> must be an integer: " + args[4]); }
								try { location.append("_").append(Integer.parseInt(args[5])); }
								catch (NumberFormatException e) { throw new CommandException("<bodyID> must be an integer: " + args[5]); }
								try { location.append("_").append(Integer.parseInt(args[6])); }
								catch (NumberFormatException e) { throw new CommandException("<bodyColor> must be an integer: " + args[6]); }
								try { location.append("_").append(Integer.parseInt(args[7])); }
								catch (NumberFormatException e) { throw new CommandException("<hairID> must be an integer: " + args[7]); }
								try { location.append("_").append(Integer.parseInt(args[8])); }
								catch (NumberFormatException e) { throw new CommandException("<hairColor> must be an integer: " + args[8]); }
								try { location.append("_").append(Integer.parseInt(args[9])); }
								catch (NumberFormatException e) { throw new CommandException("<faceID> must be an integer: " + args[9]); }
								try { location.append("_").append(Integer.parseInt(args[10])); }
								catch (NumberFormatException e) { throw new CommandException("<eyesColor> must be an integer: " + args[10]); }
								try { location.append("_").append(Integer.parseInt(args[11])); }
								catch (NumberFormatException e) { throw new CommandException("<legID> must be an integer: " + args[11]); }
								try { location.append("_").append(Integer.parseInt(args[12])); }
								catch (NumberFormatException e) { throw new CommandException("<jacketID> must be an integer: " + args[12]); }
								try { location.append("_").append(Integer.parseInt(args[13])); }
								catch (NumberFormatException e) { throw new CommandException("<shoesID> must be an integer: " + args[13]); }
								for (int i = 14; i < args.length; i++) {
									try { location.append("_").append(Integer.parseInt(args[i])); }
									catch (NumberFormatException e) { throw new CommandException("Any <ID scars> must be an integer: " + args[i]); }
								}
								location.append(".png");
								sData.create(UUID.fromString(data.uuid), data.name, 0, 2, location.toString());
								sender.sendMessage(Component.translatable("command.player.set.0", data.name, "(Composite) " + location).getParent());
								break;
							}
							default: throw new CommandException("Usage " + "/noppes player <player name> skin set [url|location|composite]");
						}
						break;
					}
					default: throw new CommandException("Usage " + "/noppes player <player name> skin [get|set|clear]");
				}
				break;
			}
			case "cape": {
				switch (args[2]) {
					case "clear": {
						sData.clear(data.uuid, 1);
						sender.sendMessage(Component.translatable("command.player.clear.1", data.name).getParent());
						break;
					}
					case "get": {
						SkinData skinData = PlayerSkinController.getInstance().get(data.uuid, 1);
						if (skinData == null) { throw new PlayerNotFoundException("commands.generic.player.notFound", name); }
						String cape;
						if (skinData.isUrl()) { cape = "(URL) " + skinData.getUrl(); }
						else { cape = "(Location) " + skinData.getLocation(); }
						sender.sendMessage(Component.translatable("command.player.get.1", data.name, cape).getParent());
						break;
					}
					case "set": {
						switch (args[3]) {
							case "url": {
								if (args.length < 5 || args[4].isEmpty()) {
									throw new CommandException("Usage " + "/noppes player <player name> cape set url <urllink>");
								}
								String HTTPS_START = "https://";
								if(!args[4].startsWith(args[4].length() < HTTPS_START.length() ? HTTPS_START.substring(0, args[4].length()) : HTTPS_START)) {
									throw new CommandException("argument.no.https");
								}
								sData.create(UUID.fromString(data.uuid), data.name, 1, 0, args[4]);
								sender.sendMessage(Component.translatable("command.player.set.1", data.name, "(URL) " + args[4]).getParent());
								break;
							}
							case "location": {
								if (args.length < 5 || args[4].isEmpty()) {
									throw new CommandException("Usage " + "/noppes player <player name> cape set location <location path>");
								}
								sData.create(UUID.fromString(data.uuid), data.name, 1, 1, args[4]);
								sender.sendMessage(Component.translatable("command.player.set.1", data.name, "(Location) " + args[4]).getParent());
								break;
							}
							default: throw new CommandException("Usage " + "/noppes player <player name> cape set [url|location]");
						}
						break;
					}
					default: throw new CommandException("Usage " + "/noppes player <player name> cape [get|set|clear]");
				}
				break;
			}
			case "elytra": {
				switch (args[2]) {
					case "clear": {
						sData.clear(data.uuid, 2);
						sender.sendMessage(Component.translatable("command.player.clear.2", data.name).getParent());
						break;
					}
					case "get": {
						SkinData skinData = PlayerSkinController.getInstance().get(data.uuid, 2);
						if (skinData == null) { throw new PlayerNotFoundException("commands.generic.player.notFound", name); }
						String elytra;
						if (skinData.isUrl()) { elytra = "(URL) " + skinData.getUrl(); }
						else { elytra = "(Location) " + skinData.getLocation(); }
						sender.sendMessage(Component.translatable("command.player.get.2", data.name, elytra).getParent());
						break;
					}
					case "set": {
						switch (args[3]) {
							case "url": {
								if (args.length < 5 || args[4].isEmpty()) {
									throw new CommandException("Usage " + "/noppes player <player name> elytra set url <urllink>");
								}
								String HTTPS_START = "https://";
								if(!args[4].startsWith(args[4].length() < HTTPS_START.length() ? HTTPS_START.substring(0, args[4].length()) : HTTPS_START)) {
									throw new CommandException("argument.no.https");
								}
								sData.create(UUID.fromString(data.uuid), data.name, 2, 0, args[4]);
								sender.sendMessage(Component.translatable("command.player.set.2", data.name, "(URL) " + args[4]).getParent());
								break;
							}
							case "location": {
								if (args.length < 5 || args[4].isEmpty()) {
									throw new CommandException("Usage " + "/noppes player <player name> elytra set location <location path>");
								}
								sData.create(UUID.fromString(data.uuid), data.name, 2, 1, args[4]);
								sender.sendMessage(Component.translatable("command.player.set.2", data.name, "(Location) " + args[4]).getParent());
								break;
							}
							default: throw new CommandException("Usage " + "/noppes player <player name> elytra set [url|location]");
						}
						break;
					}
					default: throw new CommandException("Usage " + "/noppes player <player name> elytra [get|set|clear]");
				}
				break;
			}
			default: throw new CommandException(error);
		}
	}

	@Override
	public @Nonnull List<String> getTabCompletions(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args, BlockPos pos) {
		List<String> list = new ArrayList<>();
		if (args.length == 0 || args.length == 2) {
			if (args.length == 2 && (args[1].equals("skin") || args[0].equals("cape") || args[0].equals("elytra"))) {
				list.add("get");
				list.add("set");
				list.add("clear");
			}
			else { list = PlayerDataController.instance.getPlayerNames(); }
		} // players name or sub commands
		else if (args.length == 1) {
			if (!args[0].equals("openmarcet") && !args[0].equals("all")) {
				list.add("skin");
				list.add("cape");
				list.add("elytra");
			}
		}
		else if (args.length == 3) {
			if (args[1].equals("openmarcet")) {
				for (int id : MarcetController.getInstance().markets.keySet()) { list.add("" + id); }
			}
			else if (args[1].equals("skin") && args[2].equals("set")) {
				list.add("url");
				list.add("location");
				list.add("composite");
			}
		}
		else if (args.length == 4) {
			if (args[1].equals("skin") && args[2].equals("set")) {
				if (args[3].equals("url")) {
					list.add("https://");
					list.add("https://github.com/BetaZavr/CustomNPCs_1.12.2-Unofficial/blob/master/hd%20skins/betazavr.png?raw=true");
					list.add("https://i.imgur.com/mORJxcm.png");
				}
			}
		}
		return list;
	}

}
