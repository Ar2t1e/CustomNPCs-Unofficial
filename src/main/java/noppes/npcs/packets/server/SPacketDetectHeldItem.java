package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.play.server.SPacketSetSlot;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.Collections;
import java.util.List;

public class SPacketDetectHeldItem extends PacketServerBasic {

    protected static int channelId;
    private ItemStack stack;

    public SPacketDetectHeldItem() { }

    public SPacketDetectHeldItem(ItemStack stackIn) { stack = stackIn; }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public List<CustomNpcsPermissions.Permission> getPermission() { return Collections.singletonList(CustomNpcsPermissions.TOOL_NBTBOOK); }

    @Override
    public void encode(FriendlyByteBuf buf) { buf.writeItemStack(stack, false); }

    @Override
    public void decode(FriendlyByteBuf buf) { stack = buf.readItem(); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        if (!stack.isEmpty()) {
            player.inventory.setItemStack(stack);
            player.inventoryContainer.detectAndSendChanges();
            player.connection.sendPacket(new SPacketSetSlot(-1, 0, stack));
        }
        CustomNpcs.debugData.end("Packets");
    }

}