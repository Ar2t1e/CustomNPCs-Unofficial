package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.api.gui.ModelMenu;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.Collections;
import java.util.List;

public class SPacketOpenParts extends PacketServerBasic {

    protected static int channelId;

    @Override
    public boolean toolAllowed(ItemStack item) { return item.getItem() == CustomItems.wand; }

    @Override
    public boolean requiresNpc() { return true; }

    @Override
    public List<PermissionNode<Boolean>> getPermission() { return Collections.singletonList(CustomNpcsPermissions.NPC_GUI); }

    public static void encode(SPacketOpenParts ignoredMsg, FriendlyByteBuf ignoredBuf) {}

    public static SPacketOpenParts decode(FriendlyByteBuf ignoredBuf) { return new SPacketOpenParts(); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        ModelMenu.open(player, (EntityCustomNpc) npc);
        CustomNpcs.debugData.end("Packets");
    }

}
