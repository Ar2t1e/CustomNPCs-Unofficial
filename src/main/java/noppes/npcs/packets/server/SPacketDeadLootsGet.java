package noppes.npcs.packets.server;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SPacketDeadLootsGet extends PacketServerBasic {

    protected static int channelId;

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public void encode(FriendlyByteBuf buf) { }

    @Override
    public void decode(FriendlyByteBuf buf) { }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        List<String> list = new ArrayList<>();
        if (player.isCreative() && npc != null && !npc.isEntityAlive() && npc.inventory.deadLoots != null) {
            for (EntityLivingBase e : npc.inventory.deadLoots.keySet()) {
                String name = e.getName();
                if (!(e instanceof EntityPlayer)) { name = ((char) 167) + "7Mob: " + e.getName(); }
                list.add(name);
            }
            Collections.sort(list);
        }
        NoppesUtilServer.sendScrollData(player, list);
        CustomNpcs.debugData.end("Packets");
    }

}