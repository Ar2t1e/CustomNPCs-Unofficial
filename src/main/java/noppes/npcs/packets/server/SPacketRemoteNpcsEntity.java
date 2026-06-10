package noppes.npcs.packets.server;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketRemoteNpcsEntity;

import java.util.List;

public class SPacketRemoteNpcsEntity extends PacketServerBasic {

    protected static int channelId;
    private int id;

    public SPacketRemoteNpcsEntity() { }

    public SPacketRemoteNpcsEntity(int entityId) { id = entityId; }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public List<CustomNpcsPermissions.Permission> getPermission() { return null; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public void encode(FriendlyByteBuf buf) { buf.writeInt(id); }

    @Override
    public void decode(FriendlyByteBuf buf) { id = buf.readInt(); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        Entity entity = player.world.getEntityByID(id);
        if (entity != null) {
            NBTTagCompound compound = new NBTTagCompound();
            entity.writeToNBT(compound);
            Packets.send(player, new PacketRemoteNpcsEntity(compound));
        }
        CustomNpcs.debugData.start("Packets");
    }

}