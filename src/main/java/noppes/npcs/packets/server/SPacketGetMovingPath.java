package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.constants.EnumMenuType;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketMenuSave;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.List;

public class SPacketGetMovingPath extends PacketServerBasic {

    protected static int channelId;
    private final int npcId;

    public SPacketGetMovingPath(int npcIdIn) { npcId = npcIdIn; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public List<PermissionNode<Boolean>> getPermission() { return null; }

    public static void encode(SPacketGetMovingPath msg, FriendlyByteBuf buf) { buf.writeInt(msg.npcId); }

    public static SPacketGetMovingPath decode(FriendlyByteBuf buf) { return new SPacketGetMovingPath(buf.readInt()); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        Entity entity = player.level().getEntity(npcId);
        if (entity instanceof EntityCustomNpc npcIn) { Packets.send(player, new PacketMenuSave(npcIn, EnumMenuType.MOVING_PATH)); }
        CustomNpcs.debugData.end("Packets");
    }

}
