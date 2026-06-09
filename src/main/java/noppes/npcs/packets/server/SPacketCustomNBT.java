package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.EventHooks;
import noppes.npcs.api.event.PlayerEvent;
import noppes.npcs.constants.EnumScriptType;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketCustomNBT extends PacketServerBasic {

    protected static int channelId;
    private NBTTagCompound data;

    public SPacketCustomNBT() { }

    public SPacketCustomNBT(NBTTagCompound dataIn) { data = dataIn; }

    @Override
    public boolean toolAllowed(ItemStack item){ return true; }

    @Override
    public void encode(FriendlyByteBuf buf) {buf.writeNbt(data); }

    @Override
    public void decode(FriendlyByteBuf buf) { data = buf.readNbt(); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        PlayerData pd = PlayerData.get(player);
        if (pd != null) {
            EventHooks.onEvent(pd.scriptData, EnumScriptType.PACKAGE_FROM, new PlayerEvent.PlayerPackage(pd.scriptData.getIPlayer(), data));
        }
        CustomNpcs.debugData.end("Packets");
    }

}