package noppes.npcs.shared.common;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.chat.Component;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketScriptError;

import javax.annotation.Nullable;

public class CommonUtil {

    private static final List<Component> errorMessagesToAdmin = new ArrayList<>();

    public static void NotifyOPs(String message, Object... obs) {
        NotifyOPs(Component.translatable(message, obs).withStyle(TextFormatting.GRAY, TextFormatting.ITALIC), false);
    }

    @SuppressWarnings("ConstantConditions")
    public static void NotifyOPs(Component message, boolean isScriptError) {
        Component component = Component.literal("[")
                .append(Component.literal(CustomNpcs.MODNAME).withStyle(TextFormatting.DARK_GREEN))
                .append(Component.literal("]").withStyle(TextFormatting.WHITE))
                .append(Component.literal(": ").withStyle(TextFormatting.GRAY))
                .append(message);
        boolean isSend = false;
        if (CustomNpcs.Server != null) {
            for (EntityPlayerMP player : CustomNpcs.Server.getPlayerList().getPlayers()) {
                if (CustomNpcsPermissions.hasPermission(player, CustomNpcsPermissions.ADMIN) ||
                        (player.isCreative() && isOp(player))) {
                    if (isScriptError) { Packets.send(player, new PacketScriptError(component)); }
                    else if (CustomNpcs.DisplayErrorInChat) { player.sendMessage(component.getParent()); }
                    isSend = true;
                }
            }
            WorldServer world = DimensionManager.getWorld(0);
            if (world != null && world.getGameRules().getBoolean("logAdminCommands")) { LogWriter.info(component.getFormattedText()); }
        }
        if (!isSend) {
            boolean found = false;
            for (Component mes : errorMessagesToAdmin) {
                if (mes.getString().equals(component.getString())) {
                    found = true;
                    break;
                }
            }
            if (!found) { errorMessagesToAdmin.add(component); }
        }
    }

    public static boolean isOp(@Nullable EntityPlayer player) {
        if (player == null || player.getServer() == null) { return false; }
        return player.getServer().getPlayerList().canSendCommands(player.getGameProfile());
    }

    // New from Unofficial (BetaZavr)
    public static void sendScriptErrorsTo(EntityPlayer player) {
        if (!errorMessagesToAdmin.isEmpty() && player != null && player.isCreative() && isOp(player)) {
            for (Component component : errorMessagesToAdmin) {
                if (player instanceof EntityPlayerMP) { Packets.send((EntityPlayerMP) player, new PacketScriptError(component)); }
                else if (CustomNpcs.DisplayErrorInChat) { player.sendMessage(component.getParent()); }
            }
            errorMessagesToAdmin.clear();
        }
    }

}
