package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.PlayerSkinController;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.List;

public class SPacketSkin extends PacketServerBasic {

    protected static int channelId;
    private NBTTagCompound data;

    public SPacketSkin() { }

    public SPacketSkin(NBTTagCompound stackNBT) { data = stackNBT; }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public List<CustomNpcsPermissions.Permission> getPermission() { return null; }

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
        PlayerSkinController.getInstance().loadPlayerSkin(data);
        PlayerSkinController.getInstance().sendToAll(player.getUniqueID());
        CustomNpcs.debugData.end("Packets");
    }

}