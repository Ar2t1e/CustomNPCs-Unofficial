package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketPlayerIsMoved extends PacketServerBasic {

    protected static int channelId;
    private boolean isMoved;

    public SPacketPlayerIsMoved() { }

    public SPacketPlayerIsMoved(boolean isMovedIn) { isMoved = isMovedIn; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public void encode(FriendlyByteBuf buf) { buf.writeBoolean(isMoved); }

    @Override
    public void decode(FriendlyByteBuf buf) { isMoved = buf.readBoolean(); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    public void handle() {
        CustomNpcs.debugData.start("Packets");
        PlayerData.get(player).overlay.isMoved = isMoved;
        CustomNpcs.debugData.end("Packets");
    }

}