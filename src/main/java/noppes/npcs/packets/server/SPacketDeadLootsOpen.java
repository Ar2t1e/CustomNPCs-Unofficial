package noppes.npcs.packets.server;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.CustomNpcs;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiClose;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketDeadLootsOpen extends PacketServerBasic {

    protected static int channelId;
    private String name;

    public SPacketDeadLootsOpen() { }

    public SPacketDeadLootsOpen(String nameIn) { name = nameIn; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public void encode(FriendlyByteBuf buf) { buf.writeUtf(name); }

    @Override
    public void decode(FriendlyByteBuf buf) { name = buf.readUtf(); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        if (!name.isEmpty() && player.isCreative() && npc != null && !npc.isEntityAlive() && npc.inventory.deadLoots != null) {
            int i = 0;
            int size = 9;
            for (EntityLivingBase e : npc.inventory.deadLoots.keySet()) {
                String n = e.getName();
                if (!(e instanceof EntityPlayer)) { n = "Mob: " + e.getName(); }
                if (n.equals(name)) {
                    size = npc.inventory.deadLoots.get(e).getSizeInventory();
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