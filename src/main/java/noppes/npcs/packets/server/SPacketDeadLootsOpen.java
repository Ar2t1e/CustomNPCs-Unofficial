package noppes.npcs.packets.server;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.CustomNpcs;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiClose;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketDeadLootsOpen extends PacketServerBasic {

    protected static int channelId;
    private final String name;

    public SPacketDeadLootsOpen(String nameIn) { name = nameIn; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    public static void encode(SPacketDeadLootsOpen msg, FriendlyByteBuf buf) { buf.writeUtf(msg.name); }

    public static SPacketDeadLootsOpen decode(FriendlyByteBuf buf) { return new SPacketDeadLootsOpen(buf.readUtf()); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        if (!name.isEmpty() && player.isCreative() && npc != null && !npc.isAlive() && npc.inventory.deadLoots != null) {
            int i = 0;
            int size = 9;
            for (LivingEntity e : npc.inventory.deadLoots.keySet()) {
                String n = e.getName().getString();
                if (!(e instanceof Player)) { n = "Mob: " + e.getName(); }
                if (n.equals(name)) {
                    size = npc.inventory.deadLoots.get(e).getContainerSize();
                    break;
                }
                i++;
            }
            SPacketGuiOpen.sendOpenGui(player, EnumGuiType.DeadInventory, npc, new BlockPos(size, i, 0));
        }
        else { Packets.send(player, new PacketGuiClose()); }
        CustomNpcs.debugData.end("Packets");
    }

}