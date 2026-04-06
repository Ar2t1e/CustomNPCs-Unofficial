package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
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
    public boolean toolAllowed(ItemStack item){ return true; }

    @Override
    public CustomNpcsPermissions.Permission getPermission() { return CustomNpcsPermissions.NPC_STATS; }

    @Override
    public void encode(FriendlyByteBuf buf) { }

    @Override
    public void decode(FriendlyByteBuf buf) { }

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