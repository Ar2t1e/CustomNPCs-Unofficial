package noppes.npcs.packets.client;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.EventHooks;
import noppes.npcs.api.event.PlayerEvent;
import noppes.npcs.constants.EnumScriptType;
import noppes.npcs.shared.common.PacketBasic;

public class PacketCustomNBT extends PacketBasic {

    protected static int channelId;
    private NBTTagCompound data;

    public PacketCustomNBT() { }

    public PacketCustomNBT(NBTTagCompound dataIn) { data = dataIn; }

    @Override
    public void decode(FriendlyByteBuf buf) { data = buf.readNbt(); }

    @Override
    public void encode(FriendlyByteBuf buf) { buf.writeNbt(data); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        EventHooks.onEvent(CustomNpcs.proxy.getPlayerData(player).scriptData, EnumScriptType.PACKAGE_FROM,
                new PlayerEvent.PlayerPackage(CustomNpcs.proxy.getPlayerData(player).scriptData.getIPlayer(), data));
        CustomNpcs.debugData.end("Packets");
    }
}
