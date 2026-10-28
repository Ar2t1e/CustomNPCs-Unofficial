package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.DropController;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.Collections;
import java.util.List;

public class SPacketDropTemplateRename extends PacketServerBasic {

    protected static int channelId;
    private String oldName;
    private String name;

    public SPacketDropTemplateRename() { }

    public SPacketDropTemplateRename(String oldNameIn, String nameIn) {
        oldName = oldNameIn;
        name = nameIn;
    }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public List<CustomNpcsPermissions.Permission> getPermission() { return Collections.singletonList(CustomNpcsPermissions.GLOBAL_DROP_TEMPLATE); }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(oldName);
        buf.writeUtf(name);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        oldName = buf.readUtf();
        name = buf.readUtf();
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        if (!name.isEmpty() && !oldName.isEmpty() && !name.equals(oldName)) {
            DropController dData = DropController.getInstance();
            while (dData.templates.containsKey(name)) { name += "_"; }
            dData.templates.put(name, dData.templates.get(oldName));
            dData.templates.remove(oldName);
            dData.save();
            dData.sendTo(player);
        }
        CustomNpcs.debugData.end("Packets");
    }

}
