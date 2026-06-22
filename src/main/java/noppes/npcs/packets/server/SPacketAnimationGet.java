package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.controllers.AnimationController;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.List;

public class SPacketAnimationGet extends PacketServerBasic {

    protected static int channelId;
    private final int npcId;

    public SPacketAnimationGet(int npcIdIn) { npcId = npcIdIn; }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public boolean toolAllowed(ItemStack item) { return item.getItem() == CustomItems.wand; }

    @Override
    public List<PermissionNode<Boolean>> getPermission() { return null; }

    public static void encode(SPacketAnimationGet msg, FriendlyByteBuf buf) { buf.writeInt(msg.npcId); }

    public static SPacketAnimationGet decode(FriendlyByteBuf buf) { return new SPacketAnimationGet(buf.readInt()); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        Entity e = player.level().getEntity(npcId);
        if (e instanceof EntityNPCInterface) {
            Packets.send(player, new PacketGuiData(npc.animation.save(new CompoundTag())));
            AnimationController.getInstance().sendTo(player);
        }
        CustomNpcs.debugData.end("Packets");
    }

}
