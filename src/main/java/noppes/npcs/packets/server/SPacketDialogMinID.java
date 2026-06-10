package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SPacketDialogMinID extends PacketServerBasic {

    protected static int channelId;
    private final int id;

    public SPacketDialogMinID(int idIn) { id = idIn; }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public boolean toolAllowed(ItemStack item){ return true; }

    @Override
    public List<PermissionNode<Boolean>> getPermission() { return Collections.singletonList(CustomNpcsPermissions.GLOBAL_DIALOG); }

    public static void encode(SPacketDialogMinID msg, FriendlyByteBuf buf) { buf.writeInt(msg.id); }

    public static SPacketDialogMinID decode(FriendlyByteBuf buf) { return new SPacketDialogMinID(buf.readInt()); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        List<Integer> ids = new ArrayList<>(DialogController.instance.dialogs.keySet());
        Collections.sort(ids);
        int idNow = 1;
        for (int i : ids) {
            if (idNow == i && idNow != id) {
                idNow++;
                continue;
            }
            break;
        }
        CompoundTag compound = new CompoundTag();
        compound.putInt("MinimumID", id);
        Packets.send(player, new PacketGuiData(compound));
        CustomNpcs.debugData.end("Packets");
    }
}
