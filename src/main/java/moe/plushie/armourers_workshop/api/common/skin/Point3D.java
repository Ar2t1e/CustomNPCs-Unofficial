package moe.plushie.armourers_workshop.api.common.skin;

import moe.plushie.armourers_workshop.api.common.IPoint3D;

public class Point3D implements IPoint3D {

	private final int x;
	private final int y;
	private final int z;

	public Point3D(int xIn, int yIn, int zIn) {
		x = xIn;
		y = yIn;
		z = zIn;
	}

	@Override
	public int getX() { return x; }

	@Override
	public int getY() { return y; }

	@Override
	public int getZ() { return z; }

	@Override
	public String toString() { return "Point3D [x=" + x + ", y=" + y + ", z=" + z + "]"; }

}
