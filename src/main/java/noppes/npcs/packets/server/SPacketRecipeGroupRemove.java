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

public class SPacketRecipeGroupRemove extends PacketServerBasic {

    protected static int channelId;
    private final boolean isGlobal;
    private final String group;

    public SPacketRecipeGroupRemove(boolean isGlobalIn, String groupIn) {
        isGlobal = isGlobalIn;
        group = groupIn;
    }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public PermissionNode<Boolean> getPermission() { return CustomNpcsPermissions.GLOBAL_RECIPE; }

    public static void encode(SPacketRecipeGroupRemove msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.isGlobal);
        buf.writeUtf(msg.group);
    }

    public static SPacketRecipeGroupRemove decode(FriendlyByteBuf buf) { return new SPacketRecipeGroupRemove(buf.readBoolean(), buf.readUtf()); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        RecipeController.getInstance().deleteGroup(isGlobal, group);
        Packets.sendDelayed(player, new PacketGuiUpdate(), 100);
        CustomNpcs.debugData.end("Packets");
    }

}