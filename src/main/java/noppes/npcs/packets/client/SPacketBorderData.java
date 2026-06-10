package noppes.npcs.packets.client;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.BorderController;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.List;

public class SPacketBorderData extends PacketServerBasic {

    protected static int channelId;
    private NBTTagCompound data;

    public SPacketBorderData() { }

    public SPacketBorderData(NBTTagCompound dataIn) { data = dataIn; }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public List<CustomNpcsPermissions.Permission> getPermission() { return null; }

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
        BorderController.getInstance().loadRegion(data);
        CustomNpcs.debugData.end("Packets");
    }

}