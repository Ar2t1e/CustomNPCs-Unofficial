package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SPacketDeadLootsGet extends PacketServerBasic {

    protected static int channelId;

    public SPacketDeadLootsGet() {  }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public List<PermissionNode<Boolean>> getPermission() { return null; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    public static void encode(SPacketDeadLootsGet ignoredMsg, FriendlyByteBuf ignoredBuf) {  }

    public static SPacketDeadLootsGet decode(FriendlyByteBuf ignoredBuf) { return new SPacketDeadLootsGet(); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        List<String> list = new ArrayList<>();
        if (player.isCreative() && npc != null && !npc.isAlive() && npc.inventory.deadLoots != null) {
            for (LivingEntity e : npc.inventory.deadLoots.keySet()) {
                String name = e.getName().getString();
                if (!(e instanceof Player)) { name = ((char) 167) + "7Mob: " + e.getName(); }
                list.add(name);
            }
            Collections.sort(list);
        }
        NoppesUtilServer.sendScrollData(player, list);
        CustomNpcs.debugData.end("Packets");
    }

}