package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketSync;

import java.util.Collections;
import java.util.List;

public class SPacketDialogGuiSettings extends PacketServerBasic {

    protected static int channelId;
    private NBTTagCompound compound;

    public SPacketDialogGuiSettings() { }

    public SPacketDialogGuiSettings(NBTTagCompound compoundIn) { compound = compoundIn; }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public boolean toolAllowed(ItemStack item){ return true; }

    @Override
    public List<CustomNpcsPermissions.Permission> getPermission() { return Collections.singletonList(CustomNpcsPermissions.GLOBAL_DIALOG); }

    @Override
    public void encode(FriendlyByteBuf buf) { buf.writeNbt(compound); }

    @Override
    public void decode(FriendlyByteBuf buf) { compound = buf.readNbt(); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        DialogController.instance.getGuiSettings().load(compound);
        DialogController.instance.saveSettings();
        Packets.sendAll(new PacketSync(10, compound, true));
        CustomNpcs.debugData.end("Packets");
    }

}
