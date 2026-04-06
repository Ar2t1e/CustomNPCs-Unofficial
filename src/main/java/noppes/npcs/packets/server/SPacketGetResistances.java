package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.entity.data.Resistances;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.Map;
import java.util.TreeMap;

public class SPacketGetResistances extends PacketServerBasic {

    protected static int channelId;

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public PermissionNode<Boolean> getPermission() { return CustomNpcsPermissions.NPC_STATS; }

    public static void encode(SPacketGetResistances ignoredMsg, FriendlyByteBuf ignoredBuf) { }

    public static SPacketGetResistances decode(FriendlyByteBuf ignoredBuf) { return new SPacketGetResistances(); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        Map<String, Integer> map = new TreeMap<>();
        for (String name : Resistances.allDamageNames) { map.put(name, 0); }
        NoppesUtilServer.sendScrollData(player, map);
        CustomNpcs.debugData.end("Packets");
    }

}