package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.DropController;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketDropTemplateRemove extends PacketServerBasic {

    protected static int channelId;
    private String name;

    public SPacketDropTemplateRemove() { }

    public SPacketDropTemplateRemove(String nameIn) { name = nameIn; }

    @Override
    public CustomNpcsPermissions.Permission getPermission() { return CustomNpcsPermissions.NPC_INVENTORY; }

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
        if (!name.isEmpty()) { DropController.getInstance().templates.remove(name); }
        CustomNpcs.debugData.end("Packets");
    }

}
