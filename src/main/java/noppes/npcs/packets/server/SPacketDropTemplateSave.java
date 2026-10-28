package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.DropController;
import noppes.npcs.controllers.data.DropsTemplate;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.Collections;
import java.util.List;

public class SPacketDropTemplateSave extends PacketServerBasic {

    protected static int channelId;
    private String name;
    private NBTTagCompound compound;

    public SPacketDropTemplateSave() { }

    public SPacketDropTemplateSave(String nameIn, NBTTagCompound compoundIn) {
        name = nameIn;
        compound = compoundIn;
    }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public List<CustomNpcsPermissions.Permission> getPermission() { return Collections.singletonList(CustomNpcsPermissions.NPC_INVENTORY); }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(name);
        buf.writeNbt(compound);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        name = buf.readUtf();
        compound = buf.readNbt();
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        if (compound.hasKey("Name", 8) && compound.hasKey("Groups", 10)) {
            DropController dData = DropController.getInstance();
            dData.templates.put(name, new DropsTemplate(compound));
            dData.save();
            dData.sendTo(player);
        }
        CustomNpcs.debugData.end("Packets");
    }

}
