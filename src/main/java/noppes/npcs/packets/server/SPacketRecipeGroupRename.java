package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.RecipeController;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiUpdate;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.Collections;
import java.util.List;

public class SPacketRecipeGroupRename extends PacketServerBasic {

    protected static int channelId;
    private final boolean isGlobal;
    private final String oldGroup;
    private final String newGroup;

    public SPacketRecipeGroupRename(boolean isGlobalIn, String oldGroupIn, String newGroupIn) {
        isGlobal = isGlobalIn;
        oldGroup = oldGroupIn;
        newGroup = newGroupIn;
    }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public List<PermissionNode<Boolean>>  getPermission() { return Collections.singletonList(CustomNpcsPermissions.GLOBAL_RECIPE); }

    public static void encode(SPacketRecipeGroupRename msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.isGlobal);
        buf.writeUtf(msg.oldGroup);
        buf.writeUtf(msg.newGroup);
    }

    public static SPacketRecipeGroupRename decode(FriendlyByteBuf buf) { return new SPacketRecipeGroupRename(buf.readBoolean(), buf.readUtf(), buf.readUtf()); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        RecipeController.getInstance().renameGroup(isGlobal, oldGroup, newGroup);
        Packets.sendDelayed(player, new PacketGuiUpdate(), 100);
        CustomNpcs.debugData.end("Packets");
    }

}