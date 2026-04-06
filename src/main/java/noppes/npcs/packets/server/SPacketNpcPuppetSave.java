package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.Objects;
import java.util.Set;

public class SPacketNpcPuppetSave extends PacketServerBasic {

    protected static int channelId;
    private final CompoundTag data;

    public SPacketNpcPuppetSave(CompoundTag dataIn) { data = dataIn; }

    @Override
    public boolean requiresNpc() { return true; }

    @Override
    public PermissionNode<Boolean> getPermission() { return CustomNpcsPermissions.NPC_ADVANCED; }

    public static void encode(SPacketNpcPuppetSave msg, FriendlyByteBuf buf) { buf.writeNbt(msg.data); }

    public static SPacketNpcPuppetSave decode(FriendlyByteBuf buf) { return new SPacketNpcPuppetSave(buf.readAnySizeNbt()); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        CompoundTag original = npc.puppet.save(new CompoundTag());
        Set<String> names = data.getAllKeys();
        for (String name : names) { original.put(name, Objects.requireNonNull(data.get(name))); }
        npc.puppet.load(original);
        npc.updateClient = true;
        CustomNpcs.debugData.end("Packets");
    }

}