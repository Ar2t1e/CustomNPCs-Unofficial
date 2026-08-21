package noppes.npcs.command;

import java.util.Map;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.CommandNoppesBase;
import noppes.npcs.entity.data.DataScenes;

import javax.annotation.Nonnull;

public class CmdScene extends CommandNoppesBase {
	@Override
	public int getRequiredPermissionLevel() { return CustomNpcs.NoppesCommandOpOnly ? 4 : 2; }

	@Override
	public String getDescription() { return "Scene operations"; }

	@Nonnull
    public String getName() { return "scene"; }

	@SubCommand(desc = "Pause scene", usage = "[name]", isOpOnly = true)
	public void pause(String[] args) {
		DataScenes.Pause((args.length == 0) ? null : args[0], null);
	}

	@SubCommand(desc = "Reset scene", usage = "[name]", isOpOnly = true)
	public void reset(ICommandSender sender, String[] args) {
		DataScenes.Reset((args.length == 0) ? null : args[0], null);
	}

	@SubCommand(desc = "Start scene", usage = "<name>", isOpOnly = true)
	public void start(ICommandSender sender, String[] args) {
		DataScenes.Start(args[0], null);
	}

	@SuppressWarnings("all")
	@SubCommand(desc = "Get/Set scene time", usage = "[time] [name]", isOpOnly = true)
	public void time(ICommandSender sender, String[] args) throws CommandException {
		if (args.length == 0) {
			this.sendMessage(sender, "Active scenes:");
			for (Map.Entry<String, DataScenes.SceneState> entry : DataScenes.StartedScenes.entrySet()) {
				this.sendMessage(sender, "Scene %s time is %s", entry.getKey(), entry.getValue().ticks);
			}
		} else if (args.length == 1) {
			int ticks = Integer.parseInt(args[0]);
			for (DataScenes.SceneState state : DataScenes.StartedScenes.values()) {
				state.ticks = ticks;
			}
			this.sendMessage(sender, "All Scene times are set to " + ticks);
		} else {
			DataScenes.SceneState state2 = DataScenes.StartedScenes.get(args[1].toLowerCase());
			if (state2 == null) {
				throw new CommandException("Unknown scene name %s", args[1]);
			}
			state2.ticks = Integer.parseInt(args[0]);
			this.sendMessage(sender, "Scene %s set to %s", args[1], state2.ticks);
		}
	}
}
