package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.DropController;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketDropTemplateClear extends PacketServerBasic {

    protected static int channelId;

    @Override
    public PermissionNode<Boolean> getPermission() { return CustomNpcsPermissions.NPC_INVENTORY; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    public static void encode(SPacketDropTemplateClear ignoredMsg, FriendlyByteBuf ignoredBuf) {}

    public static SPacketDropTemplateClear decode(FriendlyByteBuf ignoredBuf) { return new SPacketDropTemplateClear(); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        DropController.getInstance().templates.clear();
        CustomNpcs.debugData.end("Packets");
    }

}
