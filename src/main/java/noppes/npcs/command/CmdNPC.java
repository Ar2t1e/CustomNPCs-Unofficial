package noppes.npcs.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import noppes.npcs.CustomNpcs;
import noppes.npcs.shared.common.util.LogWriter;
import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import noppes.npcs.api.CommandNoppesBase;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.RoleCompanion;
import noppes.npcs.roles.RoleFollower;

import javax.annotation.Nonnull;

public class CmdNPC extends CommandNoppesBase {

	@Nonnull
	public String getName() { return "npc"; }

	@Override
	public String getDescription() { return "NPC operation"; }

	@Override
	public String getUsage() { return "<name> <command>"; }

	@Override
	public int getRequiredPermissionLevel() { return CustomNpcs.NoppesCommandOpOnly ? 4 : 2; }

	public EntityNPCInterface selectedNpc;

	@SubCommand(desc = "Creates an NPC", usage = "[name]", isOpOnly = true)
	public void create(MinecraftServer server, ICommandSender sender, String[] args) {
		World pw = sender.getEntityWorld();
		EntityCustomNpc npc = new EntityCustomNpc(pw);
		if (args.length > 0) {
			npc.display.setName(args[0]);
		}
		BlockPos pos = sender.getPosition();
		npc.setPositionAndRotation(pos.getX(), pos.getY(), pos.getZ(), 0.0f, 0.0f);
		npc.ais.setStartPos(pos);
		pw.spawnEntity(npc);
		npc.setHealth(npc.getMaxHealth());
	}

	@SubCommand(desc = "Delete an NPC", isOpOnly = true)
	public void delete(MinecraftServer server, ICommandSender sender, String[] args) {
		this.selectedNpc.delete();
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args == null) { return; }
        String npcname = args[0].replace("%", " ");
		String command = args[1];
		args = Arrays.copyOfRange(args, 2, args.length);
		if (command.equalsIgnoreCase("create")) {
			args = (String[]) ArrayUtils.add((Object[]) args, 0, npcname);
			this.executeSub(server, sender, command, args);
			return;
		}
		List<EntityNPCInterface> list = this.getEntities(EntityNPCInterface.class,
				sender.getEntityWorld(), sender.getPosition(), 80);
		for (EntityNPCInterface npc : list) {
			String name = npc.display.getName().replace(" ", "_");
			if (name.equalsIgnoreCase(npcname) && (this.selectedNpc == null || this.selectedNpc
					.getDistanceSq(sender.getPosition()) > npc.getDistanceSq(sender.getPosition()))) {
				this.selectedNpc = npc;
			}
		}
		if (this.selectedNpc == null) {
			throw new CommandException("Npc \""+npcname+"\" was not found");
		}
		this.executeSub(server, sender, command, args);
		this.selectedNpc = null;
	}

	public <T extends Entity> List<T> getEntities(Class<? extends T> cls, World world, BlockPos pos, int range) {
		List<T> list = new ArrayList<>();
		try {
			list = world.getEntitiesWithinAABB(cls, new AxisAlignedBB(pos, pos.add(1, 1, 1)).grow(range, range, range));
		}
		catch (Exception ignored) { }
		return list;
	}

	public @Nonnull List<String> getTabCompletions(@Nonnull MinecraftServer server, @Nonnull ICommandSender par1, @Nonnull String[] args, BlockPos pos) {
		if (args.length == 2) {
			return CommandBase.getListOfStringsMatchingLastWord(args, "create", "home", "visible", "delete", "owner", "name");
		}
		if (args.length == 3 && args[1].equalsIgnoreCase("owner")) {
			return CommandBase.getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
		}
		return new ArrayList<>();
	}

	@SubCommand(desc = "Set Home (respawn place)", usage = "[x] [y] [z]", isOpOnly = true)
	public void home(MinecraftServer server, ICommandSender sender, String[] args) {
		BlockPos pos = sender.getPosition();
		if (args.length == 3) {
			try {
				pos = CommandBase.parseBlockPos(sender, args, 0, false);
			} catch (NumberInvalidException e) { LogWriter.error(e); }
		}
		this.selectedNpc.ais.setStartPos(pos);
	}

	@SubCommand(desc = "Set NPC name", usage = "[name]", isOpOnly = true)
	public void name(MinecraftServer server, ICommandSender sender, String[] args) {
		if (args.length < 1) {
			return;
		}
		StringBuilder name = new StringBuilder(args[0]);
		for (int i = 1; i < args.length; ++i) {
			name.append(" ").append(args[i]);
		}
		if (!this.selectedNpc.display.getName().contentEquals(name)) {
			this.selectedNpc.display.setName(name.toString());
			this.selectedNpc.updateClient = true;
		}
	}

	@SubCommand(desc = "Sets the owner of an follower/companion", usage = "[player]", isOpOnly = true)
	public void owner(MinecraftServer server, ICommandSender sender, String[] args) {
		EntityPlayer player = null;
		if (args.length < 1) {
			if (this.selectedNpc.role instanceof RoleFollower) {
				player = ((RoleFollower) this.selectedNpc.role).owner;
			}
			if (this.selectedNpc.role instanceof RoleCompanion) {
				player = ((RoleCompanion) this.selectedNpc.role).owner;
			}
			if (player == null) {
				this.sendMessage(sender, "No owner");
			} else {
				this.sendMessage(sender, "Owner is: " + player.getName());
			}
		} else {
			try {
				player = CommandBase.getPlayer(server, sender, args[0]);
			} catch (Exception e) { LogWriter.error(e); }
			if (player != null) {
				if (this.selectedNpc.role instanceof RoleFollower) {
					((RoleFollower) this.selectedNpc.role).setOwner(player);
				}
				if (this.selectedNpc.role instanceof RoleCompanion) {
					((RoleCompanion) this.selectedNpc.role).setOwner(player);
				}
			}
		}
	}

	@SubCommand(desc = "Resets the npc", usage = "[name]", isOpOnly = true)
	public void reset(MinecraftServer server, ICommandSender sender, String[] args) {
		this.selectedNpc.reset();
	}

	@Override
	public boolean runSubCommands() {
		return false;
	}

	@SubCommand(desc = "Set NPC visibility", usage = "[true/false/semi]", isOpOnly = true)
	public void visible(MinecraftServer server, ICommandSender sender, String[] args) {
		if (args.length < 1) {
			return;
		}
		boolean bo = args[0].equalsIgnoreCase("true");
		boolean semi = args[0].equalsIgnoreCase("semi");
		if (semi) {
			this.selectedNpc.display.setVisible(2);
		} else if (bo) {
			this.selectedNpc.display.setVisible(0);
		} else {
			this.selectedNpc.display.setVisible(1);
		}
	}
}
