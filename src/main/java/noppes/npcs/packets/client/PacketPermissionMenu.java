package noppes.npcs.packets.client;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.gui.INpcMenuGui;
import noppes.npcs.shared.common.PacketBasic;

public class PacketPermissionMenu extends PacketBasic {

    protected static int channelId;
    private boolean display;
    private boolean stats;
    private boolean ai;
    private boolean inventory;
    private boolean advanced;

    public PacketPermissionMenu() { }

    public PacketPermissionMenu(boolean displayIn, boolean statsIn, boolean aiIn, boolean inventoryIn, boolean advancedIn) {
        display = displayIn;
        stats = statsIn;
        ai = aiIn;
        inventory = inventoryIn;
        advanced = advancedIn;
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        display = buf.readBoolean();
        stats = buf.readBoolean();
        ai = buf.readBoolean();
        inventory = buf.readBoolean();
        advanced = buf.readBoolean();
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(display);
        buf.writeBoolean(stats);
        buf.writeBoolean(ai);
        buf.writeBoolean(inventory);
        buf.writeBoolean(advanced);
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        if (Minecraft.getMinecraft().currentScreen instanceof INpcMenuGui) {
            ((INpcMenuGui) Minecraft.getMinecraft().currentScreen).setMenuData(display, stats, ai, inventory, advanced);
        }
        CustomNpcs.debugData.end("Packets");
    }

}
