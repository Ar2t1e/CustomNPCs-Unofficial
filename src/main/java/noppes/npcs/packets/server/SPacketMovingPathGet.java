package noppes.npcs.packets.server;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.constants.EnumMenuType;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketMenuSave;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.List;

public class SPacketMovingPathGet extends PacketServerBasic {

    protected static int channelId;
    private int npcId;

    public SPacketMovingPathGet() { }

    public SPacketMovingPathGet(int npcIdIn) { npcId = npcIdIn; }

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
    public void handle() {
        CustomNpcs.debugData.start("Packets");
        Entity entity = player.world.getEntityByID(npcId);
        if (entity instanceof EntityNPCInterface) {
            Packets.send(player, new PacketMenuSave((EntityNPCInterface) entity, EnumMenuType.AI));
        }
        CustomNpcs.debugData.end("Packets");
    }

}