package noppes.npcs.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.CommandNoppesBase;
import noppes.npcs.constants.EnumGuiType;

import javax.annotation.Nonnull;

public class CmdPermissions extends CommandNoppesBase {

    @Override
    public int getRequiredPermissionLevel() { return CustomNpcs.NoppesCommandOpOnly ? 4 : 2; }

    @Override
    public String getDescription() { return "Permission manager"; }

    @Nonnull
    public String getName() { return "permissions"; }

    @SubCommand(desc = "Open GUI manager", isOpOnly = true)
    public void open(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (sender instanceof EntityPlayerMP) {
            if (!CustomNpcsPermissions.hasPermission((EntityPlayerMP) sender, CustomNpcsPermissions.EDIT_PERMISSION)) { throw new CommandException("availability.permission"); }
            NoppesUtilServer.sendOpenGui((EntityPlayerMP) sender, EnumGuiType.PermissionsEdit, null);
        }
    }
}
