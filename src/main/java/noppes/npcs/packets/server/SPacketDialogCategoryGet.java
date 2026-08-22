package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.data.DialogCategory;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketSync;

import java.util.Collections;
import java.util.List;

public class SPacketDialogCategoryGet extends PacketServerBasic {

    protected static int channelId;

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public boolean toolAllowed(ItemStack item) { return item.getItem() == CustomItems.wand; }

    @Override
    public List<CustomNpcsPermissions.Permission> getPermission() { return Collections.singletonList(CustomNpcsPermissions.GLOBAL_DIALOG); }

    @Override
    public void encode(FriendlyByteBuf buf) { }

    @Override
    public void decode(FriendlyByteBuf buf) { }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        for (DialogCategory category : DialogController.instance.categories.values()) {
            Packets.send(player, new PacketSync(5, category.save(new NBTTagCompound()), false));
        }
        Packets.send(player, new PacketSync(5, new NBTTagCompound(), true));
        CustomNpcs.debugData.end("Packets");
    }

}
