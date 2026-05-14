package noppes.npcs.api.wrapper;

import net.minecraft.client.renderer.EnumFaceDirection;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.IPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockPosWrapper implements IPos {

	public static final BlockPosWrapper ORIGIN = new BlockPosWrapper(null, BlockPos.ORIGIN);
	protected @Nonnull BlockPos blockPos;
	protected final double x;
	protected final double y;
	protected final double z;
	protected @Nullable World world;

	public BlockPosWrapper(@Nullable World worldIn, double bx, double by, double bz) {
		world = worldIn;
		if (world == null) {
			if (CustomNpcs.Server != null) { world = CustomNpcs.Server.getWorld(0); }
			else {
				EntityPlayer player = CustomNpcs.proxy.getPlayer();
				if (player != null) { world = player.world; }
			}
		}
		x = Math.min(Integer.MAX_VALUE, Math.max(Integer.MIN_VALUE, bx));
		y = Math.min(255, Math.max(0, by));
		z = Math.min(Integer.MAX_VALUE, Math.max(Integer.MIN_VALUE, bz));
		blockPos = new BlockPos((int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z));
	}

	public BlockPosWrapper(@Nullable World world, @Nonnull BlockPos pos) {
		this(world, pos.getX(), pos.getY(), pos.getZ());
		blockPos = pos;
	}

	public BlockPosWrapper(double bx, double by, double bz) { this(null, bx, by, bz); }

	public BlockPosWrapper(@Nonnull BlockPos pos) {
		this(null, pos.getX(), pos.getY(), pos.getZ());
		blockPos = pos;
	}

	@Override
	public IPos add(double bx, double by, double bz) { return new BlockPosWrapper(world, x + bx, y + by, z + bz); }

	@Override
	public IPos add(IPos pos) { return new BlockPosWrapper(world, x + pos.getX(), y + pos.getY(), z + pos.getZ()); }

	@Override
	public double distanceTo(double bx, double by, double bz) {
		double d0 = x - bx;
		double d2 = y - by;
		double d3 = z - bz;
		return Math.sqrt(d0 * d0 + d2 * d2 + d3 * d3);
	}

	@Override
	public double distanceTo(IPos pos) { return distanceTo(pos.getX(), pos.getX(), pos.getX()); }

	@Override
	public IPos down() { return down(1.0d); }

	@Override
	public IPos down(double n) { return new BlockPosWrapper(world, x, y - n, z); }

	@Override
	public IPos east() { return east(1.0d); }

	@Override
	public IPos east(double n) { return new BlockPosWrapper(world, x + n, y, z); }

	@Override
	public BlockPos getMCBlockPos() { return this.blockPos; }

	@Override
	public double getX() { return x; }

	@Override
	public double getY() { return y; }

	@Override
	public double getZ() { return z; }

	@Override
	public double[] normalize() {
		double d = Math.sqrt(Math.pow(x, 2.0) + Math.pow(y, 2.0) + Math.pow(z, 2.0));
		return new double[] {x / d, y / d, z / d };
	}

	@Override
	public IPos north() { return north(1); }

	@Override
	public IPos north(double n) { return new BlockPosWrapper(world, x, y, z - n); }

	@Override
	public IPos offset(int direction) { return offset(direction, 1.0d); }

	@Override
	public IPos offset(int direction, double n) {
		if (n == 0) { return this; }
		EnumFacing d = EnumFacing.getHorizontal(direction);
		return new BlockPosWrapper(world, x + d.getFrontOffsetX() * n, y + d.getFrontOffsetY() * n, z + d.getFrontOffsetZ() * n);
	}

	@Override
	public IPos offset(double xIn, double yIn, double zIn) { return new BlockPosWrapper(world, x + xIn, y + yIn, z + zIn); }

	@Override
	public IPos south() { return south(1.0); }

	@Override
	public IPos south(double n) { return new BlockPosWrapper(world, x, y, z + n); }

	@Override
	public IPos subtract(double bx, double by, double bz) { return new BlockPosWrapper(world, x - bx, y - by, z - bz); }

	@Override
	public IPos subtract(IPos pos) { return subtract(pos.getX(), pos.getY(), pos.getZ()); }

	@Override
	public IPos up() { return up(1.0); }

	@Override
	public IPos up(double n) { return new BlockPosWrapper(world, x, y + n, z); }

	@Override
	public IPos west() { return west(1.0); }

	@Override
	public IPos west(double n) { return new BlockPosWrapper(world, x - n, y, z); }

	// New from Unofficial (BetaZavr)

	// New from Unofficial (BetaZavr)
	@Override
	public Vec3d getMCVec3() { return new Vec3d(x, y, z); }

	@Override
	public IPos rotate(int rotation) {
		switch (rotation) {
			case 1: return new BlockPosWrapper(world, z, y, -x);
			case 2: return new BlockPosWrapper(world, -x, y, -z);
			case 3: return new BlockPosWrapper(world, -z, y, x);
			default: return this;
		}
	}

	public IPos rotate(Rotation rotation) {
		switch (rotation) {
			case CLOCKWISE_90: return new BlockPosWrapper(world, -z, y, x);
			case CLOCKWISE_180: return new BlockPosWrapper(world, -x, y, -z);
			case COUNTERCLOCKWISE_90: return new BlockPosWrapper(world, z, y, -x);
			default: return this;
		}
	}

	public IPos rotate(EnumFaceDirection direction) {
		switch (direction) {
			case NORTH: return new BlockPosWrapper(world, -x, y, -z);
			case WEST: return new BlockPosWrapper(world, -z, y, x);
			case EAST: return new BlockPosWrapper(world, z, y, -x);
			default: return this;
		}
	}

	public IPos rotate(EnumFacing direction) {
		switch (direction) {
			case NORTH: return new BlockPosWrapper(world, -x, y, -z);
			case WEST: return new BlockPosWrapper(world, -z, y, x);
			case EAST: return new BlockPosWrapper(world, z, y, -x);
			default: return this;
		}
	}

	@Override
	public String toString() { return "BlockPosWrapper {pos: [" + x + ", " + y + ", " + z + "]; mcPos: [" + blockPos.getX() + ", " + blockPos.getY() + ", " + blockPos.getZ() + "]}"; }

}
