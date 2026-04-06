package noppes.npcs.shared.common;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.wrapper.PlayerWrapper;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.util.CustomNPCsScheduler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public abstract class PacketServerBasic extends PacketBasic {

    private static final Logger LOGGER = LogManager.getLogger();
    public EntityPlayerMP player;
    public PlayerWrapper<?> iPlayer;
    public EntityNPCInterface npc;

    public boolean requiresNpc() { return false; }

    public CustomNpcsPermissions.Permission getPermission() { return null; }

    public boolean toolAllowed(ItemStack item) { return item.getItem() == CustomItems.wand; }

    public void handleServer() {
        if (ctx.side == Side.SERVER) {
            player = ctx.getServerHandler().player;
            iPlayer = (PlayerWrapper<?>) Objects.requireNonNull(NpcAPI.Instance()).getIEntity(player);
            npc = NoppesUtilServer.getEditingNpc(player);
            try {
                if (!requiresNpc() || npc != null) {
                    if (getPermission() == null || CustomNpcsPermissions.hasPermission(player, getPermission())) {
                        CustomNPCsScheduler.runTack( ()-> {
                            if (!toolAllowed(player.getHeldItem(EnumHand.MAIN_HAND))) { warn(getPermission()); }
                            else { handle(); }
                        });
                    } else {
                        permission(getPermission(), getClass().getSimpleName());
                    }
                }
            } catch (Exception e) { LOGGER.error(e); }
        }
    }

    protected void permission(CustomNpcsPermissions.Permission permission, String className) {
        LOGGER.warn("{}: attempted to use a mechanism that was prohibited to him. Permission: {}",
                player.getName(), permission.getNodeName());
        player.sendMessage(Component.translatable("availability.permission")
                .append(TextFormatting.RED + ": " + TextFormatting.RESET + className));
    }

    protected void warn(CustomNpcsPermissions.Permission permission) {
        LOGGER.warn("{}: tried to use custom npcs without a tool in hand, possibly a hacker - {}, permission - {}",
                player == null ? "NULL" : player.getName(), this, permission.getNodeName());
        if (player != null)  { player.sendMessage(new TextComponentTranslation("availability.permission")); }
    }

}