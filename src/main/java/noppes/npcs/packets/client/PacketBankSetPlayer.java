package noppes.npcs.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.containers.ContainerNPCBank;
import noppes.npcs.shared.common.PacketBasic;

import javax.annotation.Nonnull;

public class PacketBankSetPlayer extends PacketBasic {

    protected static int channelId;
    private String name;

    public PacketBankSetPlayer() { }

    public PacketBankSetPlayer(@Nonnull String nameIn) { name = nameIn; }

    @Override
    public void encode(FriendlyByteBuf buf) { buf.writeUtf(name); }

    @Override
    public void decode(FriendlyByteBuf buf) { name = buf.readUtf(); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        ContainerNPCBank.editPlayerBankData = name.isEmpty() ? null : name;
        CustomNpcs.debugData.end("Packets");
    }

}
