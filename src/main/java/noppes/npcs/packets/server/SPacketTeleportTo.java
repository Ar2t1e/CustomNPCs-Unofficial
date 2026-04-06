package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import noppes.npcs.CustomNpcs;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketTeleportTo extends PacketServerBasic {

    protected static int channelId;
    private int dimensionId;
    private BlockPos pos;

    public SPacketTeleportTo() { }

    public SPacketTeleportTo(int dimensionIdIn, BlockPos posIn) {
        dimensionId = dimensionIdIn;
        pos = posIn;
    }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(dimensionId);
        buf.writeBlockPos(pos);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        dimensionId = buf.readInt();
        pos = buf.readBlockPos();
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        if (player.getServer() != null) {
            WorldServer world = player.getServer().getWorld(dimensionId);
            if (world == null) { world = (WorldServer) player.world; }
            BlockPos coords = new BlockPos(pos);
            if (coords.equals(BlockPos.ORIGIN)) {
                coords = world.getSpawnPoint();
                if (!world.isAirBlock(coords)) { coords = world.getTopSolidOrLiquidBlock(coords); }
                else {
                    while (world.isAirBlock(coords) && coords.getY() > 0) { coords = coords.down(); }
                    if (coords.getY() == 0) { coords = world.getTopSolidOrLiquidBlock(coords); }
                }
            }
            SPacketDimensionTeleport.teleportPlayer(player, dimensionId, coords.getX(), coords.getY(), coords.getZ(), player.rotationYaw, player.rotationPitch);
        }
        CustomNpcs.debugData.end("Packets");
    }
}