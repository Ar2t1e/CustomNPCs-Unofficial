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
    private NBTTagCompound compound;

    public SPacketDropTemplateSave() { }

    public SPacketDropTemplateSave(NBTTagCompound compoundIn) { compound = compoundIn; }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public List<CustomNpcsPermissions.Permission> getPermission() { return Collections.singletonList(CustomNpcsPermissions.NPC_INVENTORY); }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public void encode(FriendlyByteBuf buf) { buf.writeNbt(compound); }

    @Override
    public void decode(FriendlyByteBuf buf) { compound = buf.readNbt(); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        if (!compound.hasKey("Name", 8) || !compound.hasKey("Groups", 10)) { return; }
        DropController.getInstance().templates.put(compound.getString("Name"), new DropsTemplate(compound.getCompoundTag("Groups")));
        CustomNpcs.debugData.end("Packets");
    }

}
