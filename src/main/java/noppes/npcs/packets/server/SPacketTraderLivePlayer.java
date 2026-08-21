package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.MarcetController;
import noppes.npcs.controllers.data.Marcet;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.List;

public class SPacketTraderLivePlayer extends PacketServerBasic {

    protected static int channelId;
    private int marcetId;

    public SPacketTraderLivePlayer() { }

    public SPacketTraderLivePlayer(int marcetIDIn) { marcetId = marcetIDIn; }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public List<CustomNpcsPermissions.Permission> getPermission() { return null; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public void encode(FriendlyByteBuf buf) { buf.writeInt(marcetId); }

    @Override
    public void decode(FriendlyByteBuf buf) { marcetId = buf.readInt(); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        Marcet marcet = MarcetController.getInstance().getMarcet(marcetId);
        if (marcet != null) { marcet.removeListener(player, true); }
        CustomNpcs.debugData.end("Packets");
    }

}