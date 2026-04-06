package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.data.DialogCategory;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketSync;

public class SPacketDialogCategoryGet extends PacketServerBasic {

    protected static int channelId;

    @Override
    public PermissionNode<Boolean> getPermission() { return CustomNpcsPermissions.GLOBAL_DIALOG; }

    public static void encode(SPacketDialogCategoryGet ignoredMsg, FriendlyByteBuf ignoredBuf) { }

    public static SPacketDialogCategoryGet decode(FriendlyByteBuf ignoredBuf) { return new SPacketDialogCategoryGet(); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        for (DialogCategory category : DialogController.instance.categories.values()) {
            Packets.send(player, new PacketSync(5, category.save(new CompoundTag()), false));
        }
        Packets.send(player, new PacketSync(5, new CompoundTag(), true));
        CustomNpcs.debugData.end("Packets");
    }

}
