package noppes.npcs.packets.server;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketNpcRarityTitleSet;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.List;

public class SPacketNpcRarityTitleGet extends PacketServerBasic {

    protected static int channelId;
    private int npcId;

    public SPacketNpcRarityTitleGet() { }

    public SPacketNpcRarityTitleGet(int npcIdIn) { npcId = npcIdIn; }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public List<CustomNpcsPermissions.Permission> getPermission() { return null; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public void encode(FriendlyByteBuf buf) { buf.writeInt(npcId); }

    @Override
    public void decode(FriendlyByteBuf buf) { npcId = buf.readInt(); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        Entity e = player.world.getEntityByID(npcId);
        if (e instanceof EntityNPCInterface) {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setInteger("NPCLevel", ((EntityNPCInterface) e).stats.getLevel());
            compound.setInteger("NPCRarity", ((EntityNPCInterface) e).stats.getRarity());
            compound.setString("RarityTitle", ((EntityNPCInterface) e).stats.getRarityTitle());
            Packets.send(player, new PacketNpcRarityTitleSet(npcId, compound));
        }
        CustomNpcs.debugData.end("Packets");
    }

}