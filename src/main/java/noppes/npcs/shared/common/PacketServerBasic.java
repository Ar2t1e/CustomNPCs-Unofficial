package noppes.npcs.shared.common;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.wrapper.PlayerWrapper;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.util.CustomNPCsScheduler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Objects;

public abstract class PacketServerBasic extends PacketBasic {

    private static final Logger LOGGER = LogManager.getLogger();
    public EntityPlayerMP player;
    public PlayerWrapper<?> iPlayer;
    public EntityNPCInterface npc;

    public abstract boolean requiresNpc();

    public abstract List<CustomNpcsPermissions.Permission> getPermission();

    public abstract boolean toolAllowed(ItemStack item);

    public void handleServer() {
        if (ctx.side == Side.SERVER) {
            player = ctx.getServerHandler().player;
            iPlayer = (PlayerWrapper<?>) Objects.requireNonNull(NpcAPI.Instance()).getIEntity(player);
            npc = NoppesUtilServer.getEditingNpc(player);
            try {
                if (!requiresNpc() || npc != null) {
                    List<CustomNpcsPermissions.Permission> permissions = getPermission();
                    StringBuilder prs = new StringBuilder();
                    if (permissions != null) {
                        boolean isAccess = permissions.isEmpty();
                        for (CustomNpcsPermissions.Permission permission : permissions) {
                            if (prs.length() > 0) { prs.append(", "); }
                            prs.append(permission.getNodeName());
                            if (CustomNpcsPermissions.hasPermission(player, permission)) { isAccess = true; }
                        }
                        if (!isAccess) {
                            permission(prs.toString());
                            return;
                        }
                    }
                    CustomNPCsScheduler.runTack(()-> {
                        if (!toolAllowed(player.getHeldItem(EnumHand.MAIN_HAND))) { warn(prs.toString()); }
                        else { handle(); }
                    });
                }
            } catch (Exception e) { LOGGER.error(e); }
        }
    }

    protected void permission(String permissions) {
        LOGGER.warn("Player: \"{}\" attempted to use a mechanism that was prohibited to him. Packet: \"{}\". Permissions: [{}]",
                player == null ? "NULL" : player.getName(),
                getClass().getSimpleName(),
                permissions == null || permissions.isEmpty() ? "NULL" : permissions);
        sendNotAccess();
    }

    protected void warn(String permissions) {
        LOGGER.warn("Player: \"{}\" tried to use custom npcs without a tool in hand, possibly a hacker. Packet: \"{}\". Permission: [{}]",
                player == null ? "NULL" : player.getName(),
                getClass().getSimpleName(),
                permissions == null || permissions.isEmpty() ? "NULL" : permissions);
        sendNotAccess();
    }

    private void sendNotAccess() {
        if (player != null) {
            player.sendMessage(Component.translatable("availability.permission")
                    .append(TextFormatting.RED + ": " + TextFormatting.RESET + getClass().getSimpleName()).getParent());
        }
    }

}