package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.BorderController;
import noppes.npcs.items.ItemBoundary;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.Collections;
import java.util.List;

public class SPacketRegionRemove extends PacketServerBasic {

    protected static int channelId;
    private final int regionID;

    public SPacketRegionRemove(int regionIDIn) { regionID = regionIDIn; }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public List<PermissionNode<Boolean>> getPermission() { return Collections.singletonList(CustomNpcsPermissions.TOOL_BUILDERS); }

    public boolean toolAllowed(ItemStack item) { return true; }

    public static void encode(SPacketRegionRemove msg, FriendlyByteBuf buf) { buf.writeInt(msg.regionID); }

    public static SPacketRegionRemove decode(FriendlyByteBuf buf) { return new SPacketRegionRemove(buf.readInt()); }

    @Override
    public int getChannelId() { return channelId; }

    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        if (BorderController.getInstance().regions.containsKey(regionID)) {
            if (!player.getMainHandItem().isEmpty() && player.getMainHandItem().getItem() instanceof ItemBoundary) {
                CompoundTag compound = player.getMainHandItem().getOrCreateTag();
                compound.remove("RegionID");
            }
            if (BorderController.getInstance().removeRegion(regionID)) {
                for (ServerPlayer p : CustomNpcs.Server.getPlayerList().getPlayers()) { BorderController.getInstance().sendTo(p); }
            }
        }
        else { BorderController.getInstance().sendTo(player); }
        CustomNpcs.debugData.end("Packets");
    }

}