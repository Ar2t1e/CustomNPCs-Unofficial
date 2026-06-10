package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.DropController;
import noppes.npcs.controllers.data.DropsTemplate;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.Collections;
import java.util.List;

public class SPacketDropTemplateSave extends PacketServerBasic {

    protected static int channelId;
    private final CompoundTag compound;

    public SPacketDropTemplateSave(CompoundTag compoundIn) { compound = compoundIn; }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public List<PermissionNode<Boolean>> getPermission() { return Collections.singletonList(CustomNpcsPermissions.NPC_INVENTORY); }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    public static void encode(SPacketDropTemplateSave msg, FriendlyByteBuf buf) { buf.writeNbt(msg.compound); }

    public static SPacketDropTemplateSave decode(FriendlyByteBuf buf) { return new SPacketDropTemplateSave(buf.readAnySizeNbt()); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        if (!compound.contains("Name", 8) || !compound.contains("Groups", 10)) { return; }
        DropController.getInstance().templates.put(compound.getString("Name"), new DropsTemplate(compound.getCompound("Groups")));
        CustomNpcs.debugData.end("Packets");
    }

}
