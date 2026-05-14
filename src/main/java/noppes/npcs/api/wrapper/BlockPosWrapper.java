package noppes.npcs.api.wrapper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.Vec3;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.IPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockPosWrapper implements IPos {

   public static final BlockPosWrapper ZERO = new BlockPosWrapper(null, BlockPos.ZERO);
   protected @Nonnull BlockPos blockPos;
   protected final double x;
   protected final double y;
   protected final double z;

   // New from Unofficial (BetaZavr)
   protected @Nullable Level level;

   public BlockPosWrapper(@Nullable Level levelIn, double bx, double by, double bz) {
      level = levelIn;
      if (level == null) {
         if (CustomNpcs.Server != null) { level = CustomNpcs.Server.getLevel(Level.OVERWORLD); }
         else {
            Player player = CustomNpcs.proxy.getPlayer();
            if (player != null) { level = player.level(); }
         }
      }
      x = Math.min(Integer.MAX_VALUE, Math.max(Integer.MIN_VALUE, bx));
      y = Math.min(level == null ? 320 : level.getMaxBuildHeight(), Math.max(level == null ? -64 : level.getMinBuildHeight(), by));
      z = Math.min(Integer.MAX_VALUE, Math.max(Integer.MIN_VALUE, bz));
      blockPos = new BlockPos((int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z));
   }

   public BlockPosWrapper(@Nullable Level level, @Nonnull BlockPos pos) {
      this(level, pos.getX(), pos.getY(), pos.getZ());
      blockPos = pos;
   }

   public BlockPosWrapper(double bx, double by, double bz) { this(null, bx, by, bz); }

   public BlockPosWrapper(@Nonnull BlockPos pos) {
      this(null, pos.getX(), pos.getY(), pos.getZ());
      blockPos = pos;
   }

   @Override
   public double getX() { return x; }

   @Override
   public double getY() { return y; }

   @Override
   public double getZ() { return z; }

   @Override
   public IPos up() { return up(1.0d); }

   @Override
   public IPos up(double n) { return new BlockPosWrapper(level, x, y + n, z); }

   @Override
   public IPos down() { return up(1.0d); }

   @Override
   public IPos down(double n) { return new BlockPosWrapper(level, x, y - n, z); }

   @Override
   public IPos north() { return north(1.0d); }

   @Override
   public IPos north(double n) { return new BlockPosWrapper(level, x, y, z - n); }

   @Override
   public IPos east() { return east(1.0d); }

   @Override
   public IPos east(double n) { return new BlockPosWrapper(level, x + n, y, z); }

   @Override
   public IPos south() { return south(1.0d); }

   @Override
   public IPos south(double n) { return new BlockPosWrapper(level, x, y, z + n); }

   @Override
   public IPos west() { return west(1.0d); }

   @Override
   public IPos west(double n) { return new BlockPosWrapper(level, x - n, y, z); }

   @Override
   public IPos add(double bx, double by, double bz) { return new BlockPosWrapper(level, x + bx, y + by, z + bz); }

   @Override
   public IPos add(IPos pos) { return new BlockPosWrapper(level, x + pos.getX(), y + pos.getY(), z + pos.getZ()); }

   @Override
   public IPos subtract(double bx, double by, double bz) { return new BlockPosWrapper(level, x - bx, y - by, z - bz); }

   @Override
   public IPos subtract(IPos pos) { return subtract(pos.getX(), pos.getY(), pos.getZ()); }

   @Override
   public IPos offset(int direction) { return offset(direction, 1.0d); }

   @Override
   public IPos offset(int direction, double n) {
      if (n == 0) { return this; }
      Direction d = Direction.from3DDataValue(direction);
      return new BlockPosWrapper(level, x + d.getStepX() * n, y + d.getStepY() * n, z + d.getStepZ() * n);
   }

   @Override
   public IPos offset(double xIn, double yIn, double zIn) { return new BlockPosWrapper(level, x + xIn, y + yIn, z + zIn); }

   @Override
   public BlockPos getMCBlockPos() {
      return blockPos;
   }

   @Override
   public double[] normalize() {
      double d = Math.sqrt(Math.pow(x, 2.0) + Math.pow(y, 2.0) + Math.pow(z, 2.0));
      return new double[] {x / d, y / d, z / d };
   }

   @Override
   public double distanceTo(IPos pos) {
      return distanceTo(pos.getX(), pos.getY(), pos.getZ());
   }

   @Override
   public double distanceTo(double x, double y, double z) {
      double d0 = this.x - x;
      double d1 = this.y - y;
      double d2 = this.z - z;
      return Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
   }

   // New from Unofficial (BetaZavr)
   @Override
   public Vec3 getMCVec3() { return new Vec3(x, y, z); }

   @Override
   public IPos rotate(int rotation) {
      return switch (rotation) {
         case 1 -> new BlockPosWrapper(level, z, y, -x);
         case 2 -> new BlockPosWrapper(level, -x, y, -z);
         case 3 -> new BlockPosWrapper(level, -z, y, x);
         default -> this;
      };
   }

   @Override
   public String toString() { return "BlockPosWrapper {pos: [" + x + ", " + y + ", " + z + "]; mcPos: [" + blockPos.getX() + ", " + blockPos.getY() + ", " + blockPos.getZ() + "]}"; }

   public IPos rotate(Rotation rotation) {
       return switch (rotation) {
           case CLOCKWISE_90 -> new BlockPosWrapper(level, -z, y, x);
           case CLOCKWISE_180 -> new BlockPosWrapper(level, -x, y, -z);
           case COUNTERCLOCKWISE_90 -> new BlockPosWrapper(level, z, y, -x);
           default -> this;
       };
   }

   public IPos rotate(Direction direction) {
      return switch (direction) {
         case NORTH -> new BlockPosWrapper(level, -x, y, -z);
         case WEST -> new BlockPosWrapper(level, -z, y, x);
         case EAST -> new BlockPosWrapper(level, z, y, -x);
         default -> this; // SOUTH
      };
   }

}
