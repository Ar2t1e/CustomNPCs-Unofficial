package noppes.npcs.packets.client;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.gui.INpcMenuGui;
import noppes.npcs.shared.common.PacketBasic;

public class PacketPermissionMenu extends PacketBasic {

    protected static int channelId;
    private final boolean display;
    private final boolean stats;
    private final boolean ai;
    private final boolean inventory;
    private final boolean advanced;

    public PacketPermissionMenu(boolean displayIn, boolean statsIn, boolean aiIn, boolean inventoryIn, boolean advancedIn) {
        display = displayIn;
        stats = statsIn;
        ai = aiIn;
        inventory = inventoryIn;
        advanced = advancedIn;
    }

    public static void encode(PacketPermissionMenu msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.display);
        buf.writeBoolean(msg.stats);
        buf.writeBoolean(msg.ai);
        buf.writeBoolean(msg.inventory);
        buf.writeBoolean(msg.advanced);
    }

    public static PacketPermissionMenu decode(FriendlyByteBuf buf) {
        return new PacketPermissionMenu(buf.readBoolean(), buf.readBoolean(), buf.readBoolean(), buf.readBoolean(), buf.readBoolean());
    }

    @Override
    public int getChannelId() { return channelId; }

    @OnlyIn(Dist.CLIENT)
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        if (Minecraft.getInstance().screen instanceof INpcMenuGui gui) { gui.setMenuData(display, stats, ai, inventory, advanced); }
        CustomNpcs.debugData.end("Packets");
    }

}