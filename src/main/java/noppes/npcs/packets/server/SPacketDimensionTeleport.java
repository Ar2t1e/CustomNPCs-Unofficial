package noppes.npcs.packets.server;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.CustomTeleporter;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.List;
import java.util.Objects;

public class SPacketDimensionTeleport extends PacketServerBasic {

   protected static int channelId;
   private int id;

   public SPacketDimensionTeleport() { }

   public SPacketDimensionTeleport(int idIn) { id = idIn; }

   @Override
   public boolean requiresNpc() { return false; }

   @Override
   public List<CustomNpcsPermissions.Permission> getPermission() { return null; }

   @Override
   public boolean toolAllowed(ItemStack item) { return item.getItem() == CustomItems.teleporter; }

   @Override
   public void encode(FriendlyByteBuf buf) { buf.writeInt(id);}

   @Override
   public void decode(FriendlyByteBuf buf) { id = buf.readInt(); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      WorldServer world = Objects.requireNonNull(player.getServer()).getWorld(id);
      BlockPos coords = world.getSpawnCoordinate();
      if (coords == null) {
         coords = world.getSpawnPoint();
         if (!world.isAirBlock(coords)) { coords = world.getTopSolidOrLiquidBlock(coords); }
         else {
            while (world.isAirBlock(coords) && coords.getY() > 0) { coords = coords.down(); }
            if (coords.getY() == 0) { coords = world.getTopSolidOrLiquidBlock(coords); }
         }
      }
      teleportPlayer(player, id, coords.getX(), coords.getY(), coords.getZ(), player.rotationYaw, player.rotationPitch);
      CustomNpcs.debugData.end("Packets");
   }

   @SuppressWarnings("ConstantConditions")
   public static void teleportPlayer(EntityPlayerMP player, int dimension,
                                     double x, double y, double z,
                                     float yaw, float pitch) {
      if (player.dimension != dimension) {
         MinecraftServer server = player.getServer();
         if (server != null) {
            WorldServer world = server.getWorld(dimension);
            if (world != null) {
               net.minecraftforge.common.ForgeHooks.onTravelToDimension(player, dimension);
               player.setLocationAndAngles(x, y, z, yaw, pitch);
               server.getPlayerList().transferPlayerToDimension(player, dimension, new CustomTeleporter(world));
               player.connection.setPlayerLocation(x, y, z, yaw, pitch);
               if (!world.playerEntities.contains(player)) { world.spawnEntity(player); }
            }
         }
      }
      else { player.connection.setPlayerLocation(x, y, z, yaw, pitch); }
      player.world.updateEntityWithOptionalForce(player, false);
   }

}
