package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.controllers.BorderController;
import noppes.npcs.controllers.data.Zone3D;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketRegionSave extends PacketServerBasic {

    protected static int channelId;
    private NBTTagCompound data;

    public SPacketRegionSave() { }

    public SPacketRegionSave(NBTTagCompound dataIn) { data = dataIn; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public void encode(FriendlyByteBuf buf) { buf.writeNbt(data); }

    @Override
    public void decode(FriendlyByteBuf buf) { data = buf.readAnySizeNbt(); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        Zone3D reg = BorderController.getInstance().loadRegion(data);
        if (reg != null) {
            BorderController.getInstance().save();
            BorderController.getInstance().update(reg.getId());
        }
        CustomNpcs.debugData.end("Packets");
    }

}