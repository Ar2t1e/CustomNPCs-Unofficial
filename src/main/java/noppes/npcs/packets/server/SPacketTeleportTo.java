package noppes.npcs.packets.server;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.api.item.INPCToolItem;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.List;

public class SPacketTeleportTo extends PacketServerBasic {

    protected static int channelId;
    private final ResourceKey<Level> dimension;
    private final BlockPos pos;

    public SPacketTeleportTo(ResourceKey<Level> dimensionIn, BlockPos posIn) {
        dimension = dimensionIn;
        pos = posIn;
    }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public boolean toolAllowed(ItemStack item) { return item.getItem() instanceof INPCToolItem; }

    @Override
    public List<PermissionNode<Boolean>> getPermission() {
        return List.of(CustomNpcsPermissions.TOOL_BUILDERS,
                CustomNpcsPermissions.GLOBAL_TRANSPORT,
                CustomNpcsPermissions.GLOBAL_QUEST);
    }

    public static void encode(SPacketTeleportTo msg, FriendlyByteBuf buf) {
        buf.writeResourceKey(msg.dimension);
        buf.writeBlockPos(msg.pos);
    }

    public static SPacketTeleportTo decode(FriendlyByteBuf buf) {
        return new SPacketTeleportTo(buf.readResourceKey(Registries.DIMENSION), buf.readBlockPos());
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        if (player.getServer() != null) {
            ServerLevel level = player.getServer().getLevel(dimension);
            if (level == null) { level = (ServerLevel) player.level(); }
            BlockPos coords = new BlockPos(pos);
            if (coords.equals(BlockPos.ZERO)) {
                coords = level.getSharedSpawnPos();
                if (!level.isEmptyBlock(coords)) {
                    coords = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, coords);
                }
                else {
                    while (level.isEmptyBlock(coords) && coords.getY() > 0) { coords = coords.below(); }
                    if (coords.getY() == 0) { coords = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, coords); }
                }
            }
            SPacketDimensionTeleport.teleportPlayer(player, dimension,
                    coords.getX() + 0.5d, coords.getY() + 0.1d, coords.getZ() + 0.5d,
                    player.getYRot(), player.getXRot());
        }
        CustomNpcs.debugData.end("Packets");
    }
}