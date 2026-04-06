package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketDetectHeldItem extends PacketServerBasic {

    protected static int channelId;
    private final ItemStack stack;

    public SPacketDetectHeldItem(ItemStack stackIn) { stack = stackIn; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public PermissionNode<Boolean> getPermission() { return CustomNpcsPermissions.TOOL_NBTBOOK; }

    public static void encode(SPacketDetectHeldItem msg, FriendlyByteBuf buf) { buf.writeItemStack(msg.stack, false); }

    public static SPacketDetectHeldItem decode(FriendlyByteBuf buf) { return new SPacketDetectHeldItem(buf.readItem()); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        if (!stack.isEmpty()) {
            player.inventoryMenu.setCarried(stack);
            player.inventoryMenu.broadcastChanges();
            player.connection.send(new ClientboundContainerSetSlotPacket(-1, 0, 0, stack));
        }
        CustomNpcs.debugData.end("Packets");
    }

}